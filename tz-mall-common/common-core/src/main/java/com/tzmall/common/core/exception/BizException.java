package com.tzmall.common.core.exception;

import lombok.Getter;

/**
 * 自定义业务异常
 * <p>
 * 用于业务逻辑中的可预期异常，如：用户名已存在、库存不足、订单不存在等。
 * 与系统异常（如 NPE、网络超时）区分开，便于全局异常处理器返回不同的 HTTP 状态码。
 * </p>
 */
@Getter
public class BizException extends RuntimeException {

    /**
     * 业务错误码，默认 400
     */
    private final int code;

    public BizException(String message) {
        super(message);
        this.code = 400;
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.code = 400;
    }

    public BizException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
