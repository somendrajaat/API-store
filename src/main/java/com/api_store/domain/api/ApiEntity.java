package com.api_store.domain.api;

import com.api_store.domain.user.UserEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "apis")
public class ApiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String baseUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;
}