package com.lxg.springboot.mapper;

import java.util.List;

import com.lxg.springboot.model.Order;
import com.lxg.springboot.model.OrderGood;

public interface OrderMapper {

	int save(Order order);
	
	int update(Order order);
	
	int updatecheck(Order order);
	
	Order querybyno(Order order);
	
	List<Order> query(Order order);
	
	List<Order> queryshop(Order order);
	
	int savegood(OrderGood ordergood);
	
	List<OrderGood> queryGood(Order order);
	
	List<Order> querybypage(Order order);
	
	int querytotalpage(String openid);
	
	List<Order> querybypageshop(Order order);
	
	int querytotalpageshop(String storeid);
	
}