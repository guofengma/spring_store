package com.lxg.springboot.log;

import com.lxg.springboot.mapper.CartMapper;
import com.lxg.springboot.mapper.GoodMapper;
import com.lxg.springboot.mapper.OrderMapper;
import com.lxg.springboot.mapper.RefundMapper;
import com.lxg.springboot.mapper.WithDrawMapper;
import com.lxg.springboot.mapper.ShopMapper;
import com.lxg.springboot.mapper.SkuMapper;
import com.lxg.springboot.mapper.UserMapper;
import com.lxg.springboot.model.Applypage;
import com.lxg.springboot.model.Card;
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
import com.lxg.springboot.util.MatrixToImageWriter;
import com.lxg.springboot.util.PayUtil;
import com.lxg.springboot.util.RandomStringGenerator;
import com.lxg.springboot.util.Signature;
import com.lxg.springboot.util.StreamUtil;
import com.lxg.springboot.util.WebUtil;
import com.lxg.springboot.util.XmlUtil;
import com.thoughtworks.xstream.XStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.OutputStream;
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
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zhenghong
 * on 2017/4/25.
 */
@RestController
public class ReturnResult {
	
	private Logger logger =  LoggerFactory.getLogger(this.getClass());
	
    @Value("${wx.appid}")
    private String appid; 
    @Value("${wx.mch_id}")
    private String mch_id;  
	@Value("${wx.appSecret}")
	private String appSecret;
    

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
	
	 @RequestMapping("qrcode")
	    public void qrcode(String data, HttpServletResponse response, Integer width, Integer height) throws Exception {

		    MatrixToImageWriter.createRqCode(data, width, height, response.getOutputStream());
	    	
	    }
	 
		@RequestMapping("wx/getTwoBarCodes")
		public void  getTwoBarCodes(String StoreId,String StoreName,int width,HttpServletResponse response) throws Exception {
			// 获取token
			String urltoken = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&" + "appid=" + appid
					+ "&" + "secret=" + appSecret;

			Token token = JSON.parseObject(httpAPIService.doGet(urltoken), Token.class);

			String url = "https://api.weixin.qq.com/wxa/getwxacode?access_token="
					+ token.getAccess_token();

			// 参数

			// 参数
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("path","pages/index/index"+"?StoreId="+ StoreId+"&StoreName="+ StoreName);
			map.put("width",width);

			HashMap<String, Object> maptemp = new HashMap<String, Object>();
			maptemp.put("Jsondata", JSON.toJSONString(map));

			// 请求头
			HashMap<String, Object> header = new HashMap<String, Object>();

			byte[] data = httpAPIService.doPostImg(url, maptemp, header);
			response.setContentType("image/png");

	        OutputStream stream = response.getOutputStream();
	        stream.write(data);
	        stream.flush();
	        stream.close();
		}
		
		@RequestMapping("CVS/apply/getTwoBarCodes")
		public void  getTwoBar(String storeid,HttpServletResponse response) throws Exception {
			
			Shop retshop = new Shop();
	    	retshop = shopMapper.querybyid(storeid);
	    	
			// 获取token
			String urltoken = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&" + "appid=" + appid
					+ "&" + "secret=" + appSecret;

			Token token = JSON.parseObject(httpAPIService.doGet(urltoken), Token.class);

			String url = "https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode?access_token="
					+ token.getAccess_token();

			// 参数

			// 参数
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("path","pages/index/index"+"?StoreId="+ storeid+"&StoreName="+ retshop.getStoreName());
			map.put("width",5000);

			HashMap<String, Object> maptemp = new HashMap<String, Object>();
			maptemp.put("Jsondata", JSON.toJSONString(map));

			// 请求头
			HashMap<String, Object> header = new HashMap<String, Object>();

			byte[] data = httpAPIService.doPostImg(url, maptemp, header);
			response.setContentType("image/png");

	        OutputStream stream = response.getOutputStream();
	        stream.write(data);
	        stream.flush();
	        stream.close();
		}

    @RequestMapping("wxpay/result")
    public void result(HttpServletRequest request,HttpServletResponse response) throws IOException {  	
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
        logger.info("微信支付通知结果：" + result);
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
            	logger.info("减库存"+ returngood.getAmount()+temp.get(i).getAmount());
            	returngood.setAmount(returngood.getAmount()-temp.get(i).getAmount());
            	goodMapper.update(returngood);		
    	}
        	orderMapper.update(Order);     
        	//企业付款
        	int fee = (int) (tempOrder.getFee()*100); 
        	logger.info("企业支付结果：" + fee);
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
    		
    		String userunion = openid;
    		tempuser.setStoreid(Order.getStoreid());
    		String bossunion = Boss.getOpenid();
    		String ccid = "";
    		String urla ="";
    		String res="";
    		
