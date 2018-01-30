package com.lxg.springboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lxg.springboot.mapper.CommentMapper;
import com.lxg.springboot.mapper.GoodMapper;
import com.lxg.springboot.mapper.OrderMapper;
import com.lxg.springboot.mapper.ShopMapper;
import com.lxg.springboot.mapper.ShopOrderMapper;
import com.lxg.springboot.mapper.UserMapper;
import com.lxg.springboot.model.Comment;
import com.lxg.springboot.model.Good;
import com.lxg.springboot.model.HttpResult;
import com.lxg.springboot.model.Msg;
import com.lxg.springboot.model.Order;
import com.lxg.springboot.model.OrderAll;
import com.lxg.springboot.model.OrderGood;
import com.lxg.springboot.model.Result;
import com.lxg.springboot.model.ResultUtil;
import com.lxg.springboot.model.Shop;
import com.lxg.springboot.model.ShopOrder;
import com.lxg.springboot.model.User;
import com.lxg.springboot.model.UserBoss;
import com.lxg.springboot.service.HttpAPIService;
import com.lxg.springboot.util.CheckSumBuilder;
import com.lxg.springboot.util.MatrixToImageWriter;
import com.lxg.springboot.util.RandomStringGenerator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zhenghong
 * on 2017/4/25.
 */
@RestController
public class ShopController{
	private static final  double EARTH_RADIUS = 6378137;
	
	@Resource
    private ShopMapper shopMapper;
	@Resource
    private OrderMapper orderMapper;
	@Resource
    private GoodMapper goodMapper;
	@Resource
    private CommentMapper commentMapper;
	@Resource
    private UserMapper userMapper;
	@Resource
    private ShopOrderMapper shopOrderMapper;
	@Value("${sms.appKey}")
    private String smsappKey;
    @Value("${sms.appSecret}")
    private String smsappSecret; 
    @Resource
	private HttpAPIService httpAPIService;
    
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
	    
    @RequestMapping("CVS/shopinsert")
    public Result getshop(Shop shop){
    	shopMapper.insert(shop);
    	return new Result();
    }
    
    @RequestMapping("CVS/getshop")
    public Shop getshop(double lat,double lng) throws Exception {
    	List<Shop> shop;  
    	shop = shopMapper.query(); 
    	for(int i=0 ;i<shop.size();i++){
    		double dis= GetDistance(lng,lat,shop.get(i).getLng(),shop.get(i).getLat());
    		if(dis<1000){
    		return  shop.get(i);   		
    		}
    	}
    	Shop temp = new Shop();  
    	temp.setStoreName("对不起，附近不存在店面");
    	return temp;
    }
    
    @RequestMapping("CVS/shopfirst")
    public Msg shopfirst(String phoneno) throws Exception {
    	User temp3=new User();
    	temp3.setPhoneno(phoneno);
    	int i = userMapper.countbossp(temp3);
       
    	if (i>=1){
    		return ResultUtil.success(1);	
    	}
    	else{
    		return ResultUtil.success(0);
    	}
    	         
    }
    
    @RequestMapping("CVS/discount")
    public Msg discount(String storeid) throws Exception {
    	Shop temp = new Shop();
    	temp = shopMapper.querybyid(storeid);
    		
    	return ResultUtil.success(temp.getDiscount());
    	
    	         
    }
     
    @RequestMapping("CVS/address")
    public Msg address(Shop temp) throws Exception {

    	shopMapper.update(temp);
    		
    	return ResultUtil.success();
    	
    	         
    }
    
    @RequestMapping("CVS/shopopen")
    public Msg register(String storeid,String phoneno,String password) throws Exception {
    	User temp=new User();
    	User temp1=new User();
    	temp.setStoreid(storeid);
    	temp.setRole("boss");
    	temp1 = userMapper.querybossrole(temp);
    	if (temp1.getPhoneno().equals(phoneno)){
    		 return ResultUtil.success();
    	}
    	
    	
    	User user=new User();
    	user.setPhoneno(phoneno);
    	user.setPassword(password);
    	user.setStoreid(storeid);
    	user.setRole("operator");
    	try{
    	userMapper.saveboss(user);
    	}
    	catch (Exception e) {
    		userMapper.updatebossbyphone(user);
        }
    	    
        return ResultUtil.success();
    }
    
