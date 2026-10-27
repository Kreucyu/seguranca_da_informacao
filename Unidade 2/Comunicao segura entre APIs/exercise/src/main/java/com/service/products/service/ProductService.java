package com.service.products.service;

import com.service.products.dto.CreateProductDTO;
import com.service.products.dto.RecoveryProductDTO;
import com.service.products.dto.UpdateProductDTO;
import com.service.products.entities.Product;
import com.service.products.exceptions.ProductException;
import com.service.products.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public RecoveryProductDTO createProduct(CreateProductDTO dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new ProductException("Nome do produto é obrigatório");
        }
        if (dto.price() == null) {
            throw new ProductException("Preço do produto é obrigatório");
        }
        if (dto.quantity() == null) {
            throw new ProductException("Quantidade do produto é obrigatória");
        }

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
                .orElseThrow(() -> new ProductException("Produto com ID " + id + " não encontrado"));
        return toDto(product);
    }

    @Transactional
    public RecoveryProductDTO updateProduct(Long id, UpdateProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Produto com ID " + id + " não encontrado"));

        if (dto.name() != null && !dto.name().isBlank()) {
            product.setName(dto.name());
        }
        if (dto.description() != null) {
            product.setDescription(dto.description());
        }
        if (dto.price() != null) {
            product.setPrice(dto.price());
        }
        if (dto.quantity() != null) {
            product.setQuantity(dto.quantity());
        }

        Product updatedProduct = productRepository.save(product);
        return toDto(updatedProduct);
    }

    @Transactional
    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Produto com ID " + id + " não encontrado"));
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

