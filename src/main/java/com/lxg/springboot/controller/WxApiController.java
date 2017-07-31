package com.lxg.springboot.controller;

import com.lxg.springboot.model.HttpResult;
import com.lxg.springboot.model.Token;
import com.lxg.springboot.service.HttpAPIService;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import java.io.OutputStream;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zhenghong on 2017/4/25.
 */
@RestController
public class WxApiController {

	@Value("${wx.appid}")
	private String appid;
	@Value("${wx.appSecret}")
	private String appSecret;

	@Resource
	private HttpAPIService httpAPIService;

	@RequestMapping("wx/getopenid")
	public String getopenid(String code) throws Exception {

		String url = "https://api.weixin.qq.com/sns/jscode2session?" + "appid=" + appid + "&" + "secret=" + appSecret
				+ "&" + "js_code=" + code + "&" + "grant_type=authorization_code";

		String res = httpAPIService.doGet(url);

		return res;

	}

	@RequestMapping("wx/send")
	public HttpResult send(String openid, String templateid, String formid, String data, String page) throws Exception {

		// 获取token
		String urltoken = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&" + "appid=" + appid
				+ "&" + "secret=" + appSecret;

		Token token = JSON.parseObject(httpAPIService.doGet(urltoken), Token.class);

		String url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token="
				+ token.getAccess_token();

		// 参数
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("touser", openid);
		map.put("template_id", templateid);
		map.put("form_id", formid);
		map.put("data", JSON.parse(data));
		map.put("page", page);
		map.put("emphasis_keyword", "keyword1.DATA");

		HashMap<String, Object> maptemp = new HashMap<String, Object>();
		maptemp.put("Jsondata", JSON.toJSONString(map));

		// 请求头
		HashMap<String, Object> header = new HashMap<String, Object>();

		HttpResult res = httpAPIService.doPostJson(url, maptemp, header);

		return res;

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

}
