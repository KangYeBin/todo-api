package com.example.todo.userapi.api;

import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    // 이메일 중복 확인 요청 처리
    // GET : /api/auth/check?email=zzzz@xxx.mmm
    @GetMapping("/check")
    public ResponseEntity<?> check(String email) {
        log.info("/api/auth/check?email={} GET", email);
        if (email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("이메일이 없습니다");
        }

        boolean duplicated = userService.isDuplicated(email);
        log.info("duplicated : {}",duplicated);

        return ResponseEntity.ok().body(duplicated);
    }
}
