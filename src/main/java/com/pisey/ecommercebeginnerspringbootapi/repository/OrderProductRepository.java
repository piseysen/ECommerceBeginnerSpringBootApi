package com.pisey.ecommercebeginnerspringbootapi.repository;


import com.pisey.ecommercebeginnerspringbootapi.domain.OrderProduct;
import com.pisey.ecommercebeginnerspringbootapi.domain.OrderProductPK;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductRepository extends CrudRepository<OrderProduct, OrderProductPK> {
}