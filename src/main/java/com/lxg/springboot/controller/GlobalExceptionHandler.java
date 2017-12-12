package com.lxg.springboot.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lxg.springboot.model.Msg;
import com.lxg.springboot.model.ResultUtil;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {
  
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Msg defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
    	 return ResultUtil.fail("系统异常");
    }
}