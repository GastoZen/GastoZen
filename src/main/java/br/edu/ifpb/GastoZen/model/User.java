package br.edu.ifpb.GastoZen.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class User {
    private String uid;
    private String email;
    private String name;
    private int age;
    private double salary;
    private String phone;
    private String occupation;

    public User(String uid, String email, String name) {
        this.uid = uid;
        this.email = email;
        this.name = name;
    }

    public User(String uid, String email, String name, int age, double salary, String phone, String occupation) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.phone = phone;
        this.occupation = occupation;
    }
}