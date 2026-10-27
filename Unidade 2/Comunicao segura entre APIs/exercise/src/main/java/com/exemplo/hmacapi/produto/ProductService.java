package com.exemplo.hmacapi.produto;

import java.util.List;

import com.exemplo.hmacapi.produto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto) {
        Product product = new Product();

        product.setQuantity(dto.quantity());
        product.setPrice(dto.price());
        product.setName(dto.name());
        product.setDescription(dto.description());

        return ProductResponseDTO.getNew(repository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> list() {
        return repository.findAll().stream().map(ProductResponseDTO::getNew).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        return ProductResponseDTO.getNew(getById(id));
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        Product product = getById(id);

        product.setDescription(dto.description());
        product.setName(dto.name());
        product.setPrice(dto.price());
        product.setQuantity(dto.quantity());

        repository.saveAndFlush(product);

        return ProductResponseDTO.getNew(product);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getById(id));
    }

    private Product getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }
}
