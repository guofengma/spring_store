package com.lxg.springboot.controller;

import com.lxg.springboot.mapper.ApplyMapper;
import com.lxg.springboot.mapper.FieldMapper;
import com.lxg.springboot.model.Apply;
import com.lxg.springboot.model.ApplyIf;
import com.lxg.springboot.model.Applypage;
import com.lxg.springboot.model.Field;
import com.lxg.springboot.model.Msg;
import com.lxg.springboot.model.Result;
import com.lxg.springboot.model.ResultUtil;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;


@RestController
@RequestMapping("CVS/apply/")
public class ApplyController extends BaseController {
	
	@Resource
    private ApplyMapper applyMapper;
	@Resource
    private FieldMapper fieldMapper;

    @RequestMapping("insert")
    public int save(Apply apply) { 
    	int i = applyMapper.querymax();
    	apply.setId(i+1);
    	applyMapper.save(apply);
    	i=i+1;
    	return i;
    }    
    
    @RequestMapping("update")
    public Result update(Apply apply) {
    	applyMapper.update(apply);
    	return new Result();
    } 
    
    @RequestMapping("opapply")
    public Result opapply(String openid,int id,int roletype,String optype) {
    Apply apply = new Apply();
    apply.setId(id);
    if(roletype==0){
    	if(optype.equals("join")){
    		apply.setField(openid);
    	}
    	else{
    		apply.setField("");
    	}
    	applyMapper.updatefield(apply);	
    }
    else if(roletype==1){
    	if(optype.equals("join")){
    		apply.setDeal(openid);
    	}
    	else{
    		apply.setDeal("");
    	}
    	applyMapper.updatedeal(apply);	
    }
    else if(roletype==2){
    	if(optype.equals("join")){
    		apply.setSupply(openid);
    	}
    	else{
    		apply.setSupply("");
    	}
    	applyMapper.updatesupply(apply);	
    }
    	return new Result();
    }
        
    @RequestMapping("queryapply")
    public Applypage querybypage(Apply apply) {
    	List<Apply> applyA;  
    	int temp = 0;
    	if (apply.getAddress()==null || apply.getAddress().isEmpty()){
    	applyA = applyMapper.query(apply);
    	temp = applyMapper.querypage(apply);}
    	else {
    	applyA = applyMapper.querybyad(apply);
    	temp = applyMapper.querypagead(apply);
    	}   
    	Applypage applypage = new Applypage();
    	applypage.setApply(applyA);
    	applypage.setTotalpage(temp);
    	return applypage;
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
}
