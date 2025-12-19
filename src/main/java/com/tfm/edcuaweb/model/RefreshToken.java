package com.tfm.edcuaweb.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String refToken;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private boolean revoked = false;
}
