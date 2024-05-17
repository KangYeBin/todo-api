package com.example.todo.userapi.repository;

import com.example.todo.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User
        , String> {

    // 이메일 중복 체크
    boolean existsByEmail(String email);
}
