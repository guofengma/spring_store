package com.lxg.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import com.lxg.springboot.mapper.AccountMapper;
import com.lxg.springboot.model.Result;
import com.lxg.springboot.model.Account;
import com.lxg.springboot.model.Good;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

/**
 * Created by zhenghong
 * on 2017/4/25.
 */
@RestController
@RequestMapping("CVS/account/")
public class AccountController extends BaseController {
	
	@Resource
    private AccountMapper accountMapper;

    @RequestMapping("save")
    public Result save(Account account) {
    
    	accountMapper.save(account);
    	return new Result();
    }    
    
    
    @RequestMapping("query")
    public List<Account> query(Account account) {	
    	return accountMapper.query(account);  	
    } 
    
}
