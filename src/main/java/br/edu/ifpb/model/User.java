package br.edu.ifpb.model;

public class User {
    private String name;
    private int age;
    private double salary;
    private String email;
    private String phone;
    private String occupation;

    public User() {
    }

    public User(String name, int age, double salary, String email, String phone, String occupation) {
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.email = email;
        this.phone = phone;
        this.occupation = occupation;
    }

    // Getters and Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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