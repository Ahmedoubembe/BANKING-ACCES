package com.bamis.banking_access.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "banking_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "service_type", nullable = false, length = 50)
    private String serviceType; // 'mobile' ou 'web'

    @Column(name = "modification_type", nullable = false, length = 50)
    private String modificationType; // 'access', 'subscription', 'password', 'other'

    @Column(name = "other_message", columnDefinition = "TEXT")
    private String otherMessage;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // 'PENDING', 'VALIDATED', 'PROCESSED', 'REJECTED'

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}