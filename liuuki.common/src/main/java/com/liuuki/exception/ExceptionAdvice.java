package com.liuuki.exception;

import com.liuuki.srb.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Component
@Slf4j
public class ExceptionAdvice {
    /**
     * 自定义异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class) // 当controller中抛出Exception就捕获异常
    public R exceptionHandler(Exception e){
        log.info(e.getMessage(),e);
        return R.error();
    }
}
