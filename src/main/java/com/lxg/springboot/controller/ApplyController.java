package com.lxg.springboot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxg.springboot.mapper.ApplyMapper;
import com.lxg.springboot.mapper.FieldMapper;
import com.lxg.springboot.mapper.ShopMapper;
import com.lxg.springboot.mapper.SkuMapper;
import com.lxg.springboot.mapper.UserMapper;
import com.lxg.springboot.model.Apply;
import com.lxg.springboot.model.ApplyIf;
import com.lxg.springboot.model.Applypage;
import com.lxg.springboot.model.Field;
import com.lxg.springboot.model.Msg;
import com.lxg.springboot.model.Order;
import com.lxg.springboot.model.OrderAll;
import com.lxg.springboot.model.Result;
import com.lxg.springboot.model.ResultUtil;
import com.lxg.springboot.model.Shop;
import com.lxg.springboot.model.Token;
import com.lxg.springboot.model.User;
import com.lxg.springboot.service.HttpAPIService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.print.DocFlavor.STRING;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("CVS/apply/")
public class ApplyController extends BaseController {
	
	

	@Resource
    private SkuMapper skuMapper;
	@Resource
    private ApplyMapper applyMapper;
	@Resource
    private FieldMapper fieldMapper;
	@Resource
    private ShopMapper shopMapper;
	@Resource
    private UserMapper userMapper;
	@Value("${wx.appid}")
	private String appid;
	@Value("${wx.appSecret}")
	private String appSecret;
	@Resource
	private HttpAPIService httpAPIService;

    @RequestMapping("insert")
    public Msg save(Apply apply) { 
    	int i = applyMapper.querymax();
    	apply.setId(i+1);
    	if(!(apply.getField() == null || apply.getField().isEmpty())){
    		if(applyMapper.fieldcount(apply)<=10) {
    		applyMapper.save(apply);
    		Field field = new Field();
    		field.setOpenid(apply.getField());
    		field = fieldMapper.queryfield(field);
    		apply.setFieldaddress(field.getAddress());
    		apply.setComname(field.getComname());
    		apply.setComnum(field.getComnum());
    		apply.setFieldfee(field.getFee());
    		apply.setFieldname(field.getName());
    		apply.setFieldphone(field.getPhone());
    		apply.setImg1(field.getImg1());
    		apply.setImg2(field.getImg2());
    		apply.setImg3(field.getImg3());
    		applyMapper.updatefieldaddress(apply);
    		}
    		else{
    			return ResultUtil.fail("场地提供超过10个");	
    		}
    	}
    	else if(!(apply.getDeal() == null || apply.getDeal().isEmpty())){
    		if(applyMapper.dealcount(apply)<=10) {
    		applyMapper.save(apply);
    		Field field = new Field();
    		field.setOpenid(apply.getDeal());
    		field = fieldMapper.querydeal(field);
    		apply.setDealaddress(field.getAddress());
    		apply.setDealname(field.getName());
    		apply.setDealphone(field.getPhone());
    		applyMapper.updatedealaddress(apply);
    		}
    		else{
    			return ResultUtil.fail("经营货架超过10个");	
    		}
    	}
    	else if(!(apply.getSupply() == null || apply.getSupply().isEmpty())){
    		if(applyMapper.supplycount(apply)<=10) {
    		applyMapper.save(apply);
    		Field field = new Field();
    		field.setOpenid(apply.getSupply());
    		field = fieldMapper.querysupply(field);
    		apply.setSupplyaddress(field.getAddress());
    		apply.setSupplyphone(field.getPhone());
    		apply.setSupplyname(field.getName());
    		apply.setGoodtype(field.getGoodtype());
    		applyMapper.updatesupplyaddress(apply);
    		}
    		else{
    			return ResultUtil.fail("供货超过10个");	
    		}
    	}
    	
    	return ResultUtil.success(i);
    }    
    
    @RequestMapping("update")
    public Result update(Apply apply) {
    	if(apply.getFieldstate() == 1 )
        	applyMapper.updatefieldstate(apply);
    	if(apply.getDealstate() == 1)
    		applyMapper.updatedealstate(apply);
    	if(apply.getSupplystate() == 1)
    		applyMapper.updatesupplystate(apply);    	
    	return new Result();
    } 
    
    @RequestMapping("delete")
    public Result delete(Apply apply) {
    	applyMapper.delete(apply);    	
    	return new Result();
    } 
    
    @RequestMapping("updatename")
    public Result updatename(Apply apply) {
    	applyMapper.updatename(apply);
    	return new Result();
    } 
    