    		urla = "http://140.143.211.161/kd/invoke?func=transefer&" + "ccId=" + ccid + "&" + "usr=" + bossunion	+ "&" + "acc=" + bossunion + "&" + "reacc=" + userunion +  "&" + "amt=5" + "&tstp=消费奖励积分&desc=消费奖励积分" ;
    		
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
    			
    	    	
    			urla = "http://140.143.211.161/kd/invoke?func=transefer&" + "ccId=" + ccid + "&" + "usr=" + userunion	+ "&" + "acc=" + userunion + "&" + "reacc=" + bossunion +  "&" + "amt=" + score + "&tstp=消费抵扣积分&desc=消费抵扣积分" ;
    			
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
    		logger.info("企业支付结果：" + returnInfo.getReturn_msg());
    		logger.info("企业支付结果：" + returnInfo.getErr_code()+returnInfo.getErr_code_des()+returnInfo.getResult_code());
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
    
    @RequestMapping("wxpay/shopresult")
    public void shopresult(HttpServletRequest request,HttpServletResponse response) throws Exception { 
    	logger.info("shopresult进入回调函数");
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
        logger.info("shopresult微信支付通知结果：" + result);
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
        tempOrder = orderMapper.querybynoshop(Order);
        
        if(tempOrder.getState()!=1){        	
        if (map.get("result_code").equals("SUCCESS")) { 
        Order.setState(1);       
        String openid = tempOrder.getOpenid();
      
        orderMapper.updateshop(Order);     
        	//企业付款
        	int fee = (int) (tempOrder.getFee()*100); 
        	logger.info("shopresult企业支付金额：" + fee);
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
    		
    		try{		
    		parm.put("mch_appid", appid); //公众账号appid
    		parm.put("mchid", "1472139902"); //商户号
    		parm.put("nonce_str", RandomStringGenerator.getRandomStringByLength(32)); //随机字符串
    		parm.put("partner_trade_no",orderNo); //商户订单号
    		parm.put("openid", tempOrder.getCharges()); //用户openid	
    		parm.put("check_name", "NO_CHECK"); //校验用户姓名选项 OPTION_CHECK
    	//	parm.put("re_user_name", Boss.getNickname()); //check_name设置为FORCE_CHECK或OPTION_CHECK，则必填
    		parm.put("amount", feeS); //转账金额
    		parm.put("desc", "快点接单"); //企业付款描述信息		
    		parm.put("spbill_create_ip", InetAddress.getLocalHost().getHostAddress());
    		parm.put("sign", PayUtil.getSign(parm, paternerKey));
    		OrderPay.setMch_id("1472139902");
    		Date date=new Date(); 
    		DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss"); 
    		String time=format.format(date);
    		OrderPay.setOpenid(tempOrder.getCharges());
    		OrderPay.setTime(time);
    		OrderPay.setState(2);
    		withDrawMapper.save(OrderPay);
    		logger.info("shopresult赋值完成：" + fee);
    		} catch (UnsupportedEncodingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (UnknownHostException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}	
    		
    		HttpResult result2 = HttpUtils.posts("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", XmlUtil.xmlFormat(parm, false));
    			

    		XStream xStream = new XStream();
    		xStream.alias("xml", PayReturnInfo.class); 

    		PayReturnInfo returnInfo = (PayReturnInfo)xStream.fromXML(result2.getBody());
    		JSONObject json = new JSONObject();
    		json.put("return_code", returnInfo.getReturn_code());
    		json.put("return_msg", returnInfo.getReturn_msg());
    		logger.info("shopresult企业支付结果：" + returnInfo.getReturn_msg());
    		logger.info("shopresult企业支付结果：" + returnInfo.getErr_code()+returnInfo.getErr_code_des()+returnInfo.getResult_code());
    		if (returnInfo.getResult_code().equals("SUCCESS")) {
    			OrderPay.setState(1);		
            }
            else{
            	logger.info("shopresult通知结果：" + "提现失败");
            	OrderPay.setState(0);
            	// 获取token
        		String urltoken = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&" + "appid=" + appid
        				+ "&" + "secret=" + appSecret;

        		Token token = JSON.parseObject(httpAPIService.doGet(urltoken), Token.class);

        		logger.info("shopresult通知结果：" + "token获取成功");
        		
        		String url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token="
        				+ token.getAccess_token();

        		// 参数
        		
        		TreeMap<String,TreeMap<String,String>> params = new TreeMap<String,TreeMap<String,String>>();  
                //根据具体模板参数组装  
                params.put("keyword1",item("服务接单 ","#000000"));  
                params.put("keyword2",item(tempOrder.getTime(), "#333333"));  
                params.put("keyword3",item("系统打款中", "#333333"));  
               
        	
        		HashMap<String, Object> map1 = new HashMap<String, Object>();
        		map1.put("touser", openid);
        		map1.put("template_id", "y3szvUfb0ovtDQzMu87zIrFxtrt9l4LBpSmZYzFg-HM");
        		map1.put("form_id", tempOrder.getBonusScore());
        		map1.put("data", params);
///       		map1.put("emphasis_keyword", "keyword1.DATA");

        		HashMap<String, Object> maptemp = new HashMap<String, Object>();
        		maptemp.put("Jsondata", JSON.toJSONString(map1));

        		// 请求头
        		HashMap<String, Object> header = new HashMap<String, Object>();

        		HttpResult res = httpAPIService.doPostJson(url, maptemp, header);
        		
        		logger.info("shopresult通知结果：" + res.toString());
            }               
    		withDrawMapper.update(OrderPay);  	
    		}
        }
    }
    
    public static TreeMap<String, String> item(String value, String color) {  
        TreeMap<String, String> params = new TreeMap<String, String>();  
        params.put("value", value);  
        params.put("color", color);  
        return params;  
    }  
    
    @RequestMapping("wxpay/cardresult")
    public void cardresult(HttpServletRequest request,HttpServletResponse response) throws IOException { 
    	logger.info("cardresult进入回调函数");
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
        logger.info("cardresult微信支付通知结果：" + result);
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
        tempOrder = orderMapper.querybynocard(Order);
        
        if(tempOrder.getState()!=1){        	
        if (map.get("result_code").equals("SUCCESS")) { 
        Order.setState(1);       
        String openid = tempOrder.getOpenid();
        Card card = new Card();
        card.setOpenid(openid);
        card.setCardNo(map.get("out_trade_no"));
        orderMapper.updatecard(Order);    
        userMapper.updatecard(card);
        
        String userunion = openid;
		String ccid = "";
		String urla ="";
		String res="";
		int i = (int) (tempOrder.getFee()*10);
		urla = "http://140.143.211.161/kd/invoke?func=transefer&" + "ccId=" + ccid + "&" + "usr=kdcoinpool" + "&" + "acc=kdcoinpool"  + "&" + "reacc=" + userunion +  "&" + "amt=" + i + "&tstp=购卡奖励积分&desc=购卡奖励积分" ;
		
		res = null;
		
		try {
			res = httpAPIService.doGet(urla);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject json1 = JSON.parseObject(res);  
		String resc1 = json1.getString("code");
    	}
        }
    }
    
    
    @RequestMapping("wxpay/scoreresult")
    public void scoreresult(HttpServletRequest request,HttpServletResponse response) throws IOException { 
    	logger.info("cardresult进入回调函数");
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
        logger.info("cardresult微信支付通知结果：" + result);
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
        tempOrder = orderMapper.querybynoscore(Order);
        
        if(tempOrder.getState()!=1){        	
        if (map.get("result_code").equals("SUCCESS")) { 
        Order.setState(1);       
        String openid = tempOrder.getOpenid();
        Card card = new Card();
        card.setOpenid(openid);
        card.setCardNo(map.get("out_trade_no"));
        orderMapper.updatescore(Order);    
    	}
        }
    }
    
/*    @RequestMapping(value = "wxpay/sendredpack")
   	public String sendredpack(Order Order,HttpServletResponse response) {
      	int fee = (int) (Order.getFee()*100); 
      	String feeS = Integer.toString(fee);
      	String paternerKey="79m1jyaofjonvahln1wnoq606rvbk2gi";
  		Map<String, String> parm = new HashMap<String, String>();
  		String orderNo = RandomStringGenerator.getRandomStringByLength(32);
  		Order.setOrderNo(orderNo);
  		try{		
  		parm.put("wxappid", "wx6f335f8ecd4e9c23"); //公众账号appid
  		parm.put("mchid", "1472139902"); //商户号
  		parm.put("nonce_str", RandomStringGenerator.getRandomStringByLength(32)); //随机字符串
  		parm.put("mch_billno",orderNo); //商户订单号
  		parm.put("re_openid", Order.getOpenid()); //用户openid	
  		parm.put("send_name", "链链信息"); //check_name设置为FORCE_CHECK或OPTION_CHECK，则必填
  		parm.put("total_amount", feeS); //转账金额
  		parm.put("total_num", "1"); //企业付款描述信息	
  		parm.put("wishing", "恭喜发财"); //企业付款描述信息		
  		parm.put("client_ip", InetAddress.getLocalHost().getHostAddress());
  		parm.put("sign", PayUtil.getSign(parm, paternerKey));
  		parm.put("total_num", "1"); 
  		parm.put("act_name", "小游戏送创业基金"); 
  		parm.put("remark", "欢迎关注快点公众号"); 
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
  		HttpResult result = HttpUtils.posts("https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack", XmlUtil.xmlFormat(parm, false));
  			

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
  	}*/
    
    @RequestMapping(value = "wxpay/sendredpack")
 	public Msg transferPay(Order Order,HttpServletResponse response) {
    	int count = orderMapper.countredpack(Order);
    	if(count == 0){
    		return ResultUtil.fail("您没有获奖或已兑奖");
    	}
    	Order temp = orderMapper.queryredpack(Order);
    	Order.setState(1);
    	orderMapper.updateredpack(Order);
    	
    	int fee = (int) temp.getFee()*100;
    	Order.setFee(fee);
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
		parm.put("check_name", "NO_CHECK"); //校验用户姓名选项 OPTION_CHECK
		//parm.put("re_user_name", "安迪"); //check_name设置为FORCE_CHECK或OPTION_CHECK，则必填
		parm.put("amount", feeS); //转账金额
		parm.put("desc", "快点红包"); //企业付款描述信息		
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
		orderMapper.updateredpack(Order);
		return ResultUtil.success(feeS);
	}
  
}
