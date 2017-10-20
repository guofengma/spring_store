package com.lxg.springboot.mapper;

import java.util.List;

import com.lxg.springboot.model.Sku;
import com.lxg.springboot.model.Order;
import com.lxg.springboot.model.OrderAll;
import com.lxg.springboot.model.Intea;;

public interface SkuMapper {
		
	List<Sku> queryall(Sku sku);
	
	List<Sku> queryalldown(Sku sku);
	
	Sku query(Sku sku);
	
	Sku querybyCode(Sku sku);
	
	void inserthistory(Sku sku);
	
	void insert(Sku sku);
	
	void insertdown(Sku sku);
	
	void delete(Sku sku);
	
	void deletedown(Sku sku);
	
	void update(Sku sku);
	
	void updatedown(Sku sku);
	
	Intea comment(Sku sku);
	
	Intea count(Sku sku);
	
	List<OrderAll> queryorder(Order order);
}