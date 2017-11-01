package com.lxg.springboot.mapper;

import java.util.List;

import com.lxg.springboot.model.Apply;
import com.lxg.springboot.model.Field;

public interface ApplyMapper {

	void save(Apply apply);
	
	void update(Apply apply);
	
	List<Apply> query(Apply apply);
	
	List<Apply> querybyad(Apply apply);
	
	int querymax();
	
}