    @RequestMapping("CVS/total")
    public String total(Order order) throws Exception {
    	List<Order> allOrder; 
    	Date date=new Date();
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
		String time=format.format(date);
		String timeS = time + "000000";
		String timeE = time + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	allOrder=shopMapper.totle(order);
    	int count=0;
    	double fee=0;
    	
    	for(int j =0;j<allOrder.size();j++){
    		if(allOrder.get(j).getState()==1){
    		List<OrderGood> temp;
        	temp=orderMapper.queryGood(allOrder.get(j));
        	for(int i =0;i<temp.size();i++){
        	count = count + temp.get(i).getAmount();
        	}
        	fee = fee + allOrder.get(j).getFee()*100;
    	}
    	}
		JSONObject json = new JSONObject();
		json.put("count", count);
		json.put("totlefee", fee/100);
		
		return json.toJSONString();

    }
    

    
    @RequestMapping("CVS/totallastday")
    public String totallastday(Order order) throws Exception {
    	List<Order> allOrder; 
    	Calendar date = Calendar.getInstance();
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	date.add(Calendar.DATE, -1);
		String time=format.format(date.getTime());
		String timeS = time + "000000";
		String timeE = time + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	allOrder=shopMapper.totle(order);
    	int count=0;
    	double fee=0;
    	
    	for(int j =0;j<allOrder.size();j++){
    		if(allOrder.get(j).getState()==1){
    		List<OrderGood> temp;
        	temp=orderMapper.queryGood(allOrder.get(j));
        	for(int i =0;i<temp.size();i++){
        	count = count + temp.get(i).getAmount();
        	}
        	fee = fee + allOrder.get(j).getFee();
    	}
    	}
    	


		JSONObject json = new JSONObject();
		json.put("count", count);
		json.put("totlefee", fee);
		
		return json.toJSONString();

    }
    
    @RequestMapping("CVS/totallastseven")
    public String totallastseven(Order order) throws Exception {
    	List<Order> allOrder; 
    	Calendar date = Calendar.getInstance();
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	date.add(Calendar.DATE, -8);
		String time=format.format(date.getTime());
		String timeS = time + "000000";
		Calendar datenow = Calendar.getInstance();
		datenow.add(Calendar.DATE, -1);
		String timenow=format.format(datenow.getTime());
		String timeE = timenow + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	allOrder=shopMapper.totle(order);
    	int count=0;
    	double fee=0;
    	
    	for(int j =0;j<allOrder.size();j++){
    		if(allOrder.get(j).getState()==1){
    		List<OrderGood> temp;
        	temp=orderMapper.queryGood(allOrder.get(j));
        	for(int i =0;i<temp.size();i++){
        	count = count + temp.get(i).getAmount();
        	}
        	fee = fee + allOrder.get(j).getFee()*100;
    	}
    	}
    	

		JSONObject json = new JSONObject();
		json.put("count", count);
		json.put("totlefee", fee/100);
		
		return json.toJSONString();

    }
    
    @RequestMapping("CVS/totallastmonth")
    public String totallastmonth(Order order) throws Exception {
    	List<Order> allOrder; 
    	Calendar date = Calendar.getInstance();
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	date.add(Calendar.MONTH, -1);
    	date.add(Calendar.DATE, -1);
		String time=format.format(date.getTime());
		String timeS = time + "000000";
		Calendar datenow = Calendar.getInstance();
		datenow.add(Calendar.DATE, -1);
		String timenow=format.format(datenow.getTime());
		String timeE = timenow + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	allOrder=shopMapper.totle(order);
    	int count=0;
    	double fee=0;
    	
    	for(int j =0;j<allOrder.size();j++){
    		if(allOrder.get(j).getState()==1){
    		List<OrderGood> temp;
        	temp=orderMapper.queryGood(allOrder.get(j));
        	for(int i =0;i<temp.size();i++){
        	count = count + temp.get(i).getAmount();
        	}
        	fee = fee + allOrder.get(j).getFee()*100;
    	}
    	}
    	

		JSONObject json = new JSONObject();
		json.put("count", count);
		json.put("totlefee", fee/100);
		
		return json.toJSONString();

    }
    
