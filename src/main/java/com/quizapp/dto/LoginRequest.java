package com.quizapp.dto;


import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    public LoginRequest(String mail, String password) {
    }
    public LoginRequest() {
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
