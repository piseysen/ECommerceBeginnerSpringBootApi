package com.pisey.ecommercebeginnerspringbootapi.controller;


import com.pisey.ecommercebeginnerspringbootapi.domain.Product;
import com.pisey.ecommercebeginnerspringbootapi.payload.response.DataResponse;
import com.pisey.ecommercebeginnerspringbootapi.payload.response.RespondMessageCode;
import com.pisey.ecommercebeginnerspringbootapi.repository.ProductRepository;
import com.pisey.ecommercebeginnerspringbootapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@CrossOrigin(value = "*")
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
        return new DataResponse<>(RespondMessageCode.responseSuccess(), products);
    }

    @PostMapping( "/create")
    public DataResponse<Product> createProduct(@Valid @RequestBody Product product){
        productService.save(product);
        return new DataResponse<>(RespondMessageCode.responseSuccess(), productService.getProduct(product.getId()));
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
           return new DataResponse<>(RespondMessageCode.responseSuccess("Update product successful"), _product);
        }
        return new DataResponse<>(RespondMessageCode.responseError("02", "Update Product Error"), null);
    }

    @DeleteMapping("/delete/{id}")
    public DataResponse<Object> deleteProduct(@PathVariable("id") Long id){
        try{
            productRepository.deleteById(id);
            return new DataResponse<>(RespondMessageCode.responseSuccess("Delete product successful"), null);
        }catch (Exception e){
            return new DataResponse<>(RespondMessageCode.responseError("99","Unable to delete product"), null);
        }
    }

    @GetMapping("/search")
    public DataResponse<Page<List<Product>>> searchProduct(@RequestParam("keyword") String keyword, @RequestParam("offset") int offset, @RequestParam("limit") int limit){
        Page<List<Product>> productList = productService.findByName(keyword, offset, limit);
        return new DataResponse<>(RespondMessageCode.responseSuccess(), productList);
    }

    @GetMapping("/byLimitOffset")
    public DataResponse<Page<Product>> getProductByLimitOffset(@RequestParam("offset") int offset, @RequestParam("limit") int limit){
        Page<Product> productList = productService.getProductByOffsetLimit(offset, limit);
        return new DataResponse<>(RespondMessageCode.responseSuccess(), productList);
    }

}