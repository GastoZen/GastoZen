package br.edu.ifpb.gastozen.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private int age;
    private double salary;
    private String phone;
    private String occupation;
}