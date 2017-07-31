package com.lxg.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import com.lxg.springboot.mapper.UserMapper;
import com.lxg.springboot.model.Result;
import com.lxg.springboot.model.User;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by zhenghong
 * on 2017/4/25.
 */
@RestController
@RequestMapping("CVS/user/")
public class UserController extends BaseController {
	
	@Resource
    private UserMapper userMapper;

    @RequestMapping("save")
    public Result save(User user) {
    
    	userMapper.save(user);
    	return new Result();
    }    
    
    @RequestMapping("update")
    public Result update(User user) {
    	
    	// 用户数据存储
    	userMapper.update(user);
    	return new Result();
    }
    
    @RequestMapping("query")
    public User query(User user) {
    	
    	User userf = userMapper.query(user);
    	return userf;  	
    }  
    
    @RequestMapping("saveboss")
    public Result saveboss(User user) {
    
    	userMapper.saveboss(user);
    	return new Result();
    }    
    
    @RequestMapping("updateboss")
    public Result updateboss(User user) {
    	
    	// 用户数据存储
    	userMapper.updateboss(user);
    	return new Result();
    }
    
    @RequestMapping("queryboss")
    public User queryboss(User user) {
    	
    	User userf = userMapper.queryboss(user);
    	return userf;  	
    }  
    
    @RequestMapping("login")
    public String login(User user) {    	
    	if(userMapper.countboss(user)==1){
    		User userf = userMapper.querybynoboss(user);
    		JSONObject json = new JSONObject();
    		json.put("returncode", "00");
    		json.put("returnmsg","登陆成功");
    		json.put("storeid",userf.getStoreid());
    		return json.toJSONString();
    	}
    	else{
    		JSONObject jsonA = new JSONObject();
    		jsonA.put("returncode", "01");
    		jsonA.put("returnmsg","登陆失败");
    		return jsonA.toJSONString();
    	}
    }    
}
