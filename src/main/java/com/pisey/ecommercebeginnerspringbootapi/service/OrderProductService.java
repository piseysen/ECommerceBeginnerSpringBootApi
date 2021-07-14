package com.pisey.ecommercebeginnerspringbootapi.service;


import com.pisey.ecommercebeginnerspringbootapi.domain.OrderProduct;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
@Service
public interface OrderProductService {

    OrderProduct create(@NotNull(message = "The products for order cannot be null.") @Valid OrderProduct orderProduct);
}