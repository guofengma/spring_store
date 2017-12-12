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
    
    @Scheduled(cron = "0 15 6 ? * *" )
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
    		int fee = skuMapper.allmoney(order);       		
    		long now =  System.currentTimeMillis();  
    		String ms = now + "";
    		String ccid = "f90524bd12ec53448f2cc7c178e757d07eb53772c2ab355b4d5c00abbcd36375";
    		String url = "https://store.lianlianchains.com/alloc/invoke?func=allocEarning&" + "ccId=" + ccid + "&" + "usr=centerBank&acc=centerBank&rid=" + shop.get(i).getStoreId()
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
    			url = "https://store.lianlianchains.com/alloc/query?func=queryRackAlloc&" + "ccId=" + ccid + "&" + "usr=centerBank&acc=centerBank&rid=" + shop.get(i).getStoreId()
        			 + "&ak=" + ms ;
    		}
    		try {
				res = httpAPIService.doGet(url);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
    		temp.setTime(timed);
    		temp.setStoreid(shop.get(i).getStoreId());
    		temp.setState(0);
    		String timevb = timed.toString();
    		String descr = "日结算:" + timevb + shop.get(i).getStoreName() + "经营者";
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
    		temp.setTime(timed);
    		temp.setStoreid(shop.get(i).getStoreId());
    		temp.setState(0);
    		timevb = timed.toString();
    		descr = "日结算:" + timevb + shop.get(i).getStoreName() + "供货商";
    		temp.setDescription(descr);
    		   		
    		withDrawMapper.savewith(temp); 
    		
    		paternerKey="79m1jyaofjonvahln1wnoq606rvbk2gi";
    		parm = new HashMap<String, String>();
    		orderNo = RandomStringGenerator.getRandomStringByLength(32);
    		feeS = Integer.toString(deal);
    		
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
    		temp.setTime(timed);
    		temp.setStoreid(shop.get(i).getStoreId());
    		temp.setState(0);
    		timevb = timed.toString();
    		descr = "日结算:" + timevb + shop.get(i).getStoreName() + "场地";
    		temp.setDescription(descr);
    		   		
    		withDrawMapper.savewith(temp); 
    		
    		paternerKey="79m1jyaofjonvahln1wnoq606rvbk2gi";
    		parm = new HashMap<String, String>();
    		orderNo = RandomStringGenerator.getRandomStringByLength(32);
    		feeS = Integer.toString(deal);
    		
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
    		temp.setTime(timed);
    		temp.setStoreid(shop.get(i).getStoreId());
    		temp.setState(0);
    		timevb = timed.toString();
    		descr = "日结算:" + timevb + shop.get(i).getStoreName() + "平台";
    		temp.setDescription(descr);
    		   		
    		withDrawMapper.savewith(temp); 
    		
    		paternerKey="79m1jyaofjonvahln1wnoq606rvbk2gi";
    		parm = new HashMap<String, String>();
    		orderNo = RandomStringGenerator.getRandomStringByLength(32);
    		feeS = Integer.toString(deal);
    		
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
    		
    		result2 = HttpUtils.posts("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", XmlUtil.xmlFormat(parm, false));
			

    		xStream = new XStream();
    		xStream.alias("xml", PayReturnInfo.class); 

    		returnInfo = (PayReturnInfo)xStream.fromXML(result2.getBody());
    		jsonre = new JSONObject();
    		jsonre.put("return_code", returnInfo.getReturn_code());
    		jsonre.put("return_msg", returnInfo.getReturn_msg());
    		System.out.println("企业支付结果：" + returnInfo.getReturn_msg());
    		System.out.println("企业支付结果：" + returnInfo.getErr_code()+returnInfo.getErr_code_des()+returnInfo.getResult_code());
    		if (returnInfo.getResult_code().equals("SUCCESS")) {
    			temp.setState(1);		
            }
            else{
            	temp.setState(2);
            }
    		
    		withDrawMapper.updatewith(temp);
    		
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
