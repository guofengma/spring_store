package com.lxg.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import com.lxg.springboot.mapper.GoodMapper;
import com.lxg.springboot.mapper.OrderMapper;
import com.lxg.springboot.mapper.ShopMapper;
import com.lxg.springboot.mapper.SkuMapper;
import com.lxg.springboot.model.Cart;
import com.lxg.springboot.model.Good;
import com.lxg.springboot.model.Intea;
import com.lxg.springboot.model.Order;
import com.lxg.springboot.model.OrderGood;
import com.lxg.springboot.model.Result;
import com.lxg.springboot.model.Sku;
import com.lxg.springboot.util.CharacterUtil;
import com.lxg.springboot.model.OrderAll;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

/**
 * Created by xuhuadong
 */
@RestController
@RequestMapping("CVS/sku/")
public class SkuController extends BaseController {
	
	@Resource
    private SkuMapper skuMapper;
	@Resource
    private ShopMapper shopMapper;
	@Resource
    private OrderMapper orderMapper;
	@Resource
	private GoodMapper goodMapper;
     
    @RequestMapping("queryall")
    public List<Sku> query(Sku sku) {
    	Calendar c = Calendar.getInstance();
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	c.setTime(new Date());
        c.add(Calendar.DATE, - 7); 
        Date d = c.getTime();
        String timed=format.format(d);       
    	List<OrderAll> allOrder; 
    	Date date=new Date();
    	Order order = new Order();
		String time=format.format(date);
		String timeS = timed + "000000";
		String timeE = time + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	order.setStoreid(sku.getStoreid());
    	allOrder=skuMapper.queryorder(order);
    	int count=0;
    	double fee=0;
    	Good good = new Good();
    	good.setStoreid(sku.getStoreid());
    	List<Good> temp;
    	temp = goodMapper.queryall(good);
    	String tempcode="";
    	Sku skuAf = new Sku();
    	int set;
    	int sell;
    	skuMapper.delete(sku);
    	skuMapper.deletedown(sku);
    	for(int i =0;i<temp.size();i++){
    		count = 0;
    		set = 0;
        	tempcode = temp.get(i).getCode();
        	sell= temp.get(i).getAmount();
        	for(int j =0;j<allOrder.size();j++){
            	if(allOrder.get(j).getCode().equals(tempcode)){
            	count = count + allOrder.get(j).getAmount();
            	}
            }
        	if(count>sell){
        		set = (count - sell) * 2;
        		skuAf.setAmount(sell);
        		skuAf.setCode(tempcode);
        		skuAf.setName(temp.get(i).getName());
        		skuAf.setSpecifi(temp.get(i).getSpecifi());
        		skuAf.setTotal(count + sell);
        		skuAf.setStoreid(temp.get(i).getStoreid());
        		skuAf.setPrice(temp.get(i).getPrice());
        		skuAf.setTag(0);
        		skuAf.setSkuamount(set);  
        		skuMapper.insert(skuAf);
        	}
        	else if (count * 2 > sell){
        		set = count * 2 - sell;
        		skuAf.setAmount(sell);
        		skuAf.setCode(tempcode);
        		skuAf.setName(temp.get(i).getName());
        		skuAf.setSpecifi(temp.get(i).getSpecifi());
        		skuAf.setTotal(count + sell);
        		skuAf.setStoreid(temp.get(i).getStoreid());
        		skuAf.setPrice(temp.get(i).getPrice());
        		skuAf.setTag(0);
        		skuAf.setSkuamount(set); 
        		skuMapper.insert(skuAf);
        	}
        	else if (count*3 < sell){
        		set = sell - count * 2;
        		skuAf.setAmount(sell);
        		skuAf.setCode(tempcode);
        		skuAf.setName(temp.get(i).getName());
        		skuAf.setSpecifi(temp.get(i).getSpecifi());
        		skuAf.setTotal(count + sell);
        		skuAf.setStoreid(temp.get(i).getStoreid());
        		skuAf.setPrice(temp.get(i).getPrice());
        		skuAf.setTag(0);
        		skuAf.setSkuamount(set);    
        		skuMapper.insertdown(skuAf);
        	}
        }   
    	return skuMapper.queryall(sku);  	
    } 
    
