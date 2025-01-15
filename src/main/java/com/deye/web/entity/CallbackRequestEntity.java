package com.deye.web.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Table(name = "CALLBACK_REQUEST")
@Entity
public class CallbackRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String comment;
    private Boolean messageCustomer;
    private LocalDateTime createdTime;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private CustomerEntity customer;
}
