package com.hae.todoapp.data.model;

public class SignupUser {
    private String strUserName;
    private String strEmail;
    private String strPassword;

    public SignupUser() {
    }

    public SignupUser(String strUserName, String strEmail, String strPassword) {
        this.strUserName = strUserName;
        this.strEmail = strEmail;
        this.strPassword = strPassword;
    }

    public String getStrUserName() {
        return strUserName;
    }

    public void setStrUserName(String strUserName) {
        this.strUserName = strUserName;
    }

    public String getStrEmail() {
        return strEmail;
    }

    public void setStrEmail(String strEmail) {
        this.strEmail = strEmail;
    }

    public String getStrPassword() {
        return strPassword;
    }

    public void setStrPassword(String strPassword) {
        this.strPassword = strPassword;
    }

    @Override
    public String toString() {
        return "SignupUser{" +
                "strUserName='" + strUserName + '\'' +
                ", strEmail='" + strEmail + '\'' +
                ", strPassword='" + strPassword + '\'' +
                '}';
    }
}
