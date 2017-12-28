package com.lxg.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import com.lxg.springboot.mapper.UserMapper;
import com.lxg.springboot.model.Finance;
import com.lxg.springboot.model.Msg;
import com.lxg.springboot.model.Result;
import com.lxg.springboot.model.ResultUtil;
import com.lxg.springboot.model.User;
import com.lxg.springboot.service.HttpAPIService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
	@Resource
    private HttpAPIService httpAPIService;

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
    	if(userMapper.countboss(user)>=1){
    		List<User> userf ;
    		userf = userMapper.querybynoboss(user);
    		JSONObject json = new JSONObject();
    		json.put("returncode", "00");
    		json.put("returnmsg","登陆成功");
    		json.put("storeid",userf);
    		json.put("role",userf.get(0).getRole());
    		return json.toJSONString();
    	}
    	else{
    		JSONObject jsonA = new JSONObject();
    		jsonA.put("returncode", "01");
    		jsonA.put("returnmsg","登陆失败");
    		return jsonA.toJSONString();
    	}
    }    
    
    @RequestMapping("joinfinance")
    public Msg joinfinance(Finance finance) {
    	
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	String time=format.format(new Date());
    	
    	finance.setTime(time);
    	
    	userMapper.savefinance(finance); 	
    	
    	String userunion = userMapper.getunionid(finance.getOpenid());
    	User tempuser = new User();
		tempuser.setStoreid(finance.getStoreid());
		User Boss=userMapper.querybossbyid(tempuser);
		String bossunion = userMapper.getunionid(Boss.getOpenid());
		
		String ccid = "7590f31d29f96f08fef626eb2dca619012f21cfe211606db8447fe6a668d9f1d";
		String urla ="";
		String res="";
		
		urla = "https://store.lianlianchains.com/kd/invoke?func=transefer&" + "ccId=" + ccid + "&" + "usr=" + userunion	+ "&" + "acc=" + userunion + "&" + "reacc=" + bossunion  +  "&" + "amt=" + finance.getScore()+ "&tstp=积分理财投资&desc=积分理财投资" ;
		
		res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return ResultUtil.success();
    }  
    
    @RequestMapping("deletefinance")
    public Msg deletefinance(Finance finance) {
    	
    	int i =userMapper.deletefinance(finance);
    	if(i!=0){
    	String userunion = userMapper.getunionid(finance.getOpenid());
    	User tempuser = new User();
		tempuser.setStoreid(finance.getStoreid());
		User Boss=userMapper.querybossbyid(tempuser);
		String bossunion = userMapper.getunionid(Boss.getOpenid());
		
		String ccid = "7590f31d29f96f08fef626eb2dca619012f21cfe211606db8447fe6a668d9f1d";
		String urla ="";
		String res="";
		
		urla = "https://store.lianlianchains.com/kd/invoke?func=transefer&" + "ccId=" + ccid + "&" + "usr=" + bossunion	+ "&" + "acc=" + bossunion + "&" + "reacc=" + userunion +  "&" + "amt=" + finance.getScore() + "&tstp=积分理财提取&desc=积分理财提取" ;
		
		res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
    	return ResultUtil.success();
    } 
}
