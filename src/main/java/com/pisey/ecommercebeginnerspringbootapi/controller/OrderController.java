package com.pisey.ecommercebeginnerspringbootapi.controller;


import com.pisey.ecommercebeginnerspringbootapi.domain.Order;
import com.pisey.ecommercebeginnerspringbootapi.domain.OrderProduct;
import com.pisey.ecommercebeginnerspringbootapi.domain.OrderStatus;
import com.pisey.ecommercebeginnerspringbootapi.dto.OrderProductDto;
import com.pisey.ecommercebeginnerspringbootapi.exception.ResourceNotFoundException;
import com.pisey.ecommercebeginnerspringbootapi.payload.request.OrderFormRequest;
import com.pisey.ecommercebeginnerspringbootapi.payload.response.DataResponse;
import com.pisey.ecommercebeginnerspringbootapi.payload.response.RespondMsgCode;
import com.pisey.ecommercebeginnerspringbootapi.service.OrderProductService;
import com.pisey.ecommercebeginnerspringbootapi.service.OrderService;
import com.pisey.ecommercebeginnerspringbootapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    ProductService productService;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderProductService orderProductService;

    public OrderController(ProductService productService, OrderService orderService, OrderProductService orderProductService) {
        this.productService = productService;
        this.orderService = orderService;
        this.orderProductService = orderProductService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public @NotNull Iterable<Order> list() {
        return this.orderService.getAllOrders();
    }

    @PostMapping
    public DataResponse<Order> create(@RequestBody OrderFormRequest form) {
        List<OrderProductDto> formDtos = form.getProductOrders();
        validateProductsExistence(formDtos);
        Order order = new Order();
        order.setStatus(OrderStatus.PAID.name());
        order = this.orderService.create(order);

        List<OrderProduct> orderProducts = new ArrayList<>();
        for (OrderProductDto dto : formDtos) {
            orderProducts.add(orderProductService.create(new OrderProduct(order, productService.getProduct(dto
                    .getProduct()
                    .getId()), dto.getQuantity())));
        }

        order.setOrderProducts(orderProducts);

        this.orderService.update(order);

        String uri = ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/orders/{id}")
                .buildAndExpand(order.getId())
                .toString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", uri);

        return new DataResponse<>(RespondMsgCode.responseSuccess(), order);
    }

    private void validateProductsExistence(List<OrderProductDto> orderProducts) {
        List<OrderProductDto> list = orderProducts
                .stream()
                .filter(op -> Objects.isNull(productService.getProduct(op
                        .getProduct()
                        .getId())))
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(list)) {
            new ResourceNotFoundException("Product not found");
        }
    }


}
