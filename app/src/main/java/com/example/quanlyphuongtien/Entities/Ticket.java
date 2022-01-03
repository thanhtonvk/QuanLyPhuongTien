package com.example.quanlyphuongtien.Entities;

public class Ticket {
    private String id,idhs, qr, sendDate, receveDate, name, vehicle, plate;
    private boolean status;

    public Ticket(String id, String idhs, String qr, String sendDate, String receveDate, String name, String vehicle, String plate, boolean status) {
        this.id = id;
        this.idhs = idhs;
        this.qr = qr;
        this.sendDate = sendDate;
        this.receveDate = receveDate;
        this.name = name;
        this.vehicle = vehicle;
        this.plate = plate;
        this.status = status;
    }

    public Ticket() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getReceveDate() {
        return receveDate;
    }

    public void setReceveDate(String receveDate) {
        this.receveDate = receveDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }


    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getIdhs() {
        return idhs;
    }

    public void setIdhs(String idhs) {
        this.idhs = idhs;
    }

    public boolean isStatus() {
        return status;
    }
}
