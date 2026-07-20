package edu.cit.capendit.unisell.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.capendit.unisell.auth.model.Role;
import edu.cit.capendit.unisell.auth.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByRole(Role role);
}