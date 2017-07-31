package com.lxg.springboot.mapper;

import java.util.List;

import com.lxg.springboot.model.Cart;

public interface CartMapper {

	int save(Cart cart);
	
	int edit(Cart cart);
	
	List<Cart> querybypage(Cart cart);
	
	int delete(Cart cart);
	
	int deleteall(Cart cart);
		
}