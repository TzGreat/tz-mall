package com.tzmall.common.core.exception;

import com.tzmall.common.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 按异常类型从具体到通用排列，Spring 会匹配最精确的 handler。
 * </p>
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class GlobalExceptionHandler {

    /**
     * 业务异常（可预期）
     * <p>
     * 如：用户名已存在、库存不足、订单不存在等。
     * 返回异常中携带的 code 和 message，HTTP 状态码默认 400。
     * </p>
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBizException(BizException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常（@Valid / @Validated 触发）
     * <p>
     * 提取所有字段的校验错误信息，拼接后返回。
     * </p>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", message);
        return Result.fail(400, message);
    }

    /**
     * 缺少必填请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("缺少必填参数: {}", e.getParameterName());
        return Result.fail(400, "缺少必填参数: " + e.getParameterName());
    }

    /**
     * 参数类型不匹配（如传字符串给 Integer 参数）
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String name = e.getName();
        String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知类型";
        log.warn("参数类型不匹配: {} 需要 {}", name, requiredType);
        return Result.fail(400, "参数 '" + name + "' 类型错误，需要 " + requiredType);
    }

    /**
     * 非法参数异常（如 @RequestParam 的 validation 失败）
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("非法参数: {}", e.getMessage());
        return Result.fail(400, e.getMessage());
    }

    /**
     * 兜底：处理所有未被上述 handler 捕获的异常（系统异常）
     * <p>
     * 不暴露内部错误细节给前端，只返回通用提示。
     * </p>
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.fail(500, StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "操作失败");
    }
}
