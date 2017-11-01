package com.lxg.springboot.controller;

import com.lxg.springboot.mapper.ApplyMapper;
import com.lxg.springboot.mapper.FieldMapper;
import com.lxg.springboot.model.Apply;
import com.lxg.springboot.model.Field;
import com.lxg.springboot.model.Result;

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
    	apply.setId(i);
    	applyMapper.save(apply);
    	return i;
    }    
    
    @RequestMapping("update")
    public Result update(Apply apply) {
    	applyMapper.update(apply);
    	return new Result();
    } 
        
    @RequestMapping("queryapply")
    public List<Apply> querybypage(Apply apply) {
    	List<Apply> applyA;  
    	if (apply.getAddress()==null || apply.getAddress().isEmpty()){
    	applyA = applyMapper.query(apply);}
    	else {
    	applyA = applyMapper.querybyad(apply);
    	}   		
    	return applyA;
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
}
