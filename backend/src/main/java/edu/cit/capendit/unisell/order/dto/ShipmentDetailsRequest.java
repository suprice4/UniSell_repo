package edu.cit.capendit.unisell.order.dto;

public class ShipmentDetailsRequest {
    private String trackingNumber;
    private String courierName;

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getCourierName() { return courierName; }
    public void setCourierName(String courierName) { this.courierName = courierName; }
}