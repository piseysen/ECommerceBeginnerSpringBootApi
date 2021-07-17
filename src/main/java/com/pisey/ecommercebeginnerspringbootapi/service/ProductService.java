package com.pisey.ecommercebeginnerspringbootapi.service;


import com.pisey.ecommercebeginnerspringbootapi.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@Service
public interface ProductService {

    @NotNull Iterable<Product> getAllProducts();

    Product getProduct(@Min(value = 1L, message = "Invalid product ID.") long id);

    Product save(Product product);

    Page<List<Product>> findByName(String name, int offset, int limit);

    Page<Product> getProductByOffsetLimit(int offset, int limit);

}