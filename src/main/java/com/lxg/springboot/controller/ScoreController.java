package com.lxg.springboot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxg.springboot.mapper.ScoreMapper;
import com.lxg.springboot.model.Msg;
import com.lxg.springboot.model.Result;
import com.lxg.springboot.model.ResultUtil;
import com.lxg.springboot.model.Score;
import com.lxg.springboot.service.HttpAPIService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by zhenghong
 * on 2017/4/25.
 */
@RestController
@RequestMapping("CVS/score/")
public class ScoreController extends BaseController {
	
	@Resource
    private ScoreMapper scoreMapper;
	@Resource
	private HttpAPIService httpAPIService;
    
 /*   @RequestMapping("update")
    public Result update(Score score) {
  
    	int count = scoreMapper.count(score);
    	if (count == 0){
    		scoreMapper.save(score);
    	}else{
    		scoreMapper.update(score);
    	}
    	return new Result();
    }
    
    @RequestMapping("querybytype")
    public int querybytype(Score score) {
    	
    	int count = scoreMapper.count(score);
    	if (count == 0)
    		return 0;
    	
    	int s = scoreMapper.querybytype(score);
    	return s;  	
    }   
    
    @RequestMapping("query")
    public int query(String openid) {
    	
    	int score = scoreMapper.query(openid);
    	return score;  	
    }  */
	
	   @RequestMapping("update")
	    public Result update(Score score) {
	  
	    	int count = scoreMapper.count(score);
	    	if (count == 0){
	    		scoreMapper.save(score);
	    	}else{
	    		scoreMapper.update(score);
	    	}
	    	return new Result();
	    }
	    
	    @RequestMapping("querybytype")
	    public int querybytype(Score score) {
	    	
	    	int count = scoreMapper.count(score);
	    	if (count == 0)
	    		return 0;
	    	
	    	int s = scoreMapper.querybytype(score);
	    	return s;  	
	    }   
	    
	    @RequestMapping("query")
	    public Msg query(String unionId) {
	    	System.out.println("企业支付结果：" + unionId);
			

	    	String ccid = "292bed8e86bc425bbd9351d6af4ed51bd80c40a00633843cf63028498837e178";
			String urla = "https://store.lianlianchains.com/kd/query?func=getBalance&" + "ccId=" + ccid + "&" + "usr=" + unionId	+ "&" + "acc=" + unionId;
			
			String res = null;
			
			try {
				res = httpAPIService.doGet(urla);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("企业支付结果：" + res);
			
			JSONObject json = JSON.parseObject(res);  
			String resc = json.getString("code");
			
			if (!resc.equals("0")){
				return ResultUtil.fail("区块链连接错误");
			}	
			else{
				return ResultUtil.success(json.getString("result"));}
	    }  
	    
	    @RequestMapping("querydetail")
	    public Msg querydetail(String unionId ,int start ,int pagenum) {
	    	

	    	String ccid = "292bed8e86bc425bbd9351d6af4ed51bd80c40a00633843cf63028498837e178";
			String urla = "https://store.lianlianchains.com/kd/query?func=getTransInfo&" + "ccId=" + ccid + "&" + "usr=" + unionId	+ "&" + "acc=" + unionId + "&" + "qacc=" + unionId	+ "&" + "bsq=" + start + "&" + "cnt=" + pagenum;
			
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
			else{
				return ResultUtil.success(json.getString("result"));}
	    }  
}
