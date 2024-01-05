package org.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto<T> {

    private StatusEnum status;
    private T body;
    private String message;

    public ResponseDto(T body, String message) {
        this.body = body;
        this.message = message;
    }

    public static <T> ResponseDto<T> success(T body) {
        return new ResponseDto<>(StatusEnum.SUCCESS, body, null);
    }

    public static <T> ResponseDto<T> fail(String message) {
        return new ResponseDto<>(StatusEnum.FAIL, null, message);
    }

    public static <T> ResponseDto<T> error(String message) {
        return new ResponseDto<>(StatusEnum.ERROR, null, message);
    }

    public enum StatusEnum {
        SUCCESS, FAIL, ERROR
    }

}
