package com.example.quanlyphuongtien.Entities;

public class Delete {
    private String id, name, reason;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Delete(String id, String name, String reason) {
        this.id = id;
        this.name = name;
        this.reason = reason;
    }

    public Delete() {

    }
}
