package com.pisey.ecommercebeginnerspringbootapi.service.impl;


import com.pisey.ecommercebeginnerspringbootapi.domain.Product;
import com.pisey.ecommercebeginnerspringbootapi.exception.ResourceNotFoundException;
import com.pisey.ecommercebeginnerspringbootapi.repository.ProductRepository;
import com.pisey.ecommercebeginnerspringbootapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
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

    @Override
    public Page<List<Product>> findByName(String name, int offset, int limit) {
        return productRepository.findByName(name, PageRequest.of(offset, limit, Sort.by("id")));
    }

    @Override
    public Page<Product> getProductByOffsetLimit(int offset, int limit) {
        return productRepository.findAll(PageRequest.of(offset, limit, Sort.by("id")));
    }
}