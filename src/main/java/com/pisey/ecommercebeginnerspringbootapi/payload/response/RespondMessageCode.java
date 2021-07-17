package com.pisey.ecommercebeginnerspringbootapi.payload.response;

public class RespondMessageCode {
    public static MsgEntity responseSuccess() {
        return new MsgEntity("01", "Successful");
    }

    public static MsgEntity responseSuccess(String message) {
        return new MsgEntity("01", message);
    }

    public static MsgEntity responseError(String message) {
        return new MsgEntity("02", message);
    }

    public static MsgEntity responseError(String code, String message) {
        return new MsgEntity(code, message);
    }
}
