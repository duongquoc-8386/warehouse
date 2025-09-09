package com.example.warehouse.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_variants")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenBienThe;

    private int soLuong;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;


    public Long getId() {
        return id;
    }

    public String getTenBienThe() {
        return tenBienThe;
    }

    public void setTenBienThe(String tenBienThe) {
        this.tenBienThe = tenBienThe;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
