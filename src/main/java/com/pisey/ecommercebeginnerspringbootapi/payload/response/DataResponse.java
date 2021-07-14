package com.pisey.ecommercebeginnerspringbootapi.payload.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataResponse<T> extends BaseDataResponse<T> {
    public DataResponse(MsgEntity msgEntity, T data) {
        super(msgEntity, data);
    }
}
