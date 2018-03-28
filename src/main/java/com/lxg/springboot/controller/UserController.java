package com.lxg.springboot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxg.springboot.mapper.ShopMapper;
import com.lxg.springboot.mapper.SkuMapper;
import com.lxg.springboot.mapper.UserMapper;
import com.lxg.springboot.model.Finance;
import com.lxg.springboot.model.IncreaseMoney;
import com.lxg.springboot.model.IncreasePage;
import com.lxg.springboot.model.Msg;
import com.lxg.springboot.model.Order;
import com.lxg.springboot.model.OrderAll;
import com.lxg.springboot.model.Result;
import com.lxg.springboot.model.ResultUtil;
import com.lxg.springboot.model.Shop;
import com.lxg.springboot.model.Union;
import com.lxg.springboot.model.User;
import com.lxg.springboot.service.HttpAPIService;
import com.lxg.springboot.util.CollectionUtil;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
	@Resource
    private SkuMapper skuMapper;
	@Resource
    private ShopMapper shopMapper;

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
		
		String ccid = "";
		String urla ="";
		String res="";
		
		urla = "https://store.lianlianchains.com/kd/invoke?func=buyFinance&ccId=" + ccid + "&" + "usr=" + userunion	+ "&" + "acc=" + userunion + "&"+ "rid=" + finance.getStoreid()  + "&fid=" + time +"&pee=" + bossunion  +  "&" + "amt=" + finance.getScore()  + "&desc=积分定期投资";
		
		res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*urla = "https://store.lianlianchains.com/kd/invoke?func=transefer&" + "ccId=" + ccid + "&" + "usr=" + userunion	+ "&" + "acc=" + userunion + "&" + "reacc=" + bossunion  +  "&" + "amt=" + finance.getScore()+ "&tstp=积分理财投资&desc=积分理财投资" ;
		
		res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	
    	
    	return ResultUtil.success();
    }  
    
    @RequestMapping("querytransfer")
    public Msg querytransfer(String unionto,String unionid,String score,String password) {
    		
		String ccid = "";
		String urla ="";
		String res="";
		
		urla ="https://store.lianlianchains.com/kd/query?func=transPreCheck&" + "ccId=" + ccid + "&" + "usr=" + unionid	+ "&" + "acc=" + unionid + "&" + "reacc=" + unionto +  "&" + "amt=" + score + "&tstp=积分交换&desc=积分交换&pwd="+ password;
		
		res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject json = JSON.parseObject(res);  
		String resc = json.getString("code");
		
		if (!resc.equals("0")){
			return ResultUtil.fail(res);
		}
    			
    	return ResultUtil.success(res);
    } 
    
    
    @RequestMapping("newredpack")
    public Msg newredpack(String unionid) {
    	
    	Union temp = new Union();
		temp.setUnionid(unionid);
		temp.setState(0);
		
		String uuid = CollectionUtil.generateUUID();
		
		temp.setUuid(uuid);
    			
		userMapper.saveunionstate(temp);
		
    	return ResultUtil.success(uuid);
    } 
    
    
    @RequestMapping("scoretransfer")
    public Msg scoretransfer(String unionto,String unionid,String score,String password) {
    		
		String ccid = "";
		String urla ="";
		String res="";
		
		urla ="https://store.lianlianchains.com/kd/invoke?func=transeferUsePwd&" + "ccId=" + ccid + "&" + "usr=" + unionid	+ "&" + "acc=" + unionid + "&" + "reacc=" + unionto +  "&" + "amt=" + score + "&tstp=积分交换&desc=积分交换&pwd="+ password;
		
		res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject json = JSON.parseObject(res);  
		String resc = json.getString("code");
		
		if (!resc.equals("0")){
			return ResultUtil.fail(res);
		}
    			
    	return ResultUtil.success(res);
    } 
    
    @RequestMapping("transferquery")
    public Msg transferquery(String unionto,String unionid,String amount) {
    		
		String ccid = "";
		String urla ="";
		String res="";
		
		urla ="https://store.lianlianchains.com/kd/query?func=transPreCheck&" + "ccId=" + ccid + "&" + "usr=" + unionid	+ "&" + "acc=" + unionid + "&" + "reacc=" + unionto +  "&" + "amt=" + amount + "&tstp=积分红包&desc=积分红包";
		
		res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject json = JSON.parseObject(res);  
		String resc = json.getString("code");
		
		if (!resc.equals("0")){
			return ResultUtil.fail(res);
		}
    			
    	return ResultUtil.success(res);
    } 
    
    @RequestMapping("transfer")
    public Msg transfer(String unionto,String unionid,String amount,String uuid) {
    		
		String ccid = "";
		String urla ="";
		String res="";
		
		Union temp = new Union();
		temp.setUuid(uuid);
		temp.setUnionid(unionid);
		temp.setUnionto(unionto);
		temp.setState(1);
		
		int flag = userMapper.getunionstate(temp);
		
		if (flag==0){
		urla ="https://store.lianlianchains.com/kd/invoke?func=transefer&" + "usr=" + unionid	+ "&" + "acc=" + unionid + "&" + "reacc=" + unionto+ "&" + "amt="+ amount + "&tstp=积分红包&desc=积分红包";
		
		res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResultUtil.fail(res);
		}
		
		JSONObject json = JSON.parseObject(res);  
		String resc = json.getString("code");
		
		if (!resc.equals("0")){
			return ResultUtil.fail(res);
		}
		
		userMapper.updateunionstate(temp);
		
		}
    			
    	return ResultUtil.success(flag);
    } 
    
    @RequestMapping("deletefinance")
    public Msg deletefinance(Finance finance) {
    	
    	int i =userMapper.deletecount(finance);
    	if(i!=0){
    	String userunion = userMapper.getunionid(finance.getOpenid());
    	User tempuser = new User();
		tempuser.setStoreid(finance.getStoreid());
		User Boss=userMapper.querybossbyid(tempuser);
		String bossunion = userMapper.getunionid(Boss.getOpenid());
		
		String ccid = "";
		String urla ="";
		String res="";
		
		urla = "https://store.lianlianchains.com/kd/invoke?func=payFinance&ccId=" + ccid + "&" + "usr=" + bossunion	+ "&" + "acc=" + bossunion + "&"+ "rid=" + finance.getStoreid() + "&pee=" + userunion  + "&desc=积分定期提取";
		
		res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject json = JSON.parseObject(res);  
		String resc = json.getString("code");
		
		if (!resc.equals("0")){
			return ResultUtil.fail("区块链连接错误");
		}
    	
		userMapper.deletefinance(finance);
		
		/*urla = "https://store.lianlianchains.com/kd/invoke?func=transefer&" + "ccId=" + ccid + "&" + "usr=" + bossunion	+ "&" + "acc=" + bossunion + "&" + "reacc=" + userunion +  "&" + "amt=" + finance.getScore() + "&tstp=积分理财提取&desc=积分理财提取" ;
		
		res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		}
		
    	return ResultUtil.success();
    } 
    
    @RequestMapping("resetPassword")
    public Msg resetPassword(String password,String openid) {
    	

    	String userunion = userMapper.getunionid(openid);
	
		String ccid = "";
		String urla ="";
		String res="";
		
		urla = "https://store.lianlianchains.com/kd/invoke?func=resetAccPwd&ccId=" + ccid + "&" + "usr=" + userunion	+ "&" + "acc=" + userunion + "&"+ "pwd=" + password ;
		
		res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject json = JSON.parseObject(res);  
		String resc = json.getString("code");
		
		if (!resc.equals("0")){
			return ResultUtil.fail("区块链连接错误");
		}

    	return ResultUtil.success(res);
    } 
    
    @Scheduled(cron = "0 0 1 * * ?" )
    @RequestMapping("financeThread")
    public Msg financeThread() throws IOException { 
    	Calendar c8 = Calendar.getInstance();
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	c8.setTime(new Date());
        c8.add(Calendar.DATE, - 1); 
        Date d8 = c8.getTime();
        String timed8=format.format(d8);        
        Calendar c1 = Calendar.getInstance();
        c1.setTime(new Date());
        c1.add(Calendar.DATE, - 1); 
        Date d1 = c1.getTime();
        String timed1=format.format(d1);     
        timed8 = userMapper.lasttime();
		String timeS = timed8 + "000000";
		String timeE = timed1 + "235959";
		
        String fid= timed8;
		
		List<Shop> shop;
		shop= shopMapper.query();
    	String temp = "";
    	Finance user = new Finance();
    	user.setTime(fid);
		Order order = new Order();
		for(int i=0;i<shop.size();i++){
			user.setStoreid(shop.get(i).getStoreId());
			int flag = userMapper.storecount(user);
			if(flag!=0){
    		if(!temp.equals("")){
    			temp = temp + ";";
    		}
        	order.setStartDate(timeS);
        	order.setEndDate(timeE);
        	order.setStoreid(shop.get(i).getStoreId());
    		int fee=skuMapper.allmoney(order);
        	temp = temp + shop.get(i).getStoreId()+":"+ fee;
			}
    		}
		
		if(!temp.equals("")){
		String ccid = "";
    	String urla = "https://store.lianlianchains.com/kd/invoke?func=financeBouns&ccId=" + ccid + "&" + "usr=centerBank&acc=centerBank&fid="+ fid + "&rscfg=" + temp;
		
		String res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject json = JSON.parseObject(res);  
		String resc = json.getString("code");
		
		if (!resc.equals("0")){
			return ResultUtil.fail("区块链连接错误");
		}
		}
    	
    	return ResultUtil.success();
		
    }
    
    
    @Scheduled(cron = "0 0 23 * * ?" )
    @RequestMapping("financeThreadend")
    public Msg financeThreadend() throws IOException { 
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
        String time = format.format(new Date());     

		String ccid = "";
    	String urla = "https://store.lianlianchains.com/kd/invoke?func=financeIssueFinish&ccId=" + ccid + "&" + "usr=centerBank&acc=centerBank&fid="+ time;
		
		String res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject json = JSON.parseObject(res);  
		String resc = json.getString("code");
		
		if (!resc.equals("0")){
			return ResultUtil.fail("区块链连接错误");
		}
    	
		userMapper.updatelasttime(time);
		
    	return ResultUtil.success();
		
    }
    
    @RequestMapping("queryalone")
    public Msg queryalone(String unionId,String storeId) throws IOException { 
    
		String ccid = "";
    	String urla = "https://store.lianlianchains.com/kd/query?func=getRackFinanceProfit&ccId=" + ccid + "&" + "usr=" + unionId	+ "&" + "acc=" + unionId + "&"+ "rid=" + storeId;
		
		
		String res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject json = JSON.parseObject(res);  
		String resc = json.getString("code");
		
		if (!resc.equals("0")){
			return ResultUtil.fail("区块链连接错误");
		}
    	
    	return ResultUtil.success(res);
		
    }
    
    @RequestMapping("querylimit")
    public Msg querylimit(String storeId) throws IOException { 
    	
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
        String time = format.format(new Date());     

    
		String ccid = "";
    	String urla = "https://store.lianlianchains.com/kd/query?func=getRackRestFinanceCapacity&ccId=" + ccid + "&" + "&usr=centerBank&acc=centerBank&"+ "rid=" + storeId + "&fid="+ time;
		
    	
		String res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject json = JSON.parseObject(res);  
		String resc = json.getString("code");
		
		if (!resc.equals("0")){
			return ResultUtil.fail("区块链连接错误");
		}
    	
    	return ResultUtil.success(res);
		
    }
}
