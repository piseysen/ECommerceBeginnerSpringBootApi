package com.pisey.ecommercebeginnerspringbootapi.service.impl;


import com.pisey.ecommercebeginnerspringbootapi.domain.Product;
import com.pisey.ecommercebeginnerspringbootapi.exception.ResourceNotFoundException;
import com.pisey.ecommercebeginnerspringbootapi.repository.ProductRepository;
import com.pisey.ecommercebeginnerspringbootapi.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Iterable<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProduct(long id) {
        return productRepository
          .findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }
}