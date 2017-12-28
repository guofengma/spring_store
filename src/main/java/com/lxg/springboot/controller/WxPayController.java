package com.lxg.springboot.controller;

import com.lxg.springboot.mapper.CartMapper;
import com.lxg.springboot.mapper.GoodMapper;
import com.lxg.springboot.mapper.OrderMapper;
import com.lxg.springboot.mapper.RefundMapper;
import com.lxg.springboot.mapper.WithDrawMapper;
import com.lxg.springboot.mapper.ShopMapper;
import com.lxg.springboot.mapper.SkuMapper;
import com.lxg.springboot.mapper.UserMapper;
import com.lxg.springboot.model.Applypage;
import com.lxg.springboot.model.Cart;
import com.lxg.springboot.model.Good;
import com.lxg.springboot.model.Shop;
import com.lxg.springboot.model.HttpResult;
import com.lxg.springboot.model.IncreaseMoney;
import com.lxg.springboot.model.IncreasePage;
import com.lxg.springboot.model.Msg;
import com.lxg.springboot.model.Order;
import com.lxg.springboot.model.OrderAll;
import com.lxg.springboot.model.OrderGood;
import com.lxg.springboot.model.OrderInfo;
import com.lxg.springboot.model.OrderReturnInfo;
import com.lxg.springboot.model.Orderpage;
import com.lxg.springboot.model.PayReturnInfo;
import com.lxg.springboot.model.RefundReturnInfo;
import com.lxg.springboot.model.ResultUtil;
import com.lxg.springboot.model.SignInfo;
import com.lxg.springboot.model.Token;
import com.lxg.springboot.model.User;
import com.lxg.springboot.model.WithDrawDay;
import com.lxg.springboot.model.WithdrawPage;
import com.lxg.springboot.service.HttpAPIService;
import com.lxg.springboot.util.CollectionUtil;
import com.lxg.springboot.util.HttpUtils;
import com.lxg.springboot.util.MD5Utils;
import com.lxg.springboot.util.PayUtil;
import com.lxg.springboot.util.RandomStringGenerator;
import com.lxg.springboot.util.Signature;
import com.lxg.springboot.util.StreamUtil;
import com.lxg.springboot.util.WebUtil;
import com.lxg.springboot.util.XmlUtil;
import com.thoughtworks.xstream.XStream;

import org.jdom.JDOMException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;  
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zhenghong
 * on 2017/4/25.
 */
@RestController
public class WxPayController {
	
    @Value("${wx.appid}")
    private String appid; 
    @Value("${wx.mch_id}")
    private String mch_id;     

	@Resource
    private HttpAPIService httpAPIService;
	@Resource
    private OrderMapper orderMapper;
	@Resource
	private WithDrawMapper withDrawMapper;
	@Resource
	private RefundMapper refundMapper;
	@Resource
    private OrderMapper OrderMapper;
	@Resource
	private CartMapper cartMapper;
	@Resource
    private GoodMapper goodMapper;
	@Resource
    private ShopMapper shopMapper;
	@Resource
    private UserMapper userMapper;
	@Resource
    private SkuMapper skuMapper;
	
    @RequestMapping("wxpay/prepay")
    public String prepay(Order Order) throws Exception {	
		OrderInfo order = new OrderInfo();
		String orderNo = RandomStringGenerator.getRandomStringByLength(32);
		Order.setState(0);
    	Date date=new Date(); 
		DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss"); 
		String time=format.format(date);
		Order.setTime(time);
		List<Cart> cart;
		Cart tempA = new Cart();
		tempA.setOpenid(Order.getOpenid());
		tempA.setStoreid(Order.getStoreid());
		cart = cartMapper.querybypage(tempA);
		for(int i = 0 ; i < cart.size() ; i++) {			
			OrderGood temp = new OrderGood();
			Good good = new Good();
    		good.setCode(cart.get(i).getCode());
        	good.setStoreid(cart.get(i).getStoreid());
        	Good returngood = new Good();
        	returngood=goodMapper.querybyCode(good);
        	if(returngood.getAmount()<cart.get(i).getAmount()){
        		JSONObject jsonA = new JSONObject();
        		jsonA.put("returncode", "01");
        		jsonA.put("returnmsg", returngood.getName()+"库存不足");
        		return jsonA.toJSONString();
        	}
        	else{
        		temp.setAmount(cart.get(i).getAmount());
        		temp.setCode(cart.get(i).getCode());
        		temp.setOrderNo(orderNo);
        		temp.setStoreid(Order.getStoreid());
        		temp.setSpecifi(returngood.getSpecifi());
        		temp.setPrice(returngood.getPrice());
        		temp.setName(returngood.getName());
        		OrderMapper.savegood(temp);	
        	}	 
        }	
		Order.setOrderNo(orderNo);
		Order.setTime(time);
		Order.setState(2);
		Order.setCheckstate(0);
		orderMapper.save(Order);
		int totalfee=(int) (Order.getFee()*100);
		order.setAppid(appid);
		order.setMch_id(mch_id);
		order.setNonce_str(RandomStringGenerator.getRandomStringByLength(32));
		order.setBody(new String(Order.getDescription().getBytes(),"UTF-8"));
		order.setOut_trade_no(orderNo);
		order.setTotal_fee(totalfee);
		order.setSpbill_create_ip("123.57.218.54");
		order.setNotify_url("https://store.lianlianchains.com/wxpay/result");
		order.setTrade_type("JSAPI");
		order.setOpenid(Order.getOpenid());
		order.setSign_type("MD5");
		//生成签名
		String sign = Signature.getSign(order);
		order.setSign(sign);
		
		System.out.println("body===="+order.getBody());
		
		HttpResult result = httpAPIService.doPost("https://api.mch.weixin.qq.com/pay/unifiedorder", order);
		
		XStream xStream = new XStream();
		xStream.alias("xml", OrderReturnInfo.class); 

		OrderReturnInfo returnInfo = (OrderReturnInfo)xStream.fromXML(result.getBody());
		JSONObject json = new JSONObject();
		json.put("prepay_id", returnInfo.getPrepay_id());
		json.put("return_code", returnInfo.getReturn_code());
		json.put("return_msg", returnInfo.getReturn_msg());
		json.put("orderNo", orderNo);
		
		String userunion = userMapper.getunionid(Order.getOpenid());
		User tempuser = new User();
		tempuser.setStoreid(Order.getStoreid());
		User Boss=userMapper.querybossbyid(tempuser);
		String bossunion = userMapper.getunionid(Boss.getOpenid());
		String ccid = "7442cca3ea7be0cdcf85e1ebd87b49dce6fe7ee7e4c166c32f11a096610adc22";
		String urla ="";
		String res="";
			
		
		
		
		
		
		return json.toJSONString();
    }
    
