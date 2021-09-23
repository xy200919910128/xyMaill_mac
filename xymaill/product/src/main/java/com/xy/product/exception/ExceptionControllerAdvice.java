package com.xy.product.exception;

import com.xy.common.exception.BizCode;
import com.xy.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice("com.xy.maill.maillproduct.controller")
public class ExceptionControllerAdvice {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public R handlerArgumentValidException(MethodArgumentNotValidException validEcxeption) {
          BindingResult bindingResult = validEcxeption.getBindingResult();
          List<FieldError> errors = bindingResult.getFieldErrors();
          Map<String,Object> exceptionMap = new HashMap<>();
          errors.forEach((FieldError fieldError)->{
              exceptionMap.put(fieldError.getField(),fieldError.getDefaultMessage());
          });
          return R.error(BizCode.VALID_EXCEPTION.getCode(),BizCode.VALID_EXCEPTION.getMsg()).put("data",exceptionMap);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public R handlerRunTimeException(RuntimeException ecxeption) {
        ecxeption.printStackTrace();
        return R.error(BizCode.RUNTIME_EXCEPTION.getCode(),BizCode.RUNTIME_EXCEPTION.getMsg());
    }

    @ExceptionHandler(value = {Throwable.class})
    public R handlerException(Throwable ecxeption) {
        return R.error(BizCode.UNKNOWN_EXCEPTION.getCode(),BizCode.UNKNOWN_EXCEPTION.getMsg());
    }

}
