package com.lxg.springboot.mapper;

import java.util.List;

import com.lxg.springboot.model.Order;
import com.lxg.springboot.model.OrderAll;
import com.lxg.springboot.model.Shop;
import com.lxg.springboot.model.ShopOrder;
import com.lxg.springboot.model.UserBoss;

public interface ShopOrderMapper {
	
	void insert(ShopOrder ShopOrder);
	
	void update(ShopOrder ShopOrder);
	
	void updatestate(ShopOrder ShopOrder);
	
	void updatestateorder(ShopOrder ShopOrder);
	
	void updatestateonly(ShopOrder ShopOrder);
	
	void updateimage(ShopOrder ShopOrder);
	
	void delete(ShopOrder ShopOrder);

	List<ShopOrder> query();
	
	int querybyidcount(ShopOrder ShopOrder);
	
	ShopOrder querybyid(ShopOrder ShopOrder);
	
	List<ShopOrder> querybyopenid(ShopOrder ShopOrder);
	
	List<ShopOrder> querybypos(ShopOrder ShopOrder);
	
	List<ShopOrder> queryall(ShopOrder ShopOrder);
	
	List<ShopOrder> queryallname(ShopOrder ShopOrder);
			
}