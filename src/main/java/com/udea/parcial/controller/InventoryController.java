package com.udea.parcial.controller;



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
    @GetMapping
    public ResponseEntity<?> getInventory(
            @RequestHeader("X-API-Version") String version,
            @RequestParam Long almacenId
    ) {
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
                                    .getInventory(version, almacenId)
                    ).withSelfRel();

                    Link almacenLink = WebMvcLinkBuilder.linkTo(
                            WebMvcLinkBuilder.methodOn(InventoryController.class)
                                    .getInventory(version, inv.getAlmacen().getId())
                    ).withRel("almacen");

                    return EntityModel.of(dto, selfLink, almacenLink);

                }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }


    // =====================================================
    // POST: Registrar producto + inventario
    // =====================================================
    @PostMapping
    public ResponseEntity<?> createInventory(
            @RequestHeader("X-API-Version") String version,
            @RequestBody InventoryRequest request
    ) {
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
                request.getStock()
        );
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
                        .getInventory(version, almacen.getId())
        ).withRel("inventario_por_almacen");

        Link selfLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(InventoryController.class)
                        .createInventory(version, request)
        ).withSelfRel();

        model.add(selfLink);
        model.add(getAllLink);

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }
}
