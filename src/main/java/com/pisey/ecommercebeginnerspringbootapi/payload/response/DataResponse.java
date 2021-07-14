package com.pisey.ecommercebeginnerspringbootapi.payload.response;


public class DataResponse extends BaseDataResponse<Object> {

    public DataResponse(MsgEntity msgEntity, Object data) {
        super(msgEntity, data);
    }
}
