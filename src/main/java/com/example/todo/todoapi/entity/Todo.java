package com.example.todo.todoapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_todo")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String todoId;

    @Column(nullable = false, length = 30)
    private String title;   // 할 일

    private boolean done;   // 할 일 완료 여부

}
