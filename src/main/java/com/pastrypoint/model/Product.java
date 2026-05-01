package com.pastrypoint.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pastry_products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private Double price;
    private String category;
    private String imageUrl;
    
    @Column(length = 1000)
    private String description;
    
    private Integer stockQuantity;

    @Column(columnDefinition = "boolean default true")
    private boolean inStock = true;
}