    @RequestMapping("CVS/usertotallastseven")
    public String usertotallastseven(UserBoss order) throws Exception {
    	List<OrderAll> allOrder; 
    	Calendar date = Calendar.getInstance();
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	date.add(Calendar.DATE, -8);
		String time=format.format(date.getTime());
		String timeS = time + "000000";
		Calendar datenow = Calendar.getInstance();
		datenow.add(Calendar.DATE, -1);
		String timenow=format.format(datenow.getTime());
		String timeE = timenow + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	allOrder=shopMapper.usermoney(order);
    	int count=0;
    	double fee=0;
    	double temp = 0;
    	
    	for(int i =0;i<allOrder.size();i++){
        	count = count + allOrder.get(i).getAmount();
        	temp = allOrder.get(i).getPrice() * 100;
        	fee = fee + allOrder.get(i).getAmount()*temp;
        	}   
    	
		JSONObject json = new JSONObject();
		User tempUser = new User();
    	tempUser.setPhoneno(order.getPhoneno());
    	List<User> userf ;
		Order tempOrder = new Order();
		userf = userMapper.querybynoboss(tempUser);
		for(int i = 0 ; i< userf.size();i++){
			tempOrder.setStoreid(userf.get(i).getStoreid());
			String ans = totallastseven(tempOrder);
			json.put(userf.get(i).getStoreid(),ans);		
			json.put(userf.get(i).getStoreid()+ "name",shopMapper.querybyid(userf.get(i).getStoreid()));
		}
		json.put("count", count);
		json.put("totlefee", fee/100);
    			
		return json.toJSONString();

    }
    
    
    @RequestMapping("CVS/userchart")
    public String userchart(UserBoss order) throws Exception {
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	Calendar d28 = Calendar.getInstance();
    	d28.setTime(new Date());
    	d28.add(Calendar.DATE, - 29); 
        Date date28 = d28.getTime();
        String time28=format.format(date28);  

    	Calendar d21 = Calendar.getInstance();
    	d21.setTime(new Date());
    	d21.add(Calendar.DATE, - 22); 
        Date date21 = d21.getTime();
        String time21=format.format(date21);
        
    	Calendar d14 = Calendar.getInstance();
    	d14.setTime(new Date());
    	d14.add(Calendar.DATE, - 15); 
        Date date14 = d14.getTime();
        String time14=format.format(date14);
         
    	Calendar d7 = Calendar.getInstance();
    	d7.setTime(new Date());
    	d7.add(Calendar.DATE, - 8); 
        Date date7 = d7.getTime();
        String time7=format.format(date7);
    	 
        Calendar datenow = Calendar.getInstance();
		datenow.add(Calendar.DATE, -1);
    	String timenow=format.format(datenow.getTime());
    	
    	List<OrderAll> allOrder; 
		String timeS = time28 + "235959";
		String timeE = time21 + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	allOrder=shopMapper.usermoney(order);
    	int count=0;
    	double fee=0;
    	double temp=0;
    	
    	for(int i =0;i<allOrder.size();i++){
        	count = count + allOrder.get(i).getAmount();
        	temp = allOrder.get(i).getPrice() * 100;
        	fee = fee + allOrder.get(i).getAmount()*temp;
        	}   
    	
		JSONObject json = new JSONObject();
		json.put("time1",time28 + "~" + time21);
		json.put("count1", count);
		json.put("totlefee1", fee/100);
		
		timeS = time21 + "235959";
		timeE = time14 + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	allOrder=shopMapper.usermoney(order);
    	count=0;
    	fee=0;
    	temp=0;
    	
    	for(int i =0;i<allOrder.size();i++){
        	count = count + allOrder.get(i).getAmount();
        	temp = allOrder.get(i).getPrice() * 100;
        	fee = fee + allOrder.get(i).getAmount()*temp;
        	}   
    	
    	json.put("time2",time21 + "~" + time14);
		json.put("count2", count);
		json.put("totlefee2", fee/100);
		
    	timeS = time14 + "235959";
		timeE = time7 + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	allOrder=shopMapper.usermoney(order);
    	count=0;
    	fee=0;
    	temp=0;
    	
    	for(int i =0;i<allOrder.size();i++){
        	count = count + allOrder.get(i).getAmount();
        	temp = allOrder.get(i).getPrice() * 100;
        	fee = fee + allOrder.get(i).getAmount()*temp;
        	}   
    	
    	json.put("time3",time14 + "~" + time7);
		json.put("count3", count);
		json.put("totlefee3", fee/100);
		
    	timeS = time7 + "235959";
		timeE = timenow + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	allOrder=shopMapper.usermoney(order);
    	count=0;
    	fee=0;
    	temp=0;
    	
    	for(int i =0;i<allOrder.size();i++){
        	count = count + allOrder.get(i).getAmount();
        	temp = allOrder.get(i).getPrice() * 100;
        	fee = fee + allOrder.get(i).getAmount()*temp;
        	}     
    	
    	json.put("time4",time7 + "~" + timenow);
		json.put("count4", count);
		json.put("totlefee4", fee/100);
		
		return json.toJSONString();

    }
    
    
    @RequestMapping("CVS/usertotal")
    public String usertotal(UserBoss order) throws Exception {
    	List<OrderAll> allOrder; 
    	Date date=new Date();
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
		String time=format.format(date);
		String timeS = time + "000000";
		String timeE = time + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	allOrder=shopMapper.usermoney(order);
    	int count=0;
    	double fee=0;
    	double temp = 0;
    	
    	for(int i =0;i<allOrder.size();i++){
        	count = count + allOrder.get(i).getAmount();
        	temp = allOrder.get(i).getPrice() * 100;
        	fee = fee + allOrder.get(i).getAmount()*temp;
        	}   
    	
		JSONObject json = new JSONObject();
		User tempUser = new User();
    	tempUser.setPhoneno(order.getPhoneno());
    	List<User> userf ;
		Order tempOrder = new Order();
		userf = userMapper.querybynoboss(tempUser);
		for(int i = 0 ; i< userf.size();i++){
			tempOrder.setStoreid(userf.get(i).getStoreid());
			String ans = total(tempOrder);
			json.put(userf.get(i).getStoreid(),ans);	
			json.put(userf.get(i).getStoreid()+ "name",shopMapper.querybyid(userf.get(i).getStoreid()));
		}
		json.put("count", count);
		json.put("totlefee", fee/100);
		
		return json.toJSONString();

    }
    
    
    @RequestMapping("CVS/usertotallastday")
    public String usertotallastday(UserBoss order) throws Exception {
    	List<OrderAll> allOrder; 
    	Calendar date = Calendar.getInstance();
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	date.add(Calendar.DATE, -1);
		String time=format.format(date.getTime());
		String timeS = time + "000000";
		String timeE = time + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	allOrder=shopMapper.usermoney(order);
    	int count=0;
    	double fee=0;
    	double temp = 0;
    	
    	for(int i =0;i<allOrder.size();i++){
        	count = count + allOrder.get(i).getAmount();
        	temp = allOrder.get(i).getPrice() * 100;
        	fee = fee + allOrder.get(i).getAmount()*temp;
        	}   
    	
		JSONObject json = new JSONObject();
		json.put("count", count);
		json.put("totlefee", fee/100);
		return json.toJSONString();

    }
    
