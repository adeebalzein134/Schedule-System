package com.example.smartschedule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer duration; // in minutes

    @Column(nullable = false)
    private Double price;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User assignedStaff; // Optional: if null, any staff can perform? Or this defines a specific service by a specific staff.
}
