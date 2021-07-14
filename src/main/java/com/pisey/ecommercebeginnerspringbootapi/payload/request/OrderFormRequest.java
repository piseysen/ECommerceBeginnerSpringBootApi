package com.pisey.ecommercebeginnerspringbootapi.payload.request;

import com.pisey.ecommercebeginnerspringbootapi.dto.OrderProductDto;

import java.util.List;

public class OrderFormRequest {

    private List<OrderProductDto> productOrders;

    public List<OrderProductDto> getProductOrders() {
        return productOrders;
    }

    public void setProductOrders(List<OrderProductDto> productOrders) {
        this.productOrders = productOrders;
    }
}