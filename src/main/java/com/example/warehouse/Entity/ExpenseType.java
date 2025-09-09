package com.example.warehouse.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "expense_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
