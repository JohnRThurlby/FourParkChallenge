package com.johnrthurlby.fourparkchallenge;

public class User {

    private int id;
    private String username, email, password, cert, userdevice, deletedate;

    public User(int id, String username, String email, String password, String cert, String userdevice, String deletedate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.cert = cert;
        this.userdevice = userdevice;
        this.deletedate = deletedate;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCert() { return cert; }

    public String getUserdevice() { return userdevice; }

    public String getDeletedate() { return deletedate; }


}
