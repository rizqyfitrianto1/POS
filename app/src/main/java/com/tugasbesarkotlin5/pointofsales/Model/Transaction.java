package com.tugasbesarkotlin5.pointofsales.Model;

public class Transaction {
    private String id;
    private String total_price;
    private String date;
    private String time;
    private String payment;
    private String card_number;

    public Transaction() {
    }

    public Transaction(String id, String total_price, String date, String time, String payment, String card_number) {
        this.id = id;
        this.total_price = total_price;
        this.date = date;
        this.time = time;
        this.payment = payment;
        this.card_number = card_number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }
}
