package com.lxg.springboot.mapper;

import java.util.List;

import com.lxg.springboot.model.Order;
import com.lxg.springboot.model.OrderGood;

public interface OrderMapper {

	int save(Order order);
	
	int saveshop(Order order);
	
	int savecard(Order order);
	
	int savescore(Order order);
	
	int update(Order order);
	
	int updateshop(Order order);
	
	int updatecard(Order order);
	
	int updatescore(Order order);
	
	int updatecheck(Order order);
	
	int updateredpack(Order order);
	
	int countredpack(Order order);
	
	Order querybyno(Order order);
	
	Order queryredpack(Order order);
	
	Order querybynoshop(Order order);
	
	Order querybynocard(Order order);
	
	Order querybynoscore(Order order);
	
	List<Order> query(Order order);
	
	List<Order> queryshop(Order order);
	
	int savegood(OrderGood ordergood);
	
	List<OrderGood> queryGood(Order order);
	
	List<Order> querybypage(Order order);
	
	int querytotalpage(String openid);
	
	List<Order> querybypageshop(Order order);
	
	int querytotalpageshop(String storeid);
	
}