package com.example.todo.auth;

import com.example.todo.userapi.entity.Role;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class TokenUserInfo {

    private String userId;
    private String email;
    private Role role;

}
