package com.workintech.twitter.repository;

import com.workintech.twitter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Spring Data, metod isimlerinden SQL uretir:
// existsByUsername -> select count(*) > 0 from users where username = ?
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);
}
