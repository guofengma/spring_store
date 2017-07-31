package com.lxg.springboot.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.lxg.springboot.model.Result;

/**
 * Created by zhenghong
 * on 2017/4/25.
 */
@RestController
public class BaseController {
		
	@ExceptionHandler
	public Result exp(Exception ex) {  
		
		Result re = new Result();
        
		re.setEc("999999");
		re.setEm(ex.getMessage());
		
		return re;
    }  
}
