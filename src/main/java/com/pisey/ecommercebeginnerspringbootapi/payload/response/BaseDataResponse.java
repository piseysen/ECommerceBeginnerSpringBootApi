package com.pisey.ecommercebeginnerspringbootapi.payload.response;


public abstract class BaseDataResponse<T> {
    private MsgEntity msgEntity;
    private T data;

    public BaseDataResponse(MsgEntity msgEntity, T data) {
        this.msgEntity = msgEntity;
        this.data = data;
    }

    public MsgEntity getMsgEntity() {
        return msgEntity;
    }

    public void setMsgEntity(MsgEntity msgEntity) {
        this.msgEntity = msgEntity;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
