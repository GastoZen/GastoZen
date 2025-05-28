package br.edu.ifpb.gastozen.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String uid;
    private String email;
    private String name;

    public LoginResponse(String token, String uid, String email, String name) {
        this.token = token;
        this.uid = uid;
        this.email = email;
        this.name = name;
    }
}