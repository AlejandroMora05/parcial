package com.udea.parcial.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.udea.parcial.dto.InventoryRequest;
import com.udea.parcial.dto.InventoryResponse;
import com.udea.parcial.entity.Inventory;
import com.udea.parcial.entity.Almacen;
import com.udea.parcial.entity.Product;
import com.udea.parcial.repository.AlmacenRepository;
import com.udea.parcial.repository.InventoryRepository;
import com.udea.parcial.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventory")
@Tag(name = "Inventario", description = "API para gestión de inventarios de productos por almacén")
public class InventoryController {

        private final InventoryRepository inventoryRepo;
        private final ProductRepository productRepo;
        private final AlmacenRepository almacenRepo;

        public InventoryController(InventoryRepository inventoryRepo,
                        ProductRepository productRepo,
                        AlmacenRepository almacenRepo) {
                this.inventoryRepo = inventoryRepo;
                this.productRepo = productRepo;
                this.almacenRepo = almacenRepo;
        }

        // =====================================================
        // GET: Consultar inventario por almacén
        // =====================================================
        @Operation(summary = "Consultar inventario por almacén", description = "Obtiene todos los productos y sus cantidades en stock para un almacén específico. "
                        +
                        "Se requiere el header X-API-Version con valor 'v1'.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Inventario encontrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventoryResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Versión de API no soportada", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Almacén no encontrado", content = @Content)
        })
        @GetMapping
        public ResponseEntity<?> getInventory(
                        @Parameter(description = "Versión de la API (requerido: 'v1')", required = true, example = "v1") @RequestHeader("X-API-Version") String version,

                        @Parameter(description = "ID del almacén a consultar", required = true, example = "1") @RequestParam Long almacenId) {
                // Versionamiento obligatorio
                if (!"v1".equals(version)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body("Unsupported API version");
                }

                List<Inventory> inventories = inventoryRepo.findByAlmacenId(almacenId);

                List<EntityModel<InventoryResponse>> responseList = inventories.stream()
                                .map(inv -> {

                                        // Construimos el DTO
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

                                        // HATEOAS
                                        Link selfLink = WebMvcLinkBuilder.linkTo(
                                                        WebMvcLinkBuilder.methodOn(InventoryController.class)
                                                                        .getInventory(version, almacenId))
                                                        .withSelfRel();

                                        Link almacenLink = WebMvcLinkBuilder.linkTo(
                                                        WebMvcLinkBuilder.methodOn(InventoryController.class)
                                                                        .getInventory(version,
                                                                                        inv.getAlmacen().getId()))
                                                        .withRel("almacen");

                                        return EntityModel.of(dto, selfLink, almacenLink);

                                }).collect(Collectors.toList());

                return ResponseEntity.ok(responseList);
        }

        // =====================================================
        // POST: Registrar producto + inventario
        // =====================================================
        @Operation(summary = "Registrar nuevo producto en inventario", description = "Crea un nuevo producto y lo registra en el inventario de un almacén específico con su cantidad inicial en stock. "
                        +
                        "Se requiere el header X-API-Version con valor 'v1'.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Producto e inventario creados exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventoryResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Versión de API no soportada o datos inválidos", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Almacén no encontrado", content = @Content)
        })
        @PostMapping
        public ResponseEntity<?> createInventory(
                        @Parameter(description = "Versión de la API (requerido: 'v1')", required = true, example = "v1") @RequestHeader("X-API-Version") String version,

                        @Parameter(description = "Datos del producto y cantidad inicial en stock", required = true) @RequestBody InventoryRequest request) {
                // Validación de versión
                if (!"v1".equals(version)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body("Unsupported API version");
                }

                // Validamos almacén
                Almacen almacen = almacenRepo.findById(request.getAlmacenId())
                                .orElse(null);

                if (almacen == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body("El almacén no existe");
                }

                // Creamos el producto
                Product product = new Product();
                product.setName(request.getProductName());
                product.setDescription(request.getProductDescription());
                product.setSku(request.getSku());
                product.setPrice(request.getPrice());
                productRepo.save(product);

                // Creamos inventario
                Inventory inv = new Inventory(
                                almacen,
                                product,
                                request.getStock());
                inventoryRepo.save(inv);

                // Construimos DTO response
                InventoryResponse dto = new InventoryResponse();
                dto.setInventoryId(inv.getId());
                dto.setAlmacenId(almacen.getId());
                dto.setAlmacenNombre(almacen.getNombre());
                dto.setProductId(product.getId());
                dto.setProductName(product.getName());
                dto.setProductDescription(product.getDescription());
                dto.setSku(product.getSku());
                dto.setPrice(product.getPrice());
                dto.setStock(inv.getStock());
                dto.setLastUpdated(inv.getLastUpdated());

                // HATEOAS links
                EntityModel<InventoryResponse> model = EntityModel.of(dto);

                Link getAllLink = WebMvcLinkBuilder.linkTo(
                                WebMvcLinkBuilder.methodOn(InventoryController.class)
                                                .getInventory(version, almacen.getId()))
                                .withRel("inventario_por_almacen");

                Link selfLink = WebMvcLinkBuilder.linkTo(
                                WebMvcLinkBuilder.methodOn(InventoryController.class)
                                                .createInventory(version, request))
                                .withSelfRel();

                model.add(selfLink);
                model.add(getAllLink);

                return ResponseEntity.status(HttpStatus.CREATED).body(model);
        }
}