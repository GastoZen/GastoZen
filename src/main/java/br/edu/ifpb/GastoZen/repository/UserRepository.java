package br.edu.ifpb.gastozen.repository;

import br.edu.ifpb.gastozen.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByUid(String uid);

    List<User> findAll();

    void delete(String email);

    boolean exists(String email);
}