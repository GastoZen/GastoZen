package br.edu.ifpb.GastoZen.dto;

public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private int age;
    private double salary;
    private String phone;
    private String occupation;

    public RegisterRequest() {
    }

    public RegisterRequest(String email, String password, String name, int age, double salary, String phone, String occupation) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.phone = phone;
        this.occupation = occupation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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