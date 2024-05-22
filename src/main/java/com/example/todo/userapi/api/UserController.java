package com.example.todo.userapi.api;

import com.example.todo.auth.TokenUserInfo;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserSignUpRequestDTO;
import com.example.todo.userapi.dto.response.LoginResponseDTO;
import com.example.todo.userapi.dto.response.UserSignUpResponseDTO;
import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
    // POST : /api/auth
    @PostMapping
    public ResponseEntity<?> signUp(
            @Validated @RequestBody UserSignUpRequestDTO dto,
            BindingResult result
    ) {
        log.info("/api/auth POST! - {}", dto);

        ResponseEntity<FieldError> resultEntity = getFieldErrorResponseEntity(result);
        if (resultEntity != null) return resultEntity;

        UserSignUpResponseDTO responseDTO = userService.create(dto);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(
            @Validated @RequestBody LoginRequestDTO dto,
            BindingResult result
    ) {
        log.info("/api/auth/signin - POST! - {}", dto);

        ResponseEntity<FieldError> response = getFieldErrorResponseEntity(result);
        if (response != null) return response;

        LoginResponseDTO responseDTO = userService.authenticate(dto);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/promote")
    // 권한 검수 (해당 권한이 아니라면 인가처리 거부 -> 403 상태 리턴)
    // 메서드 호출 전에 검사 -> 요청 당시 토큰에 있는 user 정보가 ROLE_COMMON이라는 권한을 갖는지 검사
    @PreAuthorize("hasRole('ROLE_COMMON')")
    public ResponseEntity<?> promote(@AuthenticationPrincipal TokenUserInfo userInfo) {
        log.info("/api/auth/promote - PUT!");

        LoginResponseDTO responseDTO = userService.promoteToPremium(userInfo);
        return ResponseEntity.ok().body(responseDTO);
    }

    private static ResponseEntity<FieldError> getFieldErrorResponseEntity(BindingResult result) {
        if (result.hasErrors()) {
            log.warn(result.toString());
            return ResponseEntity.badRequest()
                    .body(result.getFieldError());
        }
        return null;
    }

}
