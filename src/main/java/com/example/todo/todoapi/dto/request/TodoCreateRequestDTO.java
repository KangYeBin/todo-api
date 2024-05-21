package com.example.todo.todoapi.dto.request;

import com.example.todo.todoapi.entity.Todo;
import com.example.todo.userapi.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class TodoCreateRequestDTO {

    @NotBlank
    @Size(min = 2, max = 30)
    private String title;

    // dto를 entity로
    public Todo toEntity(User user) {
        return Todo.builder()
                .title(title)
                .user(user)
                .build();
    }
}
