package br.edu.ifpb.GastoZen.repository;

import br.edu.ifpb.GastoZen.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void delete(String email);
    boolean exists(String email);
}