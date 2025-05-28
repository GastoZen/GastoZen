package br.edu.ifpb.GastoZen.service;

import br.edu.ifpb.GastoZen.repository.UserRepository;
import br.edu.ifpb.GastoZen.model.User;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        validateUser(user);
        return userRepository.save(user);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String email) {
        userRepository.delete(email);
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (user.getAge() <= 0) {
            throw new IllegalArgumentException("Age must be positive");
        }
        if (user.getSalary() < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be empty");
        }
        if (user.getOccupation() == null || user.getOccupation().trim().isEmpty()) {
            throw new IllegalArgumentException("Occupation cannot be empty");
        }
    }
}