package com.losca.gulimall.product.exception;

import com.losca.common.exception.BizCodeEnum;
import com.losca.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.losca.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R myExceptionHandler(MethodArgumentNotValidException e){
        Map<String, String> hashMap = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(item->{
            hashMap.put(item.getField(), item.getDefaultMessage());
        });
        log.error("数据校验出现问题{},异常类型：{}",e.getMessage(),e.getClass());
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(),BizCodeEnum.VALID_EXCEPTION.getMessage()).put("data",hashMap);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public R commonExceptionHandler(RuntimeException e){
        log.error("数据校验出现问题{},异常类型：{}",e.getMessage(),e.getClass());
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(),BizCodeEnum.UNKNOW_EXCEPTION.getMessage());
    }
}
