package com.example.todo.userapi.dto.response;

import com.example.todo.userapi.entity.User;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "email")
@Builder
public class UserSignUpResponseDTO {
    private String email;

    private String userName;

    public UserSignUpResponseDTO(User saved) {
        this.email = saved.getEmail();
        this.userName = saved.getUserName();
    }
}
