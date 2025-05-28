package br.edu.ifpb.GastoZen.model;

public class User {
    private String uid;
    private String email;
    private String name;
    private int age;
    private double salary;
    private String phone;
    private String occupation;

    public User() {
    }

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
}