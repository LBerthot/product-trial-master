package com.producttrial.back.entity;

import com.producttrial.back.enums.InventoryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    private String image;

    private String category;

    @Column(nullable = false)
    private Double price; //TODO ou bigDecimal? A déterminer plus tard

    private Integer quantity;

    @Column(name = "internal_reference")
    private String internalReference;

    @Column(name = "shell_id")
    private Long shellId;

    @Enumerated(EnumType.STRING)
    @Column(name = "inventory_status")
    private InventoryStatus inventoryStatus;

    private Double rating;

    @Column(nullable = false, name = "created_at")
    private Long createdAt;

    @Column(nullable = false, name = "updated_at")
    private Long updatedAt;
}
