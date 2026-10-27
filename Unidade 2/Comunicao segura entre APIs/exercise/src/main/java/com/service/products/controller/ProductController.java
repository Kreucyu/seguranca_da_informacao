package com.service.products.controller;

import com.service.products.dto.CreateProductDTO;
import com.service.products.dto.RecoveryProductDTO;
import com.service.products.dto.UpdateProductDTO;
import com.service.products.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<RecoveryProductDTO> createProduct(@Valid @RequestBody CreateProductDTO createProductDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(createProductDto));
    }

    @GetMapping
    public ResponseEntity<List<RecoveryProductDTO>> getAllProducts() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecoveryProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecoveryProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductDTO updateProductDto) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(id, updateProductDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }
}
