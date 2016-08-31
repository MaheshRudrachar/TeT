package com.teketys.templetickets.utils.ccavenue;

import java.io.Serializable;

/**
 * Created by rudram1 on 8/27/16.
 */
public class PaymentArgs implements Serializable {

    private String billingName;
    private String billingAddress;
    private String billingCity;
    private String billingRegion;
    private String billingPhone;
    private String billingZip;
    private String billingEmail;
    private String billingAmount;
    private String billingOrderId;

    public String getBillingName() {
        return billingName;
    }

    public void setBillingName(String billingName) {
        this.billingName = billingName;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getBillingCity() {
        return billingCity;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public String getBillingRegion() {
        return billingRegion;
    }

    public void setBillingRegion(String billingRegion) {
        this.billingRegion = billingRegion;
    }

    public String getBillingPhone() {
        return billingPhone;
    }

    public void setBillingPhone(String billingPhone) {
        this.billingPhone = billingPhone;
    }

    public String getBillingZip() {
        return billingZip;
    }

    public void setBillingZip(String billingZip) {
        this.billingZip = billingZip;
    }

    public String getBillingEmail() {
        return billingEmail;
    }

    public void setBillingEmail(String billingEmail) {
        this.billingEmail = billingEmail;
    }

    public String getBillingAmount() {
        return billingAmount;
    }

    public void setBillingAmount(String billingAmount) {
        this.billingAmount = billingAmount;
    }

    public String getBillingOrderId() {
        return billingOrderId;
    }

    public void setBillingOrderId(String billingOrderId) {
        this.billingOrderId = billingOrderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentArgs that = (PaymentArgs) o;

        if (getBillingName() != null ? !getBillingName().equals(that.getBillingName()) : that.getBillingName() != null)
            return false;
        if (getBillingAddress() != null ? !getBillingAddress().equals(that.getBillingAddress()) : that.getBillingAddress() != null)
            return false;
        if (getBillingCity() != null ? !getBillingCity().equals(that.getBillingCity()) : that.getBillingCity() != null)
            return false;
        if (getBillingRegion() != null ? !getBillingRegion().equals(that.getBillingRegion()) : that.getBillingRegion() != null)
            return false;
        if (getBillingPhone() != null ? !getBillingPhone().equals(that.getBillingPhone()) : that.getBillingPhone() != null)
            return false;
        if (getBillingZip() != null ? !getBillingZip().equals(that.getBillingZip()) : that.getBillingZip() != null)
            return false;
        if (getBillingEmail() != null ? !getBillingEmail().equals(that.getBillingEmail()) : that.getBillingEmail() != null)
            return false;
        if (getBillingAmount() != null ? !getBillingAmount().equals(that.getBillingAmount()) : that.getBillingAmount() != null)
            return false;
        return getBillingOrderId() != null ? getBillingOrderId().equals(that.getBillingOrderId()) : that.getBillingOrderId() == null;

    }

    @Override
    public int hashCode() {
        int result = getBillingName() != null ? getBillingName().hashCode() : 0;
        result = 31 * result + (getBillingAddress() != null ? getBillingAddress().hashCode() : 0);
        result = 31 * result + (getBillingCity() != null ? getBillingCity().hashCode() : 0);
        result = 31 * result + (getBillingRegion() != null ? getBillingRegion().hashCode() : 0);
        result = 31 * result + (getBillingPhone() != null ? getBillingPhone().hashCode() : 0);
        result = 31 * result + (getBillingZip() != null ? getBillingZip().hashCode() : 0);
        result = 31 * result + (getBillingEmail() != null ? getBillingEmail().hashCode() : 0);
        result = 31 * result + (getBillingAmount() != null ? getBillingAmount().hashCode() : 0);
        result = 31 * result + (getBillingOrderId() != null ? getBillingOrderId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PaymentArgs{" +
                "billingName='" + billingName + '\'' +
                ", billingAddress='" + billingAddress + '\'' +
                ", billingCity='" + billingCity + '\'' +
                ", billingRegion='" + billingRegion + '\'' +
                ", billingPhone='" + billingPhone + '\'' +
                ", billingZip='" + billingZip + '\'' +
                ", billingEmail='" + billingEmail + '\'' +
                ", billingAmount='" + billingAmount + '\'' +
                ", billingOrderId='" + billingOrderId + '\'' +
                '}';
    }
}
