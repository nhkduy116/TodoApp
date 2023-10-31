package com.hae.todoapp.data.model;

import android.util.Patterns;

import java.util.regex.Pattern;

public class LoginUser {
    private String strEmail;
    private String strPassword;

    public LoginUser(String strEmail, String strPassword) {
        this.strEmail = strEmail;
        this.strPassword = strPassword;
    }

    public String getStrEmail() {
        return strEmail;
    }

    public String getStrPassword() {
        return strPassword;
    }

    public boolean isEmailValid() {
        return Patterns.EMAIL_ADDRESS.matcher(getStrEmail()).matches();
    }

    public boolean isPasswordLengthGreeterThan5() {
        return getStrPassword().length() > 5;
    }
}
