package com.lxg.springboot.mapper;

import java.util.List;

import com.lxg.springboot.model.Order;
import com.lxg.springboot.model.WithDrawDay;

public interface WithDrawMapper {

	int save(Order order);
	
	int update(Order order);
	
	List<Order> query(String id);
	
	int savewith(WithDrawDay order);
	
	int updatewith(WithDrawDay order);
	
	List<WithDrawDay> queryerror(WithDrawDay order);
	
	int saveerror(WithDrawDay order);
	
	int updateerrorfee(WithDrawDay order);
	
	int updateerrorstate(WithDrawDay order);
	
	List<WithDrawDay> queryerrortime(WithDrawDay order);
	
	List<WithDrawDay> querywithman(WithDrawDay order);
	
	List<WithDrawDay> querywithstore(WithDrawDay order);
	
	int querywithmannum(WithDrawDay order);
	
	int querywithstorenum(WithDrawDay order);
	
	int querywithmansum(WithDrawDay order);
	
	List<WithDrawDay> querywithstoresum(WithDrawDay order);
	
	int querywithstoresumnum(WithDrawDay order);
}