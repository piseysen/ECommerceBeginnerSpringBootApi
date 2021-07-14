package com.pisey.ecommercebeginnerspringbootapi.service.impl;


import com.pisey.ecommercebeginnerspringbootapi.domain.OrderProduct;
import com.pisey.ecommercebeginnerspringbootapi.repository.OrderProductRepository;
import com.pisey.ecommercebeginnerspringbootapi.service.OrderProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderProductServiceImpl implements OrderProductService {

    private OrderProductRepository orderProductRepository;

    public OrderProductServiceImpl(OrderProductRepository orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }

    @Override
    public OrderProduct create(OrderProduct orderProduct) {
        return this.orderProductRepository.save(orderProduct);
    }
}