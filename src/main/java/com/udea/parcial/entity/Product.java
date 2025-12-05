package com.udea.parcial.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del producto
    @Column(nullable = false, length = 100)
    private String name;

    // Código SKU opcional
    @Column(length = 50, unique = true)
    private String sku;

    // Descripción del producto
    @Column(length = 500)
    private String description;

    // Precio de referencia (opcional)
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    // ===== Constructores =====
    public Product() {}

    public Product(String name, String sku, String description, BigDecimal price) {
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.price = price;
    }

    // ===== Getters y Setters =====
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // ===== equals y hashCode =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product that = (Product) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}