    @RequestMapping("history")
    public Result history(Sku sku) {	
    	skuMapper.inserthistory(sku);
    	return new Result();	
    } 
    
    @RequestMapping("queryalldown")
    public List<Sku> queryalldown(Sku sku) {	
    	return skuMapper.queryalldown(sku);  	
    } 
    
    @RequestMapping("query")
    public Sku queryReferee(Sku sku) {
      		
    	Sku returnsku = new Sku();
    	returnsku=skuMapper.query(sku);
    	return returnsku;  	
    }  

    @RequestMapping("querybyCode")
    public Sku querybyCode(Sku sku) {
      		
    	Sku returnsku = new Sku();
    	returnsku=skuMapper.querybyCode(sku);
    	return returnsku;  	
    }  
    
    @RequestMapping("update")
    public Result update(Sku sku) {
      		
    skuMapper.update(sku);
    return new Result();
    }  
    
    @RequestMapping("updatedown")
    public Result updatedown(Sku sku) {
      		
    skuMapper.updatedown(sku);
    return new Result();
    } 
    
    @RequestMapping("chart")
    public String chart(OrderGood orderGood) throws Exception {
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	Calendar d28 = Calendar.getInstance();
    	d28.setTime(new Date());
    	d28.add(Calendar.DATE, - 28); 
        Date date28 = d28.getTime();
        String time28=format.format(date28);  

    	Calendar d21 = Calendar.getInstance();
    	d21.setTime(new Date());
    	d21.add(Calendar.DATE, - 21); 
        Date date21 = d21.getTime();
        String time21=format.format(date21);
        
    	Calendar d14 = Calendar.getInstance();
    	d14.setTime(new Date());
    	d14.add(Calendar.DATE, - 14); 
        Date date14 = d14.getTime();
        String time14=format.format(date14);
         
    	Calendar d7 = Calendar.getInstance();
    	d7.setTime(new Date());
    	d7.add(Calendar.DATE, - 7); 
        Date date7 = d7.getTime();
        String time7=format.format(date7);
    	
    	Date date=new Date();
    	String timenow=format.format(date);
    	
    	Order order = new Order();
    	List<OrderAll> allOrder; 
		String timeS = time28 + "235959";
		String timeE = time21 + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	order.setStoreid(orderGood.getStoreid());
    	allOrder=skuMapper.queryorder(order);
    	int count=0;
    	
    	for(int i =0;i<allOrder.size();i++){
        	if(allOrder.get(i).getCode().equals(orderGood.getCode())){
        	count = count + allOrder.get(i).getAmount();
        	}
        	}  
    	
		JSONObject json = new JSONObject();
		json.put("time1",time28 + "~" + time21);
		json.put("count1", count);
		
		timeS = time21 + "235959";
		timeE = time14 + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	order.setStoreid(orderGood.getStoreid());
    	allOrder=skuMapper.queryorder(order);
    	count=0;
    	
    	for(int i =0;i<allOrder.size();i++){
        	if(allOrder.get(i).getCode().equals(orderGood.getCode())){
        	count = count + allOrder.get(i).getAmount();
        	}
        	}  
    	
    	json.put("time2",time21 + "~" + time14);
		json.put("count2", count);
    	
    	timeS = time14 + "235959";
		timeE = time7 + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	order.setStoreid(orderGood.getStoreid());
    	allOrder=skuMapper.queryorder(order);
    	count=0;
    	
    	for(int i =0;i<allOrder.size();i++){
        	if(allOrder.get(i).getCode().equals(orderGood.getCode())){
        	count = count + allOrder.get(i).getAmount();
        	}
        	}  
    	
    	json.put("time3",time14 + "~" + time7);
		json.put("count3", count);
		
    	timeS = time7 + "235959";
		timeE = timenow + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	order.setStoreid(orderGood.getStoreid());
    	allOrder=skuMapper.queryorder(order);
    	count=0;
    	
    	for(int i =0;i<allOrder.size();i++){
        	if(allOrder.get(i).getCode().equals(orderGood.getCode())){
        	count = count + allOrder.get(i).getAmount();
        	}
        	}   
    	
    	json.put("time4",time7 + "~" + timenow);
		json.put("count4", count);
		
		return json.toJSONString();

    }
    
