package com.lxg.springboot.mapper;

import java.util.List;

import com.lxg.springboot.model.Apply;
import com.lxg.springboot.model.Field;

public interface ApplyMapper {

	void save(Apply apply);
	
	void update(Apply apply);
	
	Apply queryone(Apply apply);
	
	List<Apply> query(Apply apply);
	
	List<Apply> querybyad(Apply apply);
	
	int querymax();
	
	int querypage(Apply apply);
	
	int querypagead(Apply apply);
	
	void updatefield(Apply apply);
	
	void updatedeal(Apply apply);
	
	void updatesupply(Apply apply);
	
}