    @RequestMapping("opapply")
    public Msg opapply(String openid,int id,int roletype,String optype) {
    Apply apply = new Apply();
    apply.setId(id);
    if(roletype==0){
    	if(optype.equals("join")){
    		apply.setField(openid);
    		if(applyMapper.fieldcount(apply)<=10) {
    		Field field = new Field();
    		field.setOpenid(openid);
    		field = fieldMapper.queryfield(field);
    		apply.setFieldaddress(field.getAddress());
    		apply.setComname(field.getComname());
    		apply.setComnum(field.getComnum());
    		apply.setFieldfee(field.getFee());
    		apply.setFieldname(field.getName());
    		apply.setFieldphone(field.getPhone());
    		apply.setImg1(field.getImg1());
    		apply.setImg2(field.getImg2());
    		apply.setImg3(field.getImg3());
    		applyMapper.updatefieldaddress(apply);}
    		else{
    			return ResultUtil.fail("场地提供超过10个");	
    		}
    	}
    	else{
    		apply.setField("");
    		apply.setFieldstate(0);
    		apply.setFieldaddress("");
    		apply.setComname("");
    		apply.setComnum("");
    		apply.setFieldfee("");
    		apply.setFieldname("");
    		apply.setFieldphone("");
    		apply.setImg1("");
    		apply.setImg2("");
    		apply.setImg3("");
    		applyMapper.updatefieldaddress(apply);
    		applyMapper.updatefieldstate(apply);
    	}
    	applyMapper.updatefield(apply);	
    }
    else if(roletype==1){
    	if(optype.equals("join")){
    		apply.setDeal(openid);
    		if(applyMapper.dealcount(apply)<=10) {
    		Field field = new Field();
    		field.setOpenid(openid);
    		field = fieldMapper.querydeal(field);
    		apply.setDealaddress(field.getAddress());
    		apply.setDealname(field.getName());
    		apply.setDealphone(field.getPhone());
    		applyMapper.updatedealaddress(apply);}
    		else{
    			return ResultUtil.fail("经营货架超过10个");	
    		}
    	}
    	else{
    		apply.setDeal("");
    		apply.setDealstate(0);
    		apply.setDealaddress("");
    		apply.setDealname("");
    		apply.setDealphone("");
    		applyMapper.updatedealaddress(apply);
    		applyMapper.updatedealstate(apply);
    	}
    	applyMapper.updatedeal(apply);	
    }
    else if(roletype==2){
    	if(optype.equals("join")){
    		apply.setSupply(openid);
    		if(applyMapper.supplycount(apply)<=10) {
    		Field field = new Field();
    		field.setOpenid(openid);
    		field = fieldMapper.querysupply(field);
    		apply.setSupplyaddress(field.getAddress());
    		apply.setSupplyphone(field.getPhone());
    		apply.setSupplyname(field.getName());
    		apply.setGoodtype(field.getGoodtype());
    		applyMapper.updatesupplyaddress(apply);
    		}
    		else{
    			return ResultUtil.fail("供货超过10个");	
    		}
    	}
    	else{
    		apply.setSupply("");
    		apply.setSupplystate(0);
    		apply.setSupplyaddress("");
    		apply.setSupplyphone("");
    		apply.setSupplyname("");
    		apply.setGoodtype("");
    		applyMapper.updatesupplyaddress(apply);
    		applyMapper.updatesupplystate(apply);
    	}
    	applyMapper.updatesupply(apply);	
    }
    	return ResultUtil.success();
    }
    
    
    @RequestMapping("perset")
    public Msg perset(int id,int roletype,int per) {
    Apply apply = new Apply();
    apply.setId(id);
    if(roletype==0){   	
    	apply.setFieldper(per);
    	applyMapper.updatefieldper(apply);
    }
    else if(roletype==1){
    	apply.setDealper(per);
    	applyMapper.updatedealper(apply);  	
    }
    else if(roletype==2){
    	apply.setSupplyper(per);
    	applyMapper.updatesupplyper(apply);  	
    	
    }
    else if(roletype==3){
    	apply.setPlatper(per);
    	applyMapper.updateplatper(apply);  	
    	
    }
    	return ResultUtil.success();
    }
    
