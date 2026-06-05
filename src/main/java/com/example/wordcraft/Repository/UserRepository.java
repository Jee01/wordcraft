package com.example.wordcraft.Repository;

import com.example.wordcraft.Entity.Users;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(@Email(message = "이메일 형식이 아닙니다.") String email);
}
