// src/test/java/br/edu/ifpb/service/FakeUserRepository.java
package br.edu.ifpb.service;

import br.edu.ifpb.model.User;
import br.edu.ifpb.repository.UserRepository;

import java.util.*;

public class FakeUserRepository implements UserRepository {
    private final Map<String, User> storage = new HashMap<>();

    @Override
    public User save(User user) {
        if (storage.containsKey(user.getEmail())) {
            throw new IllegalArgumentException("User with email already exists");
        }
        storage.put(user.getEmail(), user);
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(storage.get(email));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(String email) {
        storage.remove(email);
    }

    @Override
    public boolean exists(String email) {
        return storage.containsKey(email);
    }
}