    @RequestMapping("queryper")
    public String queryper(int id) {
    	Apply apply = new Apply();
        apply.setId(id);
        Apply temp = new Apply();
        temp = applyMapper.queryone(apply);
    	JSONObject json = new JSONObject();
		json.put("fieldper", temp.getFieldper());
		json.put("dealper",temp.getDealper());
		json.put("supplyper",temp.getSupplyper());
		json.put("platper",temp.getPlatper());
		return json.toJSONString();
    }
    
       
    @RequestMapping("queryapply")
    public Applypage querybypage(Apply apply) {
    	List<Apply> applyA;  
    	int temp = 0;
    	temp = apply.getPage();
    	apply.setPage(temp*apply.getPagenum());
    	if (apply.getAddress()==null || apply.getAddress().isEmpty()){
    	applyA = applyMapper.query(apply);
    	for(int i = 0 ; i < applyA.size();i++)
    	{
    		if(applyA.get(i).getAddress() == null || applyA.get(i).getAddress().isEmpty())   			
    		{
    			applyA.get(i).setAddress("");	
    		}
    	}
    	temp = applyMapper.querypage(apply);}
    	else {
    	applyA = applyMapper.querybyad(apply);
    	temp = applyMapper.querypagead(apply);
    	}   
    	for(int i=0;i<applyA.size();i++){
    		if(!(applyA.get(i).getAddress()== null || applyA.get(i).getAddress().isEmpty())){
    		if (applyA.get(i).getAddress().length()>=8){
    			String tempA = applyA.get(i).getAddress().substring(0,7)+"...";
    			applyA.get(i).setAddress(tempA);
    		}
    		}
    		if(!(applyA.get(i).getFieldaddress()== null || applyA.get(i).getFieldaddress().isEmpty())){
    		if (applyA.get(i).getFieldaddress().length()>=8){
    			String tempA = applyA.get(i).getFieldaddress().substring(0,7)+"...";
    			applyA.get(i).setFieldaddress(tempA);
    		}
    		}
    		if(!(applyA.get(i).getDealaddress()== null || applyA.get(i).getDealaddress().isEmpty())){
    		if (applyA.get(i).getDealaddress().length()>=8){
    			String tempA = applyA.get(i).getDealaddress().substring(0,7)+"...";
    			applyA.get(i).setDealaddress(tempA);
    		}
    		}
    		if(!(applyA.get(i).getSupplyaddress()== null || applyA.get(i).getSupplyaddress().isEmpty())){
    		if (applyA.get(i).getSupplyaddress().length()>=8){
    			String tempA = applyA.get(i).getSupplyaddress().substring(0,7)+"...";
    			applyA.get(i).setSupplyaddress(tempA);
    		}}
    	}
    	Applypage applypage = new Applypage();
    	applypage.setApply(applyA);
    	applypage.setTotalpage(temp/apply.getPagenum()+1);    	
    	return applypage;
    } 
    
    @RequestMapping("field/querynow")
    public Field querynowfield(Apply apply) {
    	Field temp = new Field();
    	temp = applyMapper.queryfield(apply);
    	return temp;
    } 
    
    @RequestMapping("deal/querynow")
    public Field querynowdeal(Apply apply) {
        Field temp = new Field();
        temp = applyMapper.querydeal(apply);
        return temp;
    }
    
    @RequestMapping("supply/querynow")
    public Field querynowsupply(Apply apply) {
        Field temp = new Field();
        temp = applyMapper.querysupply(apply);
        return temp;
    }  
    
    @RequestMapping("query")
    public Msg query(Apply apply) {
    	Apply temp = new Apply();
    	temp = applyMapper.queryone(apply);
    	return ResultUtil.success(temp);
    } 
    
    @RequestMapping("field/insert")
    public Result fieldsave(Field field) { 
    	fieldMapper.savefield(field);
    	return new Result();
    }    
    
    @RequestMapping("field/update")
    public Result fieldupdate(Field field) {
    	fieldMapper.updatefield(field);
    	return new Result();
    } 
        
    @RequestMapping("field/query")
    public Field fieldquerybypage(Field field) {
    	Field temp = new Field();
    	temp = fieldMapper.queryfield(field);
    	return temp;
    } 
    
     
    @RequestMapping("deal/insert")
    public Result dealsave(Field deal) { 
    	fieldMapper.savedeal(deal);
        return new Result();
    }    
    
    @RequestMapping("deal/update")
    public Result dealupdate(Field deal) {
    	fieldMapper.updatedeal(deal);
        return new Result();
    } 
        
    @RequestMapping("deal/query")
    public Field dealquerybypage(Field deal) {
        Field temp = new Field();
        temp = fieldMapper.querydeal(deal);
        return temp;
    }
    
    @RequestMapping("supply/insert")
    public Result supplysave(Field supply) { 
    	fieldMapper.savesupply(supply);
        return new Result();
    }    
    
