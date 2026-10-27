package com.service.products.controller;

import com.service.products.dto.CreateProductDTO;
import com.service.products.dto.RecoveryProductDTO;
import com.service.products.dto.UpdateProductDTO;
import com.service.products.exceptions.ProductException;
import com.service.products.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(value = {"", "/create"})
    public ResponseEntity<RecoveryProductDTO> createProduct(@RequestBody CreateProductDTO createProductDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(createProductDto));
    }

    @GetMapping(value = {"", "/list"})
    public ResponseEntity<List<RecoveryProductDTO>> getAllProducts() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProducts());
    }

    @GetMapping(value = {"/{id}", "/details/{id}"})
    public ResponseEntity<RecoveryProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductById(id));
    }

    @PutMapping(value = {"/{id}", "/update/{id}"})
    public ResponseEntity<RecoveryProductDTO> updateProduct(@PathVariable Long id, @RequestBody UpdateProductDTO updateProductDto) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(id, updateProductDto));
    }

    @DeleteMapping(value = {"/{id}", "/delete/{id}"})
    public ResponseEntity<String> deleteProductById(@PathVariable Long id) {
        try {
            productService.deleteProductById(id);
            return ResponseEntity.ok("Produto deletado com sucesso");
        } catch (ProductException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