    @RequestMapping("CVS/usertotallastmonth")
    public String usertotallastmonth(UserBoss order) throws Exception {
    	List<OrderAll> allOrder; 
    	Calendar date = Calendar.getInstance();
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	date.add(Calendar.MONTH, -1);
    	date.add(Calendar.DATE, -1);
		String time=format.format(date.getTime());
		Calendar datenow = Calendar.getInstance();
		datenow.add(Calendar.DATE, -1);
		String timenow=format.format(datenow.getTime());
		String timeS = time + "000000";
		String timeE = timenow + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	allOrder=shopMapper.usermoney(order);
    	int count=0;
    	double fee=0;
    	double temp = 0;
    	
    	for(int i =0;i<allOrder.size();i++){
        	count = count + allOrder.get(i).getAmount();
        	temp = allOrder.get(i).getPrice() * 100;
        	fee = fee + allOrder.get(i).getAmount()*temp;
        	}  
    	
    	JSONObject json = new JSONObject();
    	User tempUser = new User();
    	tempUser.setPhoneno(order.getPhoneno());
    	List<User> userf ;
		Order tempOrder = new Order();
		userf = userMapper.querybynoboss(tempUser);
		for(int i = 0 ; i< userf.size();i++){
			tempOrder.setStoreid(userf.get(i).getStoreid());
			String ans = totallastmonth(tempOrder);
			json.put(userf.get(i).getStoreid(),ans);	
			json.put(userf.get(i).getStoreid()+ "name",shopMapper.querybyid(userf.get(i).getStoreid()));
		}
		
		json.put("count", count);
		json.put("totlefee", fee/100);
		return json.toJSONString();

    }
    
    
    
