package com.lxg.springboot.mapper;

import java.util.List;

import com.lxg.springboot.model.Apply;
import com.lxg.springboot.model.Field;

public interface ApplyMapper {

	void save(Apply apply);
	
	void update(Apply apply);
	
	void delete(Apply apply);
	
	Apply queryone(Apply apply);
	
	List<Apply> query(Apply apply);
	
	List<Apply> querybyopenid(Apply apply);
	
	List<Apply> querybyad(Apply apply);
	
	int querymax();
	
	int querybyopenidcount(Apply apply);
	
	int querypage(Apply apply);
	
	int querypagead(Apply apply);
	
	void updatefield(Apply apply);
	
	void updatedeal(Apply apply);
	
	void updatesupply(Apply apply);
	
	void updatename(Apply apply);
	
	void updateaddress(Apply apply);
	
	void updatefieldaddress(Apply apply);
	
	void updatedealaddress(Apply apply);
	
	void updatesupplyaddress(Apply apply);
	
	void updatefieldstate(Apply apply);
	
	void updatedealstate(Apply apply);
	
	void updatesupplystate(Apply apply);
	
	void updatefieldper(Apply apply);
	
	void updatedealper(Apply apply);
	
	void updatesupplyper(Apply apply);
	
	void updateplatper(Apply apply);
	
	int fieldcount(Apply apply);
	
	int dealcount(Apply apply);
	
	int supplycount(Apply apply);
	
	Field queryfield(Apply apply);
	
	Field querydeal(Apply apply);
	
	Field querysupply(Apply apply);
	
}