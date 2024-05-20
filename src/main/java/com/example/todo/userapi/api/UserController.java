package com.example.todo.userapi.api;

import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserSignUpRequestDTO;
import com.example.todo.userapi.dto.response.UserSignUpResponseDTO;
import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        log.info("duplicated : {}", duplicated);

        return ResponseEntity.ok().body(duplicated);
    }

    // 회원 가입 요청 처리
    // POST: /api/auth
    @PostMapping
    public ResponseEntity<?> signUp(
            @Validated @RequestBody UserSignUpRequestDTO dto,
            BindingResult result
    ) {
        log.info("/api/auth POST! - {}", dto);

        if (result.hasErrors()) {
            log.warn(result.toString());
            return ResponseEntity.badRequest()
                    .body(result.getFieldError());
        }

        try {
            UserSignUpResponseDTO responseDTO = userService.create(dto);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
