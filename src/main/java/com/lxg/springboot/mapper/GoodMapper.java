package com.lxg.springboot.mapper;

import java.util.List;

import com.lxg.springboot.model.Good;

public interface GoodMapper {
		
	List<Good> queryall(Good good);
	
	Good query(Good good);
	
	Good querybyCode(Good good);
	
	void update(Good good);
	
	void insert(Good good);
}