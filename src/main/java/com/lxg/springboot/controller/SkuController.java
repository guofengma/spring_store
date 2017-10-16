package com.lxg.springboot.controller;

import com.lxg.springboot.mapper.SkuMapper;
import com.lxg.springboot.model.Cart;
import com.lxg.springboot.model.Result;
import com.lxg.springboot.model.Sku;
import com.lxg.springboot.util.CharacterUtil;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
     
    @RequestMapping("queryall")
    public List<Sku> query(Sku sku) {	
    	return skuMapper.queryall(sku);  	
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

      
}
