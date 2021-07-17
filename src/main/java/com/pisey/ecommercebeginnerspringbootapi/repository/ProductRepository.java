package com.pisey.ecommercebeginnerspringbootapi.repository;


import com.pisey.ecommercebeginnerspringbootapi.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

//    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%"
//             + " OR p.brand LIKE %?1%"
//             + " OR p.madein LIKE %?1%"
//            + " OR CONCAT(p.price, '') LIKE %?1%")
//    public List<Product> search(String keyword);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    List<Product> search(String keyword);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    Page<List<Product>> findByName(@Param("name") String name, Pageable pageable);

//    Page<List<Product>> getProducts(Pageable pageable);
}