package com.pisey.ecommercebeginnerspringbootapi.controller;


import com.pisey.ecommercebeginnerspringbootapi.domain.Product;
import com.pisey.ecommercebeginnerspringbootapi.payload.response.DataResponse;
import com.pisey.ecommercebeginnerspringbootapi.payload.response.MsgEntity;
import com.pisey.ecommercebeginnerspringbootapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(value = {"", "/"})
    public @NotNull ResponseEntity<DataResponse> getProducts() {
        Iterable<Product> products = productService.getAllProducts();
        MsgEntity msgEntity = new MsgEntity("01", "Success");
        DataResponse dataResponse = new DataResponse(msgEntity,products);
        return new ResponseEntity<>(dataResponse, HttpStatus.OK);
    }

    @PostMapping( "/create")
    public ResponseEntity<DataResponse> createProduct(@Valid @RequestBody Product product){
        productService.save(product);
        MsgEntity msgEntity = new MsgEntity("01", "Success");
        DataResponse dataResponse = new DataResponse(msgEntity, productService.getProduct(product.getId()));
        return new ResponseEntity<>(dataResponse, HttpStatus.CREATED);
    }


}