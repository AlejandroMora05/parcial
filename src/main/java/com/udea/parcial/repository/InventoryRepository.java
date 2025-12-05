package com.udea.parcial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udea.parcial.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByAlmacenId(Long almacenId);
}