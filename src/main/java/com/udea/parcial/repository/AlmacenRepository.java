package com.udea.parcial.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.udea.parcial.entity.Almacen;

@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Long> {
}
