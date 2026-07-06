package com.talkeys.shared.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.talkeys.shared.config.ProductionConfig
import com.talkeys.shared.data.payment.Friend
import com.talkeys.shared.data.payment.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentCheckoutViewModel(
    private val repository: PaymentRepository,
    private val isPhonePeProduction: Boolean = ProductionConfig.IS_PHONEPE_PRODUCTION
) : ViewModel() {
    private val logger = Logger.withTag("PaymentCheckout")

    private val _checkoutState = MutableStateFlow(PaymentCheckoutUiState())
    val checkoutState: StateFlow<PaymentCheckoutUiState> = _checkoutState.asStateFlow()

    private val _verificationState = MutableStateFlow(PaymentVerificationUiState())
    val verificationState: StateFlow<PaymentVerificationUiState> = _verificationState.asStateFlow()

    fun startCheckout(
        eventId: String,
        passType: String,
        friends: List<Friend>,
        teamCode: String? = null,
        authToken: String?
    ) {
        startCheckout(
            eventId = eventId,
            passType = passType,
            friends = friends,
            teamCode = teamCode,
            clientPlatform = null,
            authToken = authToken
        )
    }

    fun startCheckout(
        eventId: String,
        passType: String,
        friends: List<Friend>,
        teamCode: String? = null,
        clientPlatform: String? = null,
        authToken: String?
    ) {
        _checkoutState.value = PaymentCheckoutUiState(isLoading = true)
        viewModelScope.launch {
            repository.bookTicket(
                eventId,
                passType,
                friends,
                teamCode,
                authToken
            )
                .onSuccess { paymentOrder ->
                    val checkoutTokenOrUrl = paymentOrder.checkoutTokenOrUrl()
                    if (checkoutTokenOrUrl.isNullOrBlank()) {
                        _checkoutState.value = PaymentCheckoutUiState(
                            errorMessage = "Payment response did not include checkout details. Please try again."
                        )
                        return@onSuccess
                    }

                    val paymentUrl = PhonePeCheckoutUrlBuilder.buildCheckoutUrl(
                        tokenOrUrl = checkoutTokenOrUrl,
                        isProduction = isPhonePeProduction
                    )
                    logger.i {
                        "PhonePe checkout prepared: source=${paymentOrder.checkoutSource()}, host=${paymentUrl.hostForLog()}, isPhonePeProduction=$isPhonePeProduction"
                    }

                    _checkoutState.value = PaymentCheckoutUiState(
                        checkoutData = PaymentCheckoutData(
                            paymentUrl = paymentUrl,
                            merchantOrderId = paymentOrder.merchantOrderId,
                            passId = paymentOrder.passId
                        )
                    )
                }
                .onFailure { error ->
                    _checkoutState.value = PaymentCheckoutUiState(
                        errorMessage = error.message ?: "Failed to create payment"
                    )
                }
        }
    }

    fun verifyPaymentStatus(merchantOrderId: String, authToken: String?) {
        _verificationState.value = PaymentVerificationUiState(isLoading = true)
        viewModelScope.launch {
            repository.verifyPaymentStatus(merchantOrderId, authToken)
                .onSuccess { status ->
                    _verificationState.value = PaymentVerificationUiState(
                        passId = status.passId,
                        passUUID = status.passUUID,
                        status = status.paymentStatus
                    )
                }
                .onFailure { error ->
                    _verificationState.value = PaymentVerificationUiState(
                        errorMessage = error.message ?: "Failed to verify payment"
                    )
                }
        }
    }

    fun clearCheckout() {
        _checkoutState.value = PaymentCheckoutUiState()
    }

    fun clearVerification() {
        _verificationState.value = PaymentVerificationUiState()
    }

    private fun com.talkeys.shared.data.payment.PaymentOrderData.checkoutSource(): String =
        when {
            !paymentUrl.isNullOrBlank() -> "paymentUrl"
            !token.isNullOrBlank() -> "token"
            else -> "missing"
        }

    private fun String.hostForLog(): String {
        if (!startsWith("http://") && !startsWith("https://")) return "token"
        val authorityStart = indexOf("://") + 3
        val authorityEnd = indexOfAny(charArrayOf('/', '?', '#'), startIndex = authorityStart)
            .takeIf { it >= 0 }
            ?: length
        return substring(authorityStart, authorityEnd)
            .substringAfterLast('@')
            .substringBefore(':')
            .ifBlank { "unknown" }
    }
}