    @RequestMapping("week")
    public String total(OrderGood orderGood) throws Exception {
    	Calendar c = Calendar.getInstance();
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	c.setTime(new Date());
        c.add(Calendar.DATE, - 7); 
        Date d = c.getTime();
        String timed=format.format(d);       
    	List<OrderAll> allOrder; 
    	Date date=new Date();
    	Order order = new Order();
		String time=format.format(date);
		String timeS = timed + "000000";
		String timeE = time + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	order.setStoreid(orderGood.getStoreid());
    	allOrder=skuMapper.queryorder(order);
    	int count=0;
    	double fee=0;
    	int amount;
    	double price;
    	
    	for(int i =0;i<allOrder.size();i++){
        	if(allOrder.get(i).getCode().equals(orderGood.getCode())){
        	count = count + allOrder.get(i).getAmount();
        	amount = allOrder.get(i).getAmount();
        	price = allOrder.get(i).getPrice()*100;
        	fee = fee + amount*price;
        	}
        	}   
		JSONObject json = new JSONObject();
		json.put("count", count);
		json.put("totlefee", fee/100);
		
		return json.toJSONString();

    }
    
    @RequestMapping("day")
    public String day(OrderGood orderGood) throws Exception {
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
      	List<OrderAll> allOrder; 
    	Date date=new Date();
    	Order order = new Order();
		String time=format.format(date);
		String timeS = time + "000000";
		String timeE = time + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	order.setStoreid(orderGood.getStoreid());
    	allOrder=skuMapper.queryorder(order);
    	int count=0;
    	double fee=0;
    	int amount;
    	double price;
    	
    	for(int i =0;i<allOrder.size();i++){
        	if(allOrder.get(i).getCode().equals(orderGood.getCode())){
        	count = count + allOrder.get(i).getAmount();
        	amount = allOrder.get(i).getAmount();
        	price = allOrder.get(i).getPrice()*100;
        	fee = fee + amount*price;
        	}
        	}   
    	
		JSONObject json = new JSONObject();
		json.put("count", count);
		json.put("totlefee", fee/100);
		
		return json.toJSONString();

    }
    
    @RequestMapping("month")
    public String month(OrderGood orderGood) throws Exception {
    	Calendar c = Calendar.getInstance();
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	c.setTime(new Date());
    	c.add(Calendar.MONTH, -1);
        Date d = c.getTime();
        String timed=format.format(d);       
    	List<OrderAll> allOrder; 
    	Date date=new Date();
    	Order order = new Order();
		String time=format.format(date);
		String timeS = timed + "000000";
		String timeE = time + "235959";
    	order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	order.setStoreid(orderGood.getStoreid());
    	allOrder=skuMapper.queryorder(order);
    	int count=0;
    	double fee=0;
    	int amount;
    	double price;
    	
    	for(int i =0;i<allOrder.size();i++){
        	if(allOrder.get(i).getCode().equals(orderGood.getCode())){
        	count = count + allOrder.get(i).getAmount();
        	amount = allOrder.get(i).getAmount();
        	price = allOrder.get(i).getPrice()*100;
        	fee = fee + amount*price;
        	}
        	}       	

		JSONObject json = new JSONObject();
		json.put("count", count);
		json.put("totlefee", fee/100);
		
		return json.toJSONString();

    }

    @RequestMapping("num")
    public String num(Sku sku) {
    	Integer cartcount;
    	Integer commentcount;
    	
    	Intea SkuA = skuMapper.count(sku);
    	
      	if(SkuA == null){
      		cartcount = 0;
      	}
      	else
      		cartcount = SkuA.getNum();
      		int cart = cartcount.intValue();
      	
      	Intea Sku = skuMapper.comment(sku);
      	
      	if(Sku == null){
      		commentcount = 0;
      	}
      	else
      		commentcount = Sku.getNum();
  			int comment = commentcount.intValue();    		
      	
    	JSONObject json = new JSONObject();
		json.put("cartcount", cart);
		json.put("commentcount", comment);
		
		return json.toJSONString();
    }  
      
}
