package com.example.todo.userapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "tbl_user")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String userName;

    @CreationTimestamp
    private LocalDateTime joinDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.COMMON;    //  유저 권한

    private String profileImg;  // 프로필 이미지 경로

    private String accessToken; // 카카오 로그인 시 발급받는 accessToken 저장 -> 로그아웃 때 필요

    // 등급 수정 메서드 (엔터티는 @Setter를 설정하지 않고 변경 가능성이 있는 필드를 직접 수정하는 메서드를 작성하는 것이 일반적)
    public void changeRole(Role role) {
        this.role = role;
    }

    public void changeAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