    @RequestMapping("supply/update")
    public Result supplyupdate(Field supply) {
    	fieldMapper.updatesupply(supply);
        return new Result();
    } 
        
    @RequestMapping("supply/query")
    public Field supplyquerybypage(Field supply) {
        Field temp = new Field();
        temp = fieldMapper.querysupply(supply);
        return temp;
    }
    
    @RequestMapping("Role")
    public Msg Role(Field supply) {
        Field temp = new Field();
        Field temp1 = new Field();
        Field temp2 = new Field();
        temp = fieldMapper.queryfield(supply);
        temp1 = fieldMapper.querydeal(supply);
        temp2 = fieldMapper.querysupply(supply);
        ApplyIf applyif = new ApplyIf();
        if(temp==null){
        	applyif.setField(false);
        }
        else{
        	applyif.setField(true);
        }
        if(temp1==null){
        	applyif.setDeal(false);
        }
        else{
        	applyif.setDeal(true);
        }
        if(temp2==null){
        	applyif.setSupply(false);
        }
        else{
        	applyif.setSupply(true);
        }
        return ResultUtil.success(applyif);
    }
    
    
    @RequestMapping("registerquery")
    public boolean registerquery(int id) {
    	
    	 String str_m = String.valueOf(id); 
   /*      String str ="000000";
         str_m=str.substring(0, 6-str_m.length())+str_m;*/
         
         User temp=new User();
         temp.setStoreid(str_m);
         int i = userMapper.countbosss(temp);
         if(i!=0){
         	return true;
         }
         else{
        	 str_m = String.valueOf(id); 
        	 temp.setStoreid(str_m);
        	 i = userMapper.countbosss(temp);
        	 if(i!=0){
              	return true;
              }
        	 else
        	 {
        	    return false; 
        	 }
          }
        	 
    }
    @RequestMapping("register")
    public Msg register(int id , String password ,String storename,String address) throws Exception {
        Apply apply = new Apply();
        apply.setId(id);
        apply = applyMapper.queryone(apply);
        String str_m = String.valueOf(id); 

        Field field = new Field();
		Field fieldA = new Field();
		Field deal = new Field();
		Field supply = new Field();
		field.setOpenid(apply.getField());
		deal = fieldMapper.querydeal(field);
		fieldA = fieldMapper.queryfield(field);
		supply = fieldMapper.querysupply(field);
		
		  User temp=new User();
	        temp.setPhoneno(fieldA.getPhone());
	        int i = userMapper.countbossp(temp);
	        if(i!=0){
	        	User tempUser = new User();
	        	tempUser.setPhoneno(fieldA.getPhone());
	        	List<User> userf ;
	    		userf = userMapper.querybynoboss(tempUser);
	    		String passwordtemp = userf.get(0).getPassword();
	    		if(!passwordtemp.equals(password))
	    		{
	    			return ResultUtil.fail("密码错误");
	    		}
	        }
       
        Shop shop =new Shop();
        shop.setStoreId(str_m);
        shop.setStoreName(storename);
        shop.setAddress(address);
        shop.setField(fieldA.getOpenid());
        shop.setFieldname(fieldA.getName());
        shop.setDeal(deal.getOpenid());
        shop.setDealname(deal.getName());
        shop.setSupply(supply.getOpenid());
        shop.setSupplyname(supply.getName());
    	shopMapper.insertmore(shop);
    	
    	Apply tempA = new Apply();
    	tempA.setId(id);
    	tempA.setAddress(address);
    	tempA.setStorename(storename);
    	applyMapper.updateaddress(tempA);
    	applyMapper.updatename(tempA);
    	
    	User user=new User();
    	user.setNickname(fieldA.getName());
    	user.setOpenid(fieldA.getOpenid());
    	user.setPhoneno(fieldA.getPhone());
    	user.setPassword(password);
    	user.setStoreid(str_m);
    	user.setRole("boss");
    	userMapper.saveboss(user);
    	
    	String ccid = "7442cca3ea7be0cdcf85e1ebd87b49dce6fe7ee7e4c166c32f11a096610adc22";
		String urla = "https://store.lianlianchains.com/kd/invoke?func=setAllocCfg&" + "ccId=" + ccid + "&" + "usr=centerBank&acc=centerBank&rid=" + id
				+ "&" + "slr=" + apply.getDealper() + "&"  + "pfm=" + apply.getPlatper() + "&"  + "fld=" + apply.getFieldper() + "&" + "dvy=" + apply.getSupplyper();
		
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
		
		String fieldunion = userMapper.getunionid(shop.getField());		
		String dealunion = userMapper.getunionid(shop.getDeal());		
		String supplyunion = userMapper.getunionid(shop.getSupply());		
		String platform = "o57KOvxS0eXTtxv23EobCON__S40";
		
		urla = "https://store.lianlianchains.com/kd/invoke?func=encourageScoreForNewRack&" + "ccId=" + ccid + "&" + "usr=kdcoinpool&acc=kdcoinpool&desc=新货架奖励积分&cfg=" + shop.getStoreId()+ ","+dealunion+ ","+fieldunion+ ","+supplyunion+ ","+platform;
		
		res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		json = JSON.parseObject(res);  
		resc = json.getString("code");
		
		if (!resc.equals("0")){
			return ResultUtil.fail("区块链连接错误");
		}		
    	
    	String urltoken = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&" + "appid=" + appid
				+ "&" + "secret=" + appSecret;

		Token token = JSON.parseObject(httpAPIService.doGet(urltoken), Token.class);

		String url = "https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode?access_token="
				+ token.getAccess_token();

		// 参数

		// 参数
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("path","pages/index/index"+"?StoreId="+ str_m+"&StoreName="+ storename);
		map.put("width","5000");

		HashMap<String, Object> maptemp = new HashMap<String, Object>();
		maptemp.put("Jsondata", JSON.toJSONString(map));

		// 请求头
		HashMap<String, Object> header = new HashMap<String, Object>();

		byte[] data = httpAPIService.doPostImg(url, maptemp, header);

		File file =new File("/usr/share/nginx/html/images/" + str_m + ".png");
		
        OutputStream stream = new FileOutputStream(file);
        stream.write(data);
        stream.flush();
        stream.close();
        
        return ResultUtil.success();
    }
    
