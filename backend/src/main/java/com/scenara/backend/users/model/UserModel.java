package com.scenara.backend.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "users")
@NoArgsConstructor
@Getter
@Setter
public class UserModel {

    @Id
    private String id;

    private String email;
    private String name;
    private String password;
    private Instant createdAt;
    private Instant updatedAt;

}
