package com.example.quanlyphuongtien.Entities;

public class Teacher {
    private String username,name,password,headTeacher,dateOfBirth;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHeadTeacher() {
        return headTeacher;
    }

    public void setHeadTeacher(String headTeacher) {
        this.headTeacher = headTeacher;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Teacher(String username, String name, String password, String headTeacher, String dateOfBirth) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.headTeacher = headTeacher;
        this.dateOfBirth = dateOfBirth;
    }

    public Teacher(){
        
    }
}
