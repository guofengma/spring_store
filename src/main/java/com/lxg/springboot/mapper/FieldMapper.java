package com.lxg.springboot.mapper;

import java.util.List;

import com.lxg.springboot.model.Field;

public interface FieldMapper {

	void savefield(Field field);
	
	void updatefield(Field field);
	
	Field queryfield(Field field);
	
	void savedeal(Field field);
	
	void updatedeal(Field field);
	
	Field querydeal(Field field);
	
	void savesupply(Field field);
	
	void updatesupply(Field field);
	
	Field querysupply(Field field);
	
	
}