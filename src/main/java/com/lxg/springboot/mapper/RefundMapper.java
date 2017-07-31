package com.lxg.springboot.mapper;

import java.util.List;

import com.lxg.springboot.model.Order;

public interface RefundMapper {

	int save(Order order);
	
	int update(Order order);
	
	List<Order> query(String id);
	
}