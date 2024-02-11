package com.cockroachlabs.field.paymentsdemo.PaymentsLoadGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardTransaction {

    private UUID transactionId;
    private Instant transactionDate;
    private Double amount;
    private String currency;
    private String cardNumber;
    private String cardNumberEncryptedBase64;
    private String cardExpirationMonth;
    private String cardExpirationYear;
    private String cardHolderName;
    private String merchantCode = "WELSHGOODS";
    private String merchantReferenceCode;
    private String status = "PND";
    private String authorizationCode;
    private String responseHttpCode;
    private String responseMessage;
    private String gatewayCode = "PPL";

    public CardTransaction() {

    }

    public UUID getTransactionId() {
        return this.transactionId;
    }
    public Instant getTransactionDate() {
        return this.transactionDate;
    }
    public Double getAmount() {
        return this.amount;
    }
    public String getCurrency() {
        return this.currency;
    }
    public String getCardNumber() {
        return this.cardNumber;
    }
    public String getCardNumberEncryptedBase64() {
        return this.cardNumberEncryptedBase64;
    }
    public String getCardExpirationMonth() {
        return this.cardExpirationMonth;
    }
    public String getCardExpirationYear() {
        return this.cardExpirationYear;
    }
    public String getCardHolderName() {
        return this.cardHolderName;
    }
    public String getMerchantCode() {
        return this.merchantCode;
    }
    public String getMerchantReferenceCode() {
        return this.merchantReferenceCode;
    }
    public String getStatus() {
        return this.status;
    }
    public String getAuthorizationCode() {
        return this.authorizationCode;
    }
    public String getResponseHttpCode() {
        return this.responseHttpCode;
    }
    public String getResponseMessage() {
        return this.responseMessage;
    }
    public String getGatewayCode() {
        return this.gatewayCode;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }
    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    public void setCardNumberEncryptedBase64(String cardNumberEncryptedBase64) {
        this.cardNumberEncryptedBase64 = cardNumberEncryptedBase64;
    }
    public void setCardExpirationMonth(String cardExpirationMonth) {
        this.cardExpirationMonth = cardExpirationMonth;
    }
    public void setCardExpirationYear(String cardExpirationYear) {
        this.cardExpirationYear = cardExpirationYear;
    }
    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }
    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }
    public void setMerchantReferenceCode(String merchantReferenceCode) {
        this.merchantReferenceCode = merchantReferenceCode;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }
    public void setResponseHttpCode(String responseHttpCode) {
        this.responseHttpCode = responseHttpCode;
    }
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
    public void setGatewayCode(String gatewayCode) {
        this.responseMessage = responseMessage;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof CardTransaction))
            return false;
        CardTransaction cardTransaction = (CardTransaction) o;
        return Objects.equals(this.transactionId, cardTransaction.transactionId)
                && Objects.equals(this.transactionDate, cardTransaction.transactionDate)
                && Objects.equals(this.amount, cardTransaction.amount)
                && Objects.equals(this.currency, cardTransaction.currency)
                && Objects.equals(this.cardNumber, cardTransaction.cardNumber)
                && Objects.equals(this.cardNumberEncryptedBase64, cardTransaction.cardNumberEncryptedBase64)
                && Objects.equals(this.cardExpirationMonth, cardTransaction.cardExpirationMonth)
                && Objects.equals(this.cardExpirationYear, cardTransaction.cardExpirationYear)
                && Objects.equals(this.cardHolderName, cardTransaction.cardHolderName)
                && Objects.equals(this.merchantCode, cardTransaction.merchantCode)
                && Objects.equals(this.merchantReferenceCode, cardTransaction.merchantReferenceCode)
                && Objects.equals(this.status, cardTransaction.status)
                && Objects.equals(this.authorizationCode, cardTransaction.authorizationCode)
                && Objects.equals(this.responseHttpCode, cardTransaction.responseHttpCode)
                && Objects.equals(this.responseMessage, cardTransaction.responseMessage)
                && Objects.equals(this.gatewayCode, cardTransaction.gatewayCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.transactionId,
                this.transactionDate,
                this.amount,
                this.currency,
                this.cardNumber,
                this.cardNumberEncryptedBase64,
                this.cardExpirationMonth,
                this.cardExpirationYear,
                this.cardHolderName,
                this.merchantCode,
                this.merchantReferenceCode,
                this.status,
                this.authorizationCode,
                this.responseHttpCode,
                this.responseMessage,
                this.gatewayCode);
    }

    @Override
    public String toString() {
        return "CardTransaction {"
                + "Transaction ID = " + this.transactionId
                + ", Transaction Date = " + this.transactionDate
                + ", Amount = " + this.amount
                + ", Card Number = '" + this.cardNumber + '\''
                + ", Card Number Encrypted (base64) = '" + this.cardNumberEncryptedBase64 + '\''
                + ", Card Exp Month = '" + this.cardExpirationMonth + '\''
                + ", Card Exp Year = '" + this.cardExpirationYear + '\''
                + ", Card Holder Name = '" + this.cardHolderName + '\''
                + ", Merchant Code = '" + this.merchantCode + '\''
                + ", Merchant Reference Code = '" + this.merchantReferenceCode + '\''
                + ", Status = '" + this.status + '\''
                + ", Authorization = '" + this.authorizationCode + '\''
                + ", Response HTTP Code = '" + this.responseHttpCode + '\''
                + ", Response Message = '" + this.responseMessage + '\''
                + ", Gateway Code = '" + this.gatewayCode + '\''
                + '}';
    }

}
