package com.udea.parcial.service;

import java.util.List;


import com.udea.parcial.dto.InventoryRequest;
import com.udea.parcial.dto.InventoryResponse;


public interface InventoryService {

    List<InventoryResponse> getInventoryByAlmacen(Long almacenId);

    InventoryResponse createInventory(InventoryRequest request);
}