    @RequestMapping("authorize")
    public Msg register(String storeid,String phoneno,String password) throws Exception {
    	User temp=new User();
    	User temp1=new User();
    	temp.setStoreid(storeid);
    	temp.setRole("boss");
    	temp1 = userMapper.querybossrole(temp);
    	if (temp1.getPhoneno().equals(phoneno)){
    		return ResultUtil.fail("只能授权他人");
    	}
    	User temp3=new User();
    	temp3.setPhoneno(phoneno);
    	int i = userMapper.countbossp(temp3);
        if(i!=0){
        	User tempUser = new User();
        	tempUser.setPhoneno(phoneno);
        	List<User> userf ;
    		userf = userMapper.querybynoboss(tempUser);
    		String passwordtemp = userf.get(0).getPassword();
    		if(!passwordtemp.equals(password))
    		{
    			return ResultUtil.fail("密码错误");
    		}
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
    		return ResultUtil.fail("已经授权过");
        }
    	    
        return ResultUtil.success();
    }
    
    @RequestMapping("getshop")
    public Msg getshop(String storeid) throws Exception {
    	Shop retshop = new Shop();
    	retshop = shopMapper.querybyid(storeid);
    	
    	return ResultUtil.success(retshop);
    }
    
    @RequestMapping("income")
    public Msg income() throws Exception {
    	
    	List<Shop> shop;
    	shop= shopMapper.query();
    	String temp = "";
    	String fieldunion= "";
    	String dealunion= "";
    	String supplyunion= "";
    	String platform = "o57KOvxS0eXTtxv23EobCON__S40";
    	
    	for(int i=0;i<shop.size();i++){
    		if(!temp.equals("")){
    			temp = temp + ";";
    		}
    		fieldunion = userMapper.getunionid(shop.get(i).getField());		
    		dealunion = userMapper.getunionid(shop.get(i).getDeal());		
    		supplyunion = userMapper.getunionid(shop.get(i).getSupply());		
    		
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
        	order.setStoreid(shop.get(i).getStoreId());
        	int fee=skuMapper.allmoney(order);
        	temp = temp + shop.get(i).getStoreId()+","+ fee + "," +dealunion+ ","+fieldunion+ ","+supplyunion+ ","+platform;
    		}
        	String ccid = "7442cca3ea7be0cdcf85e1ebd87b49dce6fe7ee7e4c166c32f11a096610adc22";
        	String urla = "https://store.lianlianchains.com/kd/invoke?func=encourageScoreForSales&" + "ccId=" + ccid + "&" + "usr=kdcoinpool&acc=kdcoinpool&desc=月度奖励积分&cfg="+ temp;
    		
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
    	
    	
    	return ResultUtil.success();
    	
    	
   
    }
    	
    
}
