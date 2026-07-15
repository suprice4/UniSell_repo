package edu.cit.capendit.unisell.order.dto;

import edu.cit.capendit.unisell.order.model.PaymentStatus;

public class PaymentStatusUpdateRequest {
    private PaymentStatus paymentStatus;

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
}