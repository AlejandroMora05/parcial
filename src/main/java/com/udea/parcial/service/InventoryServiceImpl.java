package com.udea.parcial.service;


import org.springframework.stereotype.Service;

import com.udea.parcial.dto.InventoryRequest;
import com.udea.parcial.dto.InventoryResponse;
import com.udea.parcial.entity.*;
import com.udea.parcial.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepo;
    private final ProductRepository productRepo;
    private final AlmacenRepository almacenRepo;

    public InventoryServiceImpl(InventoryRepository inventoryRepo,
                                ProductRepository productRepo,
                                AlmacenRepository almacenRepo) {
        this.inventoryRepo = inventoryRepo;
        this.productRepo = productRepo;
        this.almacenRepo = almacenRepo;
    }

    // =====================================================
    // GET inventario por almacén
    // =====================================================
    @Override
    public List<InventoryResponse> getInventoryByAlmacen(Long almacenId) {

        List<Inventory> inventories = inventoryRepo.findByAlmacenId(almacenId);

        return inventories.stream().map(inv -> {
            InventoryResponse dto = new InventoryResponse();
            dto.setInventoryId(inv.getId());
            dto.setAlmacenId(inv.getAlmacen().getId());
            dto.setAlmacenNombre(inv.getAlmacen().getNombre());
            dto.setProductId(inv.getProduct().getId());
            dto.setProductName(inv.getProduct().getName());
            dto.setProductDescription(inv.getProduct().getDescription());
            dto.setSku(inv.getProduct().getSku());
            dto.setPrice(inv.getProduct().getPrice());
            dto.setStock(inv.getStock());
            dto.setLastUpdated(inv.getLastUpdated());
            return dto;
        }).collect(Collectors.toList());
    }


    // =====================================================
    // POST crear inventario + producto
    // =====================================================
    @Override
    public InventoryResponse createInventory(InventoryRequest request) {

        // Buscar almacén
        Almacen almacen = almacenRepo.findById(request.getAlmacenId())
                .orElseThrow(() -> new RuntimeException("El almacén no existe"));

        // Crear producto
        Product product = new Product();
        product.setName(request.getProductName());
        product.setDescription(request.getProductDescription());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        productRepo.save(product);

        // Crear inventario
        Inventory inv = new Inventory(almacen, product, request.getStock());
        inventoryRepo.save(inv);

        // Construir respuesta
        InventoryResponse dto = new InventoryResponse();
        dto.setInventoryId(inv.getId());
        dto.setAlmacenNombre(almacen.getNombre());
        dto.setAlmacenId(almacen.getId());
        dto.setProductId(product.getId());
        dto.setProductName(product.getName());
        dto.setProductDescription(product.getDescription());
        dto.setSku(product.getSku());
        dto.setPrice(product.getPrice());
        dto.setStock(inv.getStock());
        dto.setLastUpdated(inv.getLastUpdated());

        return dto;
    }
}