    @RequestMapping("CVS/goodcontrol")
    public String goodcontrol(Good good){
    	good.setTotal(good.getAmount());
    	Good temp = goodMapper.querybyCode(good);
    	if (temp==null){
    		goodMapper.insert(good);
    		return "添加成功";
    	}
    	else{
    		goodMapper.update(good);
    		return "更新成功";
    	}
    	  	
    }  
    
    @RequestMapping("CVS/allgood")
    public String allgood(Good good){	
    	List<Good> returngood;
    	returngood=goodMapper.queryall(good);
    	int amount=0;
    	double fee=0;
    	for(int j =0;j<returngood.size();j++){
    		amount = amount + returngood.get(j).getAmount();
    		fee = fee + returngood.get(j).getAmount()* returngood.get(j).getPrice();
    	} 	
    	
    	JSONObject json = new JSONObject();
		json.put("count", amount);
		json.put("totle", fee);
		
		return json.toJSONString();
    }  
    
    @RequestMapping("CVS/comment")
    public String comment(Comment comment){	
    	commentMapper.save(comment);
    	
		return "评论成功";
    }
    
    @RequestMapping("CVS/amountrank")
    public List<OrderGood> amountrank(OrderGood good){	
    	List<OrderGood> returngood;
    	returngood=goodMapper.amountrank(good);
    		
		return returngood;
    }  
    
    @RequestMapping("CVS/moneyrank")
    public List<OrderGood> moneyrank(OrderGood good){	
    	List<OrderGood> returngood;
    	returngood=goodMapper.moneyrank(good);
    		
		return returngood;
    }  
    
    @RequestMapping("CVS/issueOrder")
    public Msg issueOrder(ShopOrder shopOrder){	
    	int i = shopOrderMapper.querybyidcount(shopOrder);
    	if (i>=1){
    		return ResultUtil.fail("已有发起订单");
    	}
    	shopOrderMapper.insert(shopOrder);;
    	shopOrderMapper.updatestateonly(shopOrder);
    	shopOrderMapper.updatestate(shopOrder);
    	
    	return ResultUtil.success();
    }
    
    @RequestMapping("CVS/editOrder")
    public Msg editOrder(ShopOrder shopOrder){	
    	shopOrderMapper.update(shopOrder);
    	
    	return ResultUtil.success();
    }
    
    @RequestMapping("CVS/getorder")
    public Msg getorder(ShopOrder shopOrder) throws Exception{	
    	shopOrder.setServicestate(2);
    	shopOrderMapper.updatestateorder(shopOrder);
    	shopOrderMapper.updatestate(shopOrder);
    	
    	String url = "https://api.netease.im/sms/sendtemplate.action";
    	String appKey = smsappKey;
        String appSecret = smsappSecret;
        String nonce = RandomStringGenerator.getRandomStringByLength(6);
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce ,curTime);
        
