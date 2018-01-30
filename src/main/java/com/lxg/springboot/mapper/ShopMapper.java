package com.lxg.springboot.mapper;

import java.util.List;

import com.lxg.springboot.model.Order;
import com.lxg.springboot.model.OrderAll;
import com.lxg.springboot.model.Shop;
import com.lxg.springboot.model.UserBoss;

public interface ShopMapper {
	
	void insert(Shop shop);
	
	void update(Shop shop);
	
	void insertmore(Shop shop);

	List<Shop> query();
	
	Shop querybyid(String storeid);
	
	List<Order> totle(Order order);
	
	List<OrderAll> usermoney(UserBoss user);
			
}