package com.example.quanlyphuongtien.Entities;

public class Student {
    private String id, password, name, className, dateOfBirth,numberPlate,vehicleCategory;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public String getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }

    public Student(String id, String password, String name, String className, String dateOfBirth, String numberPlate, String vehicleCategory) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.className = className;
        this.dateOfBirth = dateOfBirth;
        this.numberPlate = numberPlate;
        this.vehicleCategory = vehicleCategory;
    }
    public Student(){

    }
}
