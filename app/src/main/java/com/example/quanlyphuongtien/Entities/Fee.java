package com.example.quanlyphuongtien.Entities;

public class Fee {
    private String id, idSV, name;
    private int money;
    private String startDate, endDate;
    private boolean confirm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdSV() {
        return idSV;
    }

    public void setIdSV(String idSV) {
        this.idSV = idSV;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public Fee(String id, String idSV, String name, int money, String startDate, String endDate, boolean confirm) {
        this.id = id;
        this.idSV = idSV;
        this.name = name;
        this.money = money;
        this.startDate = startDate;
        this.endDate = endDate;
        this.confirm = confirm;
    }

    public Fee() {

    }
}
