package com.lemon.usercenterback.exception;

import com.lemon.usercenterback.common.BaseResponse;
import com.lemon.usercenterback.common.ErrorCode;
import com.lemon.usercenterback.common.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error("businessException:"+e.getMessage(),e);
        return ResultUtil.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    public BaseResponse runtimeExceptionHandler(RuntimeException e){
        log.error("runtimeException:"+e.getMessage(),e);
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }

}