    @RequestMapping("wxpay/sign")
    public String sign(String repay_id) throws Exception {

		SignInfo signInfo = new SignInfo();
		signInfo.setAppId(appid);
		long time = System.currentTimeMillis()/1000;
		signInfo.setTimeStamp(String.valueOf(time));
		signInfo.setNonceStr(RandomStringGenerator.getRandomStringByLength(32));
		signInfo.setRepay_id("prepay_id="+repay_id);
		signInfo.setSignType("MD5");
		//生成签名
		String sign = Signature.getSign(signInfo);
		
		JSONObject json = new JSONObject();
		json.put("timeStamp", signInfo.getTimeStamp());
		json.put("nonceStr", signInfo.getNonceStr());
		json.put("package", signInfo.getRepay_id());
		json.put("signType", signInfo.getSignType());
		json.put("paySign", sign);
		
		return json.toJSONString();
		
    }

    @RequestMapping("wxpay/result")
    public void result(HttpServletRequest request,HttpServletResponse response) throws IOException {  	
    	System.out.println("微信支付回调");
        InputStream inStream = request.getInputStream();
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        String result = new String(outSteam.toByteArray(), "utf-8");
        System.out.println("微信支付通知结果：" + result);
        Map<String, String> map = null;
        try {
            /**
             * 解析微信通知返回的信息
             */
            map = XmlUtil.doXMLParse(result);
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Order Order = new Order();
        Order.setOrderNo(map.get("out_trade_no"));
        Order tempOrder = new Order();
        tempOrder = orderMapper.querybyno(Order);
        String openid = tempOrder.getOpenid();
        int score = tempOrder.getUsedScore();
      
        if(tempOrder.getState()!=1){        	
        if (map.get("result_code").equals("SUCCESS")) {      	
        	Order.setState(1);
        	List<OrderGood> temp;
        	temp=orderMapper.queryGood(Order);
        	for(int i=0 ;i<temp.size();i++){
        		Good good = new Good();
        		good.setCode(temp.get(i).getCode());
            	good.setStoreid(temp.get(i).getStoreid());
            	Good returngood = new Good();
            	returngood=goodMapper.querybyCode(good);
            	System.out.println("减库存"+ returngood.getAmount()+temp.get(i).getAmount());
            	returngood.setAmount(returngood.getAmount()-temp.get(i).getAmount());
            	goodMapper.update(returngood);		
    	}
        	orderMapper.update(Order);     
        	//企业付款
        	int fee = (int) (tempOrder.getFee()*100); 
        	System.out.println("企业支付结果：" + fee);
        	String feeS = Integer.toString(fee);
        	String paternerKey="79m1jyaofjonvahln1wnoq606rvbk2gi";
    		Map<String, String> parm = new HashMap<String, String>();
    		String orderNo = RandomStringGenerator.getRandomStringByLength(32);
    		Order OrderPay = new Order();
    		OrderPay.setOrderNo(orderNo);
    		OrderPay.setFee(tempOrder.getFee());
    		String storeid=tempOrder.getMch_id();
    		User tempuser = new User();
    		tempuser.setStoreid(storeid);
    		User Boss=userMapper.querybossbyid(tempuser);
    		try{		
    		parm.put("mch_appid", appid); //公众账号appid
    		parm.put("mchid", "1472139902"); //商户号
    		parm.put("nonce_str", RandomStringGenerator.getRandomStringByLength(32)); //随机字符串
    		parm.put("partner_trade_no",orderNo); //商户订单号
    		parm.put("openid", Boss.getOpenid()); //用户openid	
    		parm.put("check_name", "FORCE_CHECK"); //校验用户姓名选项 OPTION_CHECK
    		parm.put("re_user_name", Boss.getNickname()); //check_name设置为FORCE_CHECK或OPTION_CHECK，则必填
    		parm.put("amount", feeS); //转账金额
    		parm.put("desc", "快点支付"); //企业付款描述信息		
    		parm.put("spbill_create_ip", InetAddress.getLocalHost().getHostAddress());
    		parm.put("sign", PayUtil.getSign(parm, paternerKey));
    		OrderPay.setMch_id("1472139902");
    		Date date=new Date(); 
    		DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss"); 
    		String time=format.format(date);
    		OrderPay.setOpenid(Boss.getOpenid());
    		OrderPay.setTime(time);
    		OrderPay.setState(2);
    		withDrawMapper.save(OrderPay);
    		
    		} catch (UnsupportedEncodingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (UnknownHostException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}	
    		
    		String userunion = userMapper.getunionid(openid);
    		tempuser.setStoreid(Order.getStoreid());
    		String bossunion = userMapper.getunionid(Boss.getOpenid());
    		String ccid = "7442cca3ea7be0cdcf85e1ebd87b49dce6fe7ee7e4c166c32f11a096610adc22";
    		String urla ="";
    		String res="";
    		
    		urla = "https://store.lianlianchains.com/kd/invoke?func=transefer&" + "ccId=" + ccid + "&" + "usr=" + bossunion	+ "&" + "acc=" + bossunion + "&" + "reacc=" + userunion +  "&" + "amt=5" + "&tstp=消费奖励积分&desc=消费奖励积分" ;
    		
    		res = null;
    		
    		try {
    			res = httpAPIService.doGet(urla);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		JSONObject json1 = JSON.parseObject(res);  
    		String resc1 = json1.getString("code");
    		
    		if(score > 0){
    			
    	    	
    			urla = "https://store.lianlianchains.com/kd/invoke?func=transefer&" + "ccId=" + ccid + "&" + "usr=" + userunion	+ "&" + "acc=" + userunion + "&" + "reacc=" + bossunion +  "&" + "amt=" + score + "&tstp=消费抵扣积分&desc=消费抵扣积分" ;
    			
    			res = null;
    			
    			try {
    				res = httpAPIService.doGet(urla);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			
    			JSONObject json2 = JSON.parseObject(res);  
    			String resc2 = json2.getString("code");
    			
    		}
    		
    		if(userMapper.bosspay()== 0){
    		HttpResult result2 = HttpUtils.posts("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", XmlUtil.xmlFormat(parm, false));
    			

    		XStream xStream = new XStream();
    		xStream.alias("xml", PayReturnInfo.class); 

    		PayReturnInfo returnInfo = (PayReturnInfo)xStream.fromXML(result2.getBody());
    		JSONObject json = new JSONObject();
    		json.put("return_code", returnInfo.getReturn_code());
    		json.put("return_msg", returnInfo.getReturn_msg());
    		System.out.println("企业支付结果：" + returnInfo.getReturn_msg());
    		System.out.println("企业支付结果：" + returnInfo.getErr_code()+returnInfo.getErr_code_des()+returnInfo.getResult_code());
    		if (returnInfo.getResult_code().equals("SUCCESS")) {
    			OrderPay.setState(1);		
            }
            else{
            	OrderPay.setState(0);	
            }               
    		withDrawMapper.update(OrderPay);  	
    		}
        }
        else{
        	Order.setState(0);
        	orderMapper.update(Order);     
        }                  
        }
    }
    @RequestMapping("wxpay/increaseall")
    public Msg increaseall(IncreaseMoney inc) throws IOException { 
    	int tempnum = 0;
    	tempnum = inc.getPage();
    	inc.setPage(tempnum*inc.getPagenum());
    	Calendar c8 = Calendar.getInstance();
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	c8.setTime(new Date());
        c8.add(Calendar.DATE, - 8); 
        Date d8 = c8.getTime();
        String timed8=format.format(d8);        
        Calendar c1 = Calendar.getInstance();
        c1.setTime(new Date());
        c1.add(Calendar.DATE, - 1); 
        Date d1 = c1.getTime();
        String timed1=format.format(d1);     
		String timeS = timed8 + "000000";
		String timeE = timed1 + "235959";
		inc.setStartDate1(timeS);
		inc.setEndDate1(timeE);
		Calendar c15 = Calendar.getInstance();
		c15.setTime(new Date());
		c15.add(Calendar.DATE, - 15); 
		Date d15 = c15.getTime();
        String timed15=format.format(d15); 
        String timeS2 = timed15 + "000000";
		String timeE2 = timed8 + "235959";
		inc.setStartDate2(timeS2);
		inc.setEndDate2(timeE2);
		
		List<IncreaseMoney> temp;  
    	temp = skuMapper.increaseall(inc);
    	IncreasePage increasePage = new IncreasePage();
    	increasePage.setIncreasMoney(temp);
    	tempnum = skuMapper.increasenum(inc);
    	increasePage.setTotalpage(tempnum/inc.getPagenum()+1); 
    	
    	return ResultUtil.success(increasePage);
		
    }
    
    @RequestMapping("wxpay/increaseuser")
    public Msg increasealluser(IncreaseMoney inc) throws IOException { 
    	int tempnum = 0;
    	tempnum = inc.getPage();
    	inc.setPage(tempnum*inc.getPagenum());
    	Calendar c8 = Calendar.getInstance();
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	c8.setTime(new Date());
        c8.add(Calendar.DATE, - 8); 
        Date d8 = c8.getTime();
        String timed8=format.format(d8);        
        Calendar c1 = Calendar.getInstance();
        c1.setTime(new Date());
        c1.add(Calendar.DATE, - 1); 
        Date d1 = c1.getTime();
        String timed1=format.format(d1);     
		String timeS = timed8 + "000000";
		String timeE = timed1 + "235959";
		inc.setStartDate1(timeS);
		inc.setEndDate1(timeE);
		Calendar c15 = Calendar.getInstance();
		c15.setTime(new Date());
		c15.add(Calendar.DATE, - 15); 
		Date d15 = c15.getTime();
        String timed15=format.format(d15); 
        String timeS2 = timed15 + "000000";
		String timeE2 = timed8 + "235959";
		inc.setStartDate2(timeS2);
		inc.setEndDate2(timeE2);
		
		List<IncreaseMoney> temp;  
    	temp = skuMapper.increasebyuser(inc);
    	IncreasePage increasePage = new IncreasePage();
    	increasePage.setIncreasMoney(temp);
    	tempnum = skuMapper.increasebyusernum(inc);
    	increasePage.setTotalpage(tempnum/inc.getPagenum()+1); 
    	
    	return ResultUtil.success(increasePage);
		
    }
    
    @RequestMapping("CVS/user/queryfinance")
    public Msg increaseuserjoin(IncreaseMoney inc) throws IOException { 
    	int tempnum = 0;
    	tempnum = inc.getPage();
    	inc.setPage(tempnum*inc.getPagenum());
    	Calendar c8 = Calendar.getInstance();
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	c8.setTime(new Date());
        c8.add(Calendar.DATE, - 8); 
        Date d8 = c8.getTime();
        String timed8=format.format(d8);        
        Calendar c1 = Calendar.getInstance();
        c1.setTime(new Date());
        c1.add(Calendar.DATE, - 1); 
        Date d1 = c1.getTime();
        String timed1=format.format(d1);     
		String timeS = timed8 + "000000";
		String timeE = timed1 + "235959";
		inc.setStartDate1(timeS);
		inc.setEndDate1(timeE);
		Calendar c15 = Calendar.getInstance();
		c15.setTime(new Date());
		c15.add(Calendar.DATE, - 15); 
		Date d15 = c15.getTime();
        String timed15=format.format(d15); 
        String timeS2 = timed15 + "000000";
		String timeE2 = timed8 + "235959";
		inc.setStartDate2(timeS2);
		inc.setEndDate2(timeE2);
		
		List<IncreaseMoney> temp;  
    	temp = skuMapper.increasebyuserjoin(inc);
    	IncreasePage increasePage = new IncreasePage();
    	increasePage.setIncreasMoney(temp);
    	tempnum = skuMapper.increasebyuserjoinnum(inc);
    	increasePage.setTotalpage(tempnum/inc.getPagenum()+1); 
    	
    	return ResultUtil.success(increasePage);
		
    }
  
    
    @Scheduled(cron = "0 10 05 ? * *" )
    @RequestMapping("wxpay/error")
    public void error() throws IOException { 
    	if(userMapper.bosspay() == 1){
    		Calendar c = Calendar.getInstance();
    		Date nowtime = new Date();
    		c.setTime(nowtime);
    		c.add(Calendar.DATE, - 1); 
    		Date yest = c.getTime();
    		c.add(Calendar.DATE, - 1); 
    		Date lastyes = c.getTime();
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		String timenow = sdf.format(nowtime);
    		String timeyes = sdf.format(yest); 
    		String timelastyes=sdf.format(lastyes); 
    		WithDrawDay temp = new WithDrawDay();
    		temp.setStartDate(timenow.substring(0,11)+"00:00:00");
    		temp.setEndDate(timenow.substring(0,11)+"23:59:59");
    		
    		List<WithDrawDay> errorlist; 
    		List<WithDrawDay> templist; 
    		
    		errorlist = withDrawMapper.queryallerrortime(temp);
    		String openid = "";
    		int tempfee;
    		WithDrawDay error = new WithDrawDay();
    		for(int i=0;i<errorlist.size();i++){
    			openid=errorlist.get(i).getOpenid();
    			temp.setOpenid(openid);
    			temp.setStartDate(timeyes.substring(0,11)+"00:00:00");
    			temp.setEndDate(timeyes.substring(0,11)+"23:59:59");
    			temp.setRole(errorlist.get(i).getRole());
    			temp.setOpenid(errorlist.get(i).getOpenid());
    			temp.setStoreid(errorlist.get(i).getStoreid());
    			templist = withDrawMapper.queryerrortime(temp);
    			if(templist.size()!=0){
    				if( templist.get(0).getState()==2){
    					error = errorlist.get(i);
    					tempfee = error.getFee() + templist.get(0).getFee();
    					error.setFee(tempfee);
    				}
    				else{
    					error = errorlist.get(i);
    					tempfee = error.getFee();
    				}
    			}
    			else{
    				error = errorlist.get(i);
					tempfee = error.getFee();
    			}
    					String paternerKey="79m1jyaofjonvahln1wnoq606rvbk2gi";
    		    		Map<String, String> parm = new HashMap<String, String>();
    		    		String orderNo = RandomStringGenerator.getRandomStringByLength(32);
    		    		String feeS = Integer.toString(tempfee);
    		    		
    		    		parm.put("mch_appid", appid); //公众账号appid
    		    		parm.put("mchid", "1472139902"); //商户号
    		    		parm.put("nonce_str", RandomStringGenerator.getRandomStringByLength(32)); //随机字符串
    		    		parm.put("partner_trade_no",orderNo); //商户订单号
    		    		parm.put("openid", temp.getOpenid()); //用户openid	
    		    		parm.put("check_name", "FORCE_CHECK"); //校验用户姓名选项 OPTION_CHECK
    		    		parm.put("re_user_name",error.getName()); //check_name设置为FORCE_CHECK或OPTION_CHECK，则必填
    		    		parm.put("amount", feeS); //转账金额
    		    		parm.put("desc", error.getDescription()); //企业付款描述信息		
    		    		parm.put("spbill_create_ip", InetAddress.getLocalHost().getHostAddress());
    		    		parm.put("sign", PayUtil.getSign(parm, paternerKey));
    		    		
    		    		HttpResult result2 = HttpUtils.posts("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", XmlUtil.xmlFormat(parm, false));
    					

    		    		XStream xStream = new XStream();
    		    		xStream.alias("xml", PayReturnInfo.class); 

    		    		PayReturnInfo returnInfo = (PayReturnInfo)xStream.fromXML(result2.getBody());
    		    		JSONObject jsonre = new JSONObject();
    		    		jsonre.put("return_code", returnInfo.getReturn_code());
    		    		jsonre.put("return_msg", returnInfo.getReturn_msg());
    		    		System.out.println("企业支付结果：" + returnInfo.getReturn_msg());
    		    		System.out.println("企业支付结果：" + returnInfo.getErr_code()+returnInfo.getErr_code_des()+returnInfo.getResult_code());
//    		    		descr = descr + "错误信息：" + returnInfo.getErr_code_des();
    		    		temp.setErrmsg(returnInfo.getErr_code_des());
    		    		if (returnInfo.getResult_code().equals("SUCCESS")) {
    		    			error.setState(1);	
    		    			error.setErrmsg("");
    		    			withDrawMapper.updatewith(error);
    		            }
    		            else{
    		            	error.setState(2);
    		            	error.setTime(timenow);
        		    		withDrawMapper.saveerror(error);
    		            }
    		    		
    		    		
    			
    		}
    		
    		
    	}
    
    }
    
    
    @Scheduled(cron = "0 10 04 ? * *" )
    @RequestMapping("wxpay/bosspay")
    public void bosspay() throws IOException {  
    	if(userMapper.bosspay() == 1){
    	Calendar c = Calendar.getInstance();
    	DateFormat format=new SimpleDateFormat("yyyyMMdd"); 
    	c.setTime(new Date());
        c.add(Calendar.DATE, - 1); 
        Date d = c.getTime();
        String timed=format.format(d);       
    	List<OrderAll> allOrder; 
    	Order order = new Order();
		String timeS = timed + "000000";
		String timeE = timed + "235959";
		order.setStartDate(timeS);
    	order.setEndDate(timeE);
    	   
    	List<Shop> shop;
    	shop = shopMapper.query();
    	
    	for(int i = 0 ; i<shop.size() ; i++ ){
    		order.setStoreid(shop.get(i).getStoreId());
    		int fee = 0;
    		try{
    		fee = skuMapper.allmoney(order);       		
    		}catch (Exception e) {
    			System.out.println("销售额为0");	
    		}
    		if (fee!=0){
    		long now =  System.currentTimeMillis();  
    		String ms = now + "";
    		String ccid = "7442cca3ea7be0cdcf85e1ebd87b49dce6fe7ee7e4c166c32f11a096610adc22";
    		String url = "https://store.lianlianchains.com/kd/invoke?func=allocEarning&" + "ccId=" + ccid + "&" + "usr=centerBank&acc=centerBank&rid=" + shop.get(i).getStoreId()
    				+ "&" + "slr=" + shop.get(i).getDeal() + "&"  + "pfm=" + shop.get(i).getDeal() + "&"  + "fld=" + shop.get(i).getField() + "&" + "dvy=" + shop.get(i).getSupply()
    				+ "&" + "tamt=" + Integer.toString(fee) + "&ak=" + ms ;

    		String res = null;
			try {
				res = httpAPIService.doGet(url);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		JSONObject json = JSON.parseObject(res);  
    		String resc = json.getString("code");
    		if (resc.equals("0")){
    			url = "https://store.lianlianchains.com/kd/query?func=queryRackAlloc&" + "ccId=" + ccid + "&" + "usr=centerBank&acc=centerBank&rid=" + shop.get(i).getStoreId()
        			 + "&ak=" + ms ;
    		try {
				res = httpAPIService.doGet(url);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		resc = json.getString("code");
    		if (resc.equals("0")){
    		JSONObject json1 = JSON.parseObject(res); 
    		String resc1 = json1.getString("result");
    		resc1 = resc1.substring(1,resc1.length()-1);
    		JSONObject json2 = JSON.parseObject(resc1); 
    		JSONObject json3 = json2.getJSONObject("amtmap");
    		JSONObject amountsupply = json3.getJSONObject("dvy");
    		int supply = amountsupply.getIntValue(shop.get(i).getSupply());
    		JSONObject amountfield = json3.getJSONObject("fld");
    		int field = amountfield.getIntValue(shop.get(i).getField());
    		JSONObject amountboss = json3.getJSONObject("pfm");
    		int plat = amountboss.getIntValue(shop.get(i).getDeal());
    		JSONObject amountdeal = json3.getJSONObject("slr");
    		int deal= amountdeal.getIntValue(shop.get(i).getDeal());
    	
    		int all = supply + field + plat + deal;
    		 		
    		WithDrawDay temp = new WithDrawDay();
    		temp.setFee(deal);
    		temp.setOpenid(shop.get(i).getDeal());
    		Date nowtime = new Date();
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		temp.setTime(sdf.format(nowtime));
    		temp.setStoreid(shop.get(i).getStoreId());
    		temp.setState(0);
    		temp.setRole("deal");
    		temp.setName(shop.get(i).getDealname());
    		temp.setStorename(shop.get(i).getStoreName());
    		String timevb = timed.toString();
    		String descr = shop.get(i).getStoreName() + "经营者";
    		temp.setDescription(descr);
    		
    		
    		withDrawMapper.savewith(temp); 
    		
    		String paternerKey="79m1jyaofjonvahln1wnoq606rvbk2gi";
    		Map<String, String> parm = new HashMap<String, String>();
    		String orderNo = RandomStringGenerator.getRandomStringByLength(32);
    		String feeS = Integer.toString(deal);
    		
    		parm.put("mch_appid", appid); //公众账号appid
    		parm.put("mchid", "1472139902"); //商户号
    		parm.put("nonce_str", RandomStringGenerator.getRandomStringByLength(32)); //随机字符串
    		parm.put("partner_trade_no",orderNo); //商户订单号
    		parm.put("openid", temp.getOpenid()); //用户openid	
    		parm.put("check_name", "FORCE_CHECK"); //校验用户姓名选项 OPTION_CHECK
    		parm.put("re_user_name",shop.get(i).getDealname()); //check_name设置为FORCE_CHECK或OPTION_CHECK，则必填
    		parm.put("amount", feeS); //转账金额
    		parm.put("desc", descr); //企业付款描述信息		
    		parm.put("spbill_create_ip", InetAddress.getLocalHost().getHostAddress());
    		parm.put("sign", PayUtil.getSign(parm, paternerKey));
    		
    		HttpResult result2 = HttpUtils.posts("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", XmlUtil.xmlFormat(parm, false));
			

    		XStream xStream = new XStream();
    		xStream.alias("xml", PayReturnInfo.class); 

    		PayReturnInfo returnInfo = (PayReturnInfo)xStream.fromXML(result2.getBody());
    		JSONObject jsonre = new JSONObject();
    		jsonre.put("return_code", returnInfo.getReturn_code());
    		jsonre.put("return_msg", returnInfo.getReturn_msg());
    		System.out.println("企业支付结果：" + returnInfo.getReturn_msg());
    		System.out.println("企业支付结果：" + returnInfo.getErr_code()+returnInfo.getErr_code_des()+returnInfo.getResult_code());
//    		descr = descr + "错误信息：" + returnInfo.getErr_code_des();
    		temp.setErrmsg(returnInfo.getErr_code_des());
    		if (returnInfo.getResult_code().equals("SUCCESS")) {
    			temp.setState(1);		
            }
            else{
            	temp.setState(2);
            }
    		
    		withDrawMapper.updatewith(temp);
    		
    		temp = new WithDrawDay();
    		temp.setFee(supply);
    		temp.setOpenid(shop.get(i).getSupply());
    		Date nowtime1 = new Date();
    		temp.setTime(sdf.format(nowtime1));
    		temp.setStoreid(shop.get(i).getStoreId());
    		temp.setState(0);
    		timevb = timed.toString();
    		temp.setRole("supply");
    		temp.setName(shop.get(i).getSupplyname());
    		temp.setStorename(shop.get(i).getStoreName());
    		descr =  shop.get(i).getStoreName() + "供货商";
    		temp.setDescription(descr);
    		   		
    		withDrawMapper.savewith(temp); 
    		
    		paternerKey="79m1jyaofjonvahln1wnoq606rvbk2gi";
    		parm = new HashMap<String, String>();
    		orderNo = RandomStringGenerator.getRandomStringByLength(32);
    		feeS = Integer.toString(supply);
    		
    		parm.put("mch_appid", appid); //公众账号appid
    		parm.put("mchid", "1472139902"); //商户号
    		parm.put("nonce_str", RandomStringGenerator.getRandomStringByLength(32)); //随机字符串
    		parm.put("partner_trade_no",orderNo); //商户订单号
    		parm.put("openid", temp.getOpenid()); //用户openid	
    		parm.put("check_name", "FORCE_CHECK"); //校验用户姓名选项 OPTION_CHECK
    		parm.put("re_user_name",shop.get(i).getSupplyname()); //check_name设置为FORCE_CHECK或OPTION_CHECK，则必填
    		parm.put("amount", feeS); //转账金额
    		parm.put("desc", descr); //企业付款描述信息		
    		parm.put("spbill_create_ip", InetAddress.getLocalHost().getHostAddress());
    		parm.put("sign", PayUtil.getSign(parm, paternerKey));
    		
    		result2 = HttpUtils.posts("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", XmlUtil.xmlFormat(parm, false));
			

    		xStream = new XStream();
    		xStream.alias("xml", PayReturnInfo.class); 

    		returnInfo = (PayReturnInfo)xStream.fromXML(result2.getBody());
    		jsonre = new JSONObject();
    		jsonre.put("return_code", returnInfo.getReturn_code());
    		jsonre.put("return_msg", returnInfo.getReturn_msg());
    		System.out.println("企业支付结果：" + returnInfo.getReturn_msg());
    		System.out.println("企业支付结果：" + returnInfo.getErr_code()+returnInfo.getErr_code_des()+returnInfo.getResult_code());
//    		descr = descr + "错误信息：" + returnInfo.getErr_code_des();
    		temp.setErrmsg(returnInfo.getErr_code_des());
    		if (returnInfo.getResult_code().equals("SUCCESS")) {
    			temp.setState(1);		
            }
            else{
            	temp.setState(2);
            }
    		
    		withDrawMapper.updatewith(temp);
    		

    		temp = new WithDrawDay();
    		temp.setFee(field);
    		temp.setOpenid(shop.get(i).getField());
    		Date nowtime2 = new Date();
    		temp.setTime(sdf.format(nowtime2));
    		temp.setStoreid(shop.get(i).getStoreId());
    		temp.setState(0);
    		temp.setRole("field");
    		temp.setName(shop.get(i).getFieldname());
    		temp.setStorename(shop.get(i).getStoreName());
    		timevb = timed.toString();
    		descr = shop.get(i).getStoreName() + "场地";
    		temp.setDescription(descr);
    		   		
    		withDrawMapper.savewith(temp); 
    		
    		paternerKey="79m1jyaofjonvahln1wnoq606rvbk2gi";
    		parm = new HashMap<String, String>();
    		orderNo = RandomStringGenerator.getRandomStringByLength(32);
    		feeS = Integer.toString(field);
    		
    		parm.put("mch_appid", appid); //公众账号appid
    		parm.put("mchid", "1472139902"); //商户号
    		parm.put("nonce_str", RandomStringGenerator.getRandomStringByLength(32)); //随机字符串
    		parm.put("partner_trade_no",orderNo); //商户订单号
    		parm.put("openid", temp.getOpenid()); //用户openid	
    		parm.put("check_name", "FORCE_CHECK"); //校验用户姓名选项 OPTION_CHECK
    		parm.put("re_user_name",shop.get(i).getFieldname()); //check_name设置为FORCE_CHECK或OPTION_CHECK，则必填
    		parm.put("amount", feeS); //转账金额
    		parm.put("desc", descr); //企业付款描述信息		
    		parm.put("spbill_create_ip", InetAddress.getLocalHost().getHostAddress());
    		parm.put("sign", PayUtil.getSign(parm, paternerKey));
    		
    		result2 = HttpUtils.posts("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", XmlUtil.xmlFormat(parm, false));
			

    		xStream = new XStream();
    		xStream.alias("xml", PayReturnInfo.class); 

    		returnInfo = (PayReturnInfo)xStream.fromXML(result2.getBody());
    		jsonre = new JSONObject();
    		jsonre.put("return_code", returnInfo.getReturn_code());
    		jsonre.put("return_msg", returnInfo.getReturn_msg());
    		System.out.println("企业支付结果：" + returnInfo.getReturn_msg());
    		System.out.println("企业支付结果：" + returnInfo.getErr_code()+returnInfo.getErr_code_des()+returnInfo.getResult_code());
//    		descr = descr + "错误信息：" + returnInfo.getErr_code_des();
    		temp.setErrmsg(returnInfo.getErr_code_des());
    		if (returnInfo.getResult_code().equals("SUCCESS")) {
    			temp.setState(1);		
            }
            else{
            	temp.setState(2);
            }
    		
    		withDrawMapper.updatewith(temp);
    		

    		temp = new WithDrawDay();
    		temp.setFee(plat);
    		temp.setOpenid(shop.get(i).getDeal());
    		Date nowtime3 = new Date();
    		temp.setTime(sdf.format(nowtime3));
    		temp.setStoreid(shop.get(i).getStoreId());
    		temp.setState(0);
    		temp.setRole("platform");
    		temp.setName("郑宏");
    		temp.setStorename(shop.get(i).getStoreName());
    		timevb = timed.toString();
    		descr = shop.get(i).getStoreName() + "平台";
    		temp.setDescription(descr);
    		   		
    		withDrawMapper.savewith(temp); 
    		
    		paternerKey="79m1jyaofjonvahln1wnoq606rvbk2gi";
    		parm = new HashMap<String, String>();
    		orderNo = RandomStringGenerator.getRandomStringByLength(32);
    		feeS = Integer.toString(plat);
    		
    		parm.put("mch_appid", appid); //公众账号appid
    		parm.put("mchid", "1472139902"); //商户号
    		parm.put("nonce_str", RandomStringGenerator.getRandomStringByLength(32)); //随机字符串
    		parm.put("partner_trade_no",orderNo); //商户订单号
    		parm.put("openid", "oyNAN0Yf3wEidxwR4jkcAlsqHaU8"); //用户openid	
    		parm.put("check_name", "FORCE_CHECK"); //校验用户姓名选项 OPTION_CHECK
    		parm.put("re_user_name","郑宏"); //check_name设置为FORCE_CHECK或OPTION_CHECK，则必填
    		parm.put("amount", feeS); //转账金额
    		parm.put("desc", descr); //企业付款描述信息		
    		parm.put("spbill_create_ip", InetAddress.getLocalHost().getHostAddress());
    		parm.put("sign", PayUtil.getSign(parm, paternerKey));
    		
    		result2 = HttpUtils.posts("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", XmlUtil.xmlFormat(parm, false));
			

    		xStream = new XStream();
    		xStream.alias("xml", PayReturnInfo.class); 

    		returnInfo = (PayReturnInfo)xStream.fromXML(result2.getBody());
    		jsonre = new JSONObject();
    		jsonre.put("return_code", returnInfo.getReturn_code());
    		jsonre.put("return_msg", returnInfo.getReturn_msg());
    		System.out.println("企业支付结果：" + returnInfo.getReturn_msg());
    		System.out.println("企业支付结果：" + returnInfo.getErr_code()+returnInfo.getErr_code_des()+returnInfo.getResult_code());
 //   		descr = descr + "错误信息：" + returnInfo.getErr_code_des();
    		temp.setErrmsg(returnInfo.getErr_code_des());
    		temp.setDescription(descr);
    		if (returnInfo.getResult_code().equals("SUCCESS")) {
    			temp.setState(1);		
            }
            else{
            	temp.setState(2);
            }
    		
    		withDrawMapper.updatewith(temp);
    		}
    		else
    		{
    			System.out.println("区块链分账错误");
    		}
    		}
    		else{
    			System.out.println("区块链返回分账错误");
    		}
    		}
    		}
    	}
    }
    
    @RequestMapping("CVS/querywithuser")
    public Msg querywithman(WithDrawDay order) throws IOException {  	
    	int tempnum = 0;
    	tempnum = order.getPage();
    	order.setPage(tempnum*order.getPagenum());
    	List<WithDrawDay> temp;  
    	temp = withDrawMapper.querywithman(order);
    	WithdrawPage withdrawPage = new WithdrawPage();
    	withdrawPage.setWithDrawDay(temp);
    	tempnum = withDrawMapper.querywithmannum(order);
    	withdrawPage.setTotalpage(tempnum/order.getPagenum()+1); 
    	
    	return ResultUtil.success(withdrawPage);
    }
    
    @RequestMapping("CVS/querywithstore")
    public Msg querywithstore(WithDrawDay order) throws IOException {  
    	int tempnum = 0;
    	tempnum = order.getPage();
    	order.setPage(tempnum*order.getPagenum());
    	List<WithDrawDay> temp;  
    	temp = withDrawMapper.querywithstore(order);
    	WithdrawPage withdrawPage = new WithdrawPage();
    	withdrawPage.setWithDrawDay(temp);
    	tempnum = withDrawMapper.querywithstorenum(order);
    	withdrawPage.setTotalpage(tempnum/order.getPagenum()+1); 
    	return ResultUtil.success(withdrawPage);
             	
    }
    

    @RequestMapping("CVS/querywithusersum")
    public Msg querywithusersum(WithDrawDay order) throws IOException {  	
    	int tempnum = 0;  
    	tempnum = withDrawMapper.querywithmansum(order);   	
    	return ResultUtil.success(tempnum);
    }
    
    @RequestMapping("CVS/querywithstoresum")
    public Msg querywithstoresum(WithDrawDay order) throws IOException {  	
    	int tempnum = 0;
    	tempnum = order.getPage();
    	order.setPage(tempnum*order.getPagenum());
    	List<WithDrawDay> temp;  
    	temp = withDrawMapper.querywithstoresum(order);
    	WithdrawPage withdrawPage = new WithdrawPage();
    	withdrawPage.setWithDrawDay(temp);
    	tempnum = withDrawMapper.querywithstoresumnum(order);
    	withdrawPage.setTotalpage(tempnum/order.getPagenum()+1); 
    	return ResultUtil.success(withdrawPage);
    }
    
    @RequestMapping("wxpay/check")
    public int check(String orderNo) throws IOException {  	
    
        
        Order Order = new Order();
        Order.setOrderNo(orderNo);
        Order.setCheckstate(1);	
		return orderMapper.updatecheck(Order);        
    }
    
    @RequestMapping("wxpay/queryOrder")
    public List<Order> queryOrder(Order order) throws IOException {
    	List<Order> allOrder;
    	allOrder=orderMapper.query(order);
    	for(int j =0;j<allOrder.size();j++){
    		List<OrderGood> temp;
        	temp=orderMapper.queryGood(allOrder.get(j));  
        	allOrder.get(j).setTemp(temp);
    	}
    	return allOrder;  	       
    }
    
    @RequestMapping("wxpay/queryOrderByNo")
    public Order queryOrderByNo(Order order) throws IOException {
    		Order returnOrder;
    		returnOrder=orderMapper.querybyno(order);
    		List<OrderGood> temp;
        	temp=orderMapper.queryGood(returnOrder);  
        	returnOrder.setStore(shopMapper.querybyid(returnOrder.getStoreid()));
        	returnOrder.setTemp(temp);
    	return returnOrder;  	       
    }
    
    @RequestMapping("wxpay/queryShopOrder")
    public List<Order> queryShopOrder(Order order) throws IOException {
    	List<Order> allOrder;
    	allOrder=orderMapper.queryshop(order);
    	for(int j =0;j<allOrder.size();j++){
    		List<OrderGood> temp;
        	temp=orderMapper.queryGood(allOrder.get(j));  
        	allOrder.get(j).setTemp(temp);
    	}
    	return allOrder;  	       
    }
    
    @RequestMapping("wxpay/queryShopOrderByPage")
    public Orderpage queryShopOrderByPage(Order order) throws IOException {
    	order.setPage(order.getPage()*5);
    	Orderpage orderpage = new Orderpage();
    	List<Order> allOrder = orderMapper.querybypageshop(order);
    	  
    	for(int j =0;j<allOrder.size();j++){
    		List<OrderGood> temp;
        	temp=orderMapper.queryGood(allOrder.get(j));  
        	allOrder.get(j).setTemp(temp);
    	}
    	
    	orderpage.setOrder(allOrder);
    	
    	int tempcount = orderMapper.querytotalpageshop(order.getStoreid());
    	int temppage = tempcount/5;
    	if(tempcount%5==0){
    	orderpage.setTotalpage(temppage);
    	}
    	else{
    	orderpage.setTotalpage(temppage+1);
    	}
    	return orderpage;  	      
    }
    
    @RequestMapping("wxpay/queryOrderByPage")
    public Orderpage queryOrderByPage(Order order) throws IOException {
    	order.setPage(order.getPage()*5);
    	Orderpage orderpage = new Orderpage();
    	List<Order> allOrder = orderMapper.querybypage(order);
    	  
    	for(int j =0;j<allOrder.size();j++){
    		List<OrderGood> temp;
        	temp=orderMapper.queryGood(allOrder.get(j));  
        	allOrder.get(j).setTemp(temp);
    	}
    	orderpage.setOrder(allOrder);
    	int tempcount = orderMapper.querytotalpage(order.getOpenid());
    	int temppage = tempcount/5;
    	if(tempcount%5==0){
    	orderpage.setTotalpage(temppage);
    	}
    	else{
    	orderpage.setTotalpage(temppage+1);
    	}
    	return orderpage;  	       
    }
    
    
    @RequestMapping("wxpay/queryGood")
    public List<OrderGood> queryGood(Order order) throws IOException {    	
    	List<OrderGood> temp;
    	temp=orderMapper.queryGood(order);  
		return temp;	
    }
    
    
/*    @RequestMapping(value = "wxpay/withdraw")
 	public String transferPay(Order Order,HttpServletResponse response) {
    	int fee = (int) (Order.getFee()*100); 
    	String feeS = Integer.toString(fee);
    	String paternerKey="79m1jyaofjonvahln1wnoq606rvbk2gi";
		Map<String, String> parm = new HashMap<String, String>();
		String orderNo = RandomStringGenerator.getRandomStringByLength(32);
		Order.setOrderNo(orderNo);
		try{		
		parm.put("mch_appid", appid); //公众账号appid
		parm.put("mchid", "1472139902"); //商户号
		parm.put("nonce_str", RandomStringGenerator.getRandomStringByLength(32)); //随机字符串
		parm.put("partner_trade_no",orderNo); //商户订单号
		parm.put("openid", Order.getOpenid()); //用户openid	
		parm.put("check_name", "FORCE_CHECK"); //校验用户姓名选项 OPTION_CHECK
		parm.put("re_user_name", "安迪"); //check_name设置为FORCE_CHECK或OPTION_CHECK，则必填
		parm.put("amount", feeS); //转账金额
		parm.put("desc", Order.getDescription()); //企业付款描述信息		
		parm.put("spbill_create_ip", InetAddress.getLocalHost().getHostAddress());
		parm.put("sign", PayUtil.getSign(parm, paternerKey));
		Order.setMch_id("1472139902");
		Date date=new Date(); 
		DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss"); 
		String time=format.format(date);
		Order.setTime(time);
		Order.setState(2);
		withDrawMapper.save(Order);
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		HttpResult result = HttpUtils.posts("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", XmlUtil.xmlFormat(parm, false));
			

		XStream xStream = new XStream();
		xStream.alias("xml", PayReturnInfo.class); 

		PayReturnInfo returnInfo = (PayReturnInfo)xStream.fromXML(result.getBody());
		JSONObject json = new JSONObject();
		json.put("return_code", returnInfo.getReturn_code());
		json.put("return_msg", returnInfo.getReturn_msg());
		if (returnInfo.getReturn_code().equals("SUCCESS")) {
        	Order.setState(1);		
        }
        else{
        	Order.setState(0);	
        }               
		withDrawMapper.update(Order);  	
		return json.toJSONString();	
	}
    
    @RequestMapping("wxpay/refund")
    public String refund(Order Order,HttpServletResponse response) throws Exception {	
    	int fee = (int) (Order.getFee()*100); 
    	String feeS = Integer.toString(fee);
    	String paternerKey="79m1jyaofjonvahln1wnoq606rvbk2gi";
		Map<String, String> parm = new HashMap<String, String>();
		try{		
		parm.put("appid", appid); //公众账号appid
		parm.put("mchid", mch_id); //商户号
		//parm.put("device_info", device_info); 
		parm.put("nonce_str", RandomStringGenerator.getRandomStringByLength(32)); //随机字符串
		parm.put("transaction_id", Order.getOrderNo()); //商户订单号
		parm.put("out_trade_no", RandomStringGenerator.getRandomStringByLength(32)); //用户openid	
		parm.put("total_fee", feeS); //金额
		parm.put("refund_fee", feeS); //退款金额	
		parm.put("op_user_id",mch_id);
		parm.put("sign", PayUtil.getSign(parm, paternerKey));
		Order.setMch_id("1472139902");
		Date date=new Date(); 
		DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss"); 
		String time=format.format(date);
		Order.setTime(time);
		Order.setState(2);
		refundMapper.save(Order);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		HttpResult result = HttpUtils.posts("https://api.mch.weixin.qq.com/secapi/pay/refund", XmlUtil.xmlFormat(parm, false));
			

		XStream xStream = new XStream();
		xStream.alias("xml", RefundReturnInfo.class); 

		RefundReturnInfo returnInfo = (RefundReturnInfo)xStream.fromXML(result.getBody());
		JSONObject json = new JSONObject();
		json.put("return_code", returnInfo.getReturn_code());
		json.put("return_msg", returnInfo.getReturn_msg());
		if (returnInfo.getReturn_code().equals("SUCCESS")) {
        	Order.setState(1);		
        }
        else{
        	Order.setState(0);	
        }               
		refundMapper.update(Order);  		
		return json.toJSONString();	
    }*/
}
