package com.pisey.ecommercebeginnerspringbootapi.payload.request;

import com.pisey.ecommercebeginnerspringbootapi.dto.DeviceInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogOutRequest {
    private Long userId;

    public Long getUserId() {
        return this.userId;
    }

    //new
    @Valid
    @NotNull(message = "Device info cannot be null")
    private DeviceInfo deviceInfo;

    @Valid
    @NotNull(message = "Existing Token needs to be passed")
    private String token;
}
