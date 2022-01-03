package com.example.quanlyphuongtien.Database;

public class Remember {
    private String username, password;
    private int check;
    private int remember;

    public Remember() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public int getRemember() {
        return remember;
    }

    public void setRemember(int remember) {
        this.remember = remember;
    }

    public Remember(String username, String password, int check, int remember) {
        this.username = username;
        this.password = password;
        this.check = check;
        this.remember = remember;
    }
}
