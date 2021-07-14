package com.pisey.ecommercebeginnerspringbootapi.controller;


import com.pisey.ecommercebeginnerspringbootapi.domain.Product;
import com.pisey.ecommercebeginnerspringbootapi.payload.response.DataResponse;
import com.pisey.ecommercebeginnerspringbootapi.payload.response.RespondMsgCode;
import com.pisey.ecommercebeginnerspringbootapi.repository.ProductRepository;
import com.pisey.ecommercebeginnerspringbootapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    ProductRepository productRepository;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(value = {"", "/"})
    public @NotNull DataResponse<Iterable<Product>> getProducts() {
        Iterable<Product> products = productService.getAllProducts();
        return new DataResponse<>(RespondMsgCode.responseSuccess(), products);
    }

    @PostMapping( "/create")
    public DataResponse<Product> createProduct(@Valid @RequestBody Product product){
        productService.save(product);
        return new DataResponse<>(RespondMsgCode.responseSuccess(), productService.getProduct(product.getId()));
    }

    @PostMapping( "/update/{id}")
    public DataResponse<Product> updateProduct(@PathVariable("id") Long id, @RequestBody Product product){
        Optional<Product> productData = productRepository.findById(id);

        if(productData.isPresent()){
            Product _product = productData.get();
            _product.setName(product.getName());
            _product.setPrice(product.getPrice());
            _product.setPictureUrl(product.getPictureUrl());
            productRepository.save(_product);
           return new DataResponse<>(RespondMsgCode.responseSuccess("Update product successful"), _product);
        }
        return new DataResponse<>(RespondMsgCode.responseError("02", "Update Product Error"), null);
    }

    @DeleteMapping("/delete/{id}")
    public DataResponse<Object> deleteProduct(@PathVariable("id") Long id){
        try{
            productRepository.deleteById(id);
            return new DataResponse<>(RespondMsgCode.responseSuccess("Delete product successful"), null);
        }catch (Exception e){
            return new DataResponse<>(RespondMsgCode.responseError("99","Internal server error"), null);
        }
    }


}