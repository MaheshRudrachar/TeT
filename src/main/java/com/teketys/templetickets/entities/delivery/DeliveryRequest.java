package com.teketys.templetickets.entities.delivery;

/**
 * Created by rudram1 on 8/25/16.
 */

public class DeliveryRequest {

    private Delivery delivery;

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeliveryRequest that = (DeliveryRequest) o;

        return !(delivery != null ? !delivery.equals(that.delivery) : that.delivery != null);
    }

    @Override
    public int hashCode() {
        return delivery != null ? delivery.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DeliveryRequest{" +
                "delivery=" + delivery +
                '}';
    }
}