        // 参数
        HashMap<String, Object> map = new HashMap<String, Object>();
        JSONArray phone = new JSONArray();
        User temp = new User();
        temp.setStoreid(shopOrder.getStoreId());
        User boss = new User();
        boss = userMapper.querybossbyid(temp);
        phone.add(boss.getPhoneno());
        
        
        JSONArray param = new JSONArray();
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        String timed=format.format(new Date());   
        param.add(timed);
        
        map.put("mobiles", phone);
        map.put("templateid", 3972218);
        map.put("params", param);
           
        logger.warn("发送短信参数" + map);
        // 请求头
        HashMap<String, Object> header = new HashMap<String, Object>();
        header.put("AppKey", appKey);
        header.put("Nonce", nonce);
        header.put("CurTime", curTime);
        header.put("CheckSum", checkSum);
        
        HttpResult res = httpAPIService.doPost(url, map, header);
        
        logger.warn("发送短信结果" + res);
    	
    	return ResultUtil.success();
    }
    
    @RequestMapping("CVS/cancelorder")
    public Msg cancelorder(ShopOrder shopOrder){	
    	shopOrder.setServicestate(1);
    	shopOrder.setOpenid("");
    	shopOrderMapper.updatestateorder(shopOrder);
    	shopOrderMapper.updatestate(shopOrder);
    	
    	return ResultUtil.success();
    }
    
    @RequestMapping("CVS/querybypos")
    public Msg querybypos(ShopOrder shopOrder){	
    	double lat = shopOrder.getLat();
    	double lng = shopOrder.getLng();
    	shopOrder.setLat1(lat-0.05);
    	shopOrder.setLat2(lat+0.05);
    	
    	shopOrder.setLng1(lng-0.07);
    	shopOrder.setLng2(lng+0.07);
    	
    	return ResultUtil.success(shopOrderMapper.querybypos(shopOrder));
    }
    
    
    
    @RequestMapping("CVS/querybyopenid")
    public Msg querybyopenid(ShopOrder shopOrder){
    	
    	return ResultUtil.success(shopOrderMapper.querybyopenid(shopOrder));
    }
    
    @RequestMapping("CVS/queryall")
    public Msg queryall(ShopOrder shopOrder){
    	
    	return ResultUtil.success(shopOrderMapper.queryall(shopOrder));
    }
    
    @RequestMapping("CVS/queryallname")
    public Msg queryallname(ShopOrder shopOrder){
    	
    	return ResultUtil.success(shopOrderMapper.queryallname(shopOrder));
    }
    
    @RequestMapping("CVS/querybyid")
    public ShopOrder querybyid(ShopOrder shopOrder){	
    	
    	
    	return shopOrderMapper.querybyid(shopOrder);
    }
    
    @RequestMapping("CVS/updateimage")
    public Msg updateimage(ShopOrder shopOrder){	
    	
    	
    	shopOrderMapper.updateimage(shopOrder);
    	shopOrderMapper.updatestate(shopOrder);
    	
    	return ResultUtil.success();
    }
    
    @RequestMapping("CVS/updatestorestate")
    public Msg updatestorestate(ShopOrder shopOrder){	
    	
    	shopOrderMapper.updatestateonly(shopOrder);
    	shopOrderMapper.updatestate(shopOrder);
    	
    	return ResultUtil.success();
    }
    
    @RequestMapping("CVS/delete")
    public Msg delete(ShopOrder shopOrder){	
    	shopOrder.setServicestate(0);
    	shopOrderMapper.delete(shopOrder);
    	shopOrderMapper.updatestate(shopOrder);
    	
    	return ResultUtil.success();
    }
    
    @RequestMapping("CVS/finish")
    public Msg finish(ShopOrder shopOrder) throws Exception{	
    	
    	shopOrderMapper.updateimage(shopOrder);
    	
    //	User temp = new User();
    //	temp.setStoreid(shopOrder.getStoreId());
    //	temp.setPhoneno(shopOrder.getPhone());
    	
    //	userMapper.deleteauth(temp);
    	
    	String url = "https://api.netease.im/sms/sendtemplate.action";
    	String appKey = smsappKey;
        String appSecret = smsappSecret;
        String nonce = RandomStringGenerator.getRandomStringByLength(6);
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce ,curTime);
        
        // 参数
        HashMap<String, Object> map = new HashMap<String, Object>();
        JSONArray phone = new JSONArray();
        User temp = new User();
        temp.setStoreid(shopOrder.getStoreId());
        User boss = new User();
        boss = userMapper.querybossbyid(temp);
        phone.add(boss.getPhoneno());
        
        
        JSONArray param = new JSONArray();
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        String timed=format.format(new Date());   
        param.add(timed);
        
        map.put("mobiles", phone);
        map.put("templateid", 3942243);
        map.put("params", param);
           
        logger.warn("发送短信参数" + map);
        // 请求头
        HashMap<String, Object> header = new HashMap<String, Object>();
        header.put("AppKey", appKey);
        header.put("Nonce", nonce);
        header.put("CurTime", curTime);
        header.put("CheckSum", checkSum);
        
        HttpResult res = httpAPIService.doPost(url, map, header);
        
        logger.warn("发送短信结果" + res);
    	
    	return ResultUtil.success();
    }
    
    @RequestMapping("CVS/finishtofour")
    public Msg finishtofour(ShopOrder shopOrder){	
    	
    	shopOrderMapper.updatestateonly(shopOrder);
    	shopOrderMapper.updatestate(shopOrder);
    	
    	User temp = new User();
    	temp.setStoreid(shopOrder.getStoreId());
    	temp.setPhoneno(shopOrder.getPhone());
    	
    	userMapper.deleteauth(temp);
    	
    	return ResultUtil.success();
    }
    
    
    /** 
     * 转化为弧度(rad) 
     * */  
    private static double rad(double d)  
    {  
       return d * Math.PI / 180.0;  
    }  
    /** 
     * 基于googleMap中的算法得到两经纬度之间的距离,计算精度与谷歌地图的距离精度差不多，相差范围在0.2米以下 
     * @param lon1 第一点的精度 
     * @param lat1 第一点的纬度 
     * @param lon2 第二点的精度 
     * @param lat3 第二点的纬度 
     * @return 返回的距离，单位km 
     * */  
    public static double GetDistance(double lon1,double lat1,double lon2, double lat2)  
    {  
    	 double radLat1 = rad(lat1);  
         double radLat2 = rad(lat2);  
   
         double radLon1 = rad(lon1);  
         double radLon2 = rad(lon2);  
   
         if (radLat1 < 0)  
             radLat1 = Math.PI / 2 + Math.abs(radLat1);// south  
         if (radLat1 > 0)  
             radLat1 = Math.PI / 2 - Math.abs(radLat1);// north  
         if (radLon1 < 0)  
             radLon1 = Math.PI * 2 - Math.abs(radLon1);// west  
         if (radLat2 < 0)  
             radLat2 = Math.PI / 2 + Math.abs(radLat2);// south  
         if (radLat2 > 0)  
             radLat2 = Math.PI / 2 - Math.abs(radLat2);// north  
         if (radLon2 < 0)  
             radLon2 = Math.PI * 2 - Math.abs(radLon2);// west  
         double x1 = EARTH_RADIUS * Math.cos(radLon1) * Math.sin(radLat1);  
         double y1 = EARTH_RADIUS * Math.sin(radLon1) * Math.sin(radLat1);  
         double z1 = EARTH_RADIUS * Math.cos(radLat1);  
   
         double x2 = EARTH_RADIUS * Math.cos(radLon2) * Math.sin(radLat2);  
         double y2 = EARTH_RADIUS * Math.sin(radLon2) * Math.sin(radLat2);  
         double z2 = EARTH_RADIUS * Math.cos(radLat2);  
   
         double d = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)+ (z1 - z2) * (z1 - z2));  
         //余弦定理求夹角  
         double theta = Math.acos((EARTH_RADIUS * EARTH_RADIUS + EARTH_RADIUS * EARTH_RADIUS - d * d) / (2 * EARTH_RADIUS * EARTH_RADIUS));  
         double dist = theta * EARTH_RADIUS;  
         return dist;  
    }
}
