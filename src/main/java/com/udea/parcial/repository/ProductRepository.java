package com.udea.parcial.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.udea.parcial.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
