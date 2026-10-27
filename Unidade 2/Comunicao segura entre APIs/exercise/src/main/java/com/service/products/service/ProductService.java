package com.service.products.service;

import com.service.products.dto.CreateProductDTO;
import com.service.products.dto.RecoveryProductDTO;
import com.service.products.dto.UpdateProductDTO;
import com.service.products.entities.Product;
import com.service.products.exceptions.ProductNotFoundException;
import com.service.products.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public RecoveryProductDTO createProduct(CreateProductDTO dto) {
        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setQuantity(dto.quantity());

        Product savedProduct = productRepository.save(product);
        return toDto(savedProduct);
    }

    @Transactional(readOnly = true)
    public List<RecoveryProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecoveryProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return toDto(product);
    }

    @Transactional
    public RecoveryProductDTO updateProduct(Long id, UpdateProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setQuantity(dto.quantity());

        Product updatedProduct = productRepository.save(product);
        return toDto(updatedProduct);
    }

    @Transactional
    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.delete(product);
    }

    private RecoveryProductDTO toDto(Product product) {
        return new RecoveryProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity()
        );
    }
}
