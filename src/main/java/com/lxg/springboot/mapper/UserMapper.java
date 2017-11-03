package com.lxg.springboot.mapper;

import java.util.List;

import com.lxg.springboot.model.Score;
import com.lxg.springboot.model.User;

public interface UserMapper {

	int save(User user);
	
	int update(User user);

	User query(User user);
	
	User querybyno(User user);
	
	int saveboss(User user);
	
	int updateboss(User user);

	User queryboss(User user);
	
	User querybossbyid(User user);
	
	List<User> querybynoboss(User user);
	
	int count(User user);
	
	int countboss(User user);
	
	int bosspay();
	
	List<User> queryallboss();
	
}