package com.lxg.springboot.mapper;

import java.util.List;

import com.lxg.springboot.model.Sku;

public interface SkuMapper {
		
	List<Sku> queryall(Sku sku);
	
	List<Sku> queryalldown(Sku sku);
	
	Sku query(Sku sku);
	
	Sku querybyCode(Sku sku);
	
	void update(Sku sku);
	
	void updatedown(Sku sku);
		
}