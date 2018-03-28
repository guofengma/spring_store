package com.lxg.springboot.mapper;

import java.util.List;

import com.lxg.springboot.model.Finance;
import com.lxg.springboot.model.Score;
import com.lxg.springboot.model.Union;
import com.lxg.springboot.model.User;
import com.lxg.springboot.model.Card;

public interface UserMapper {

	int save(User user);
	
	int update(User user);
	
	int deleteauth(User user);

	User query(User user);
	
	User querybyno(User user);
	
	int saveboss(User user);
	
	int updateboss(User user);
	
	int updatebossbyphone(User user);

	User queryboss(User user);
	
	User querybossrole(User user);
	
	User querybossbyid(User user);
	
	List<User> querybynoboss(User user);
	
	int count(User user);
	
	int countboss(User user);
	
	int countbosss(User user);
	
	int countbossp(User user);
	
	int countbossp1(User user);
	
	int bosspay();
	
	List<User> queryallboss();
	
	void saveunion(Union union);
	
	void saveunionstate(Union union);
	
	void updateunionstate(Union union);
	
	int getunionstate(Union union);
	
	String getunionid(String openid);
	
	void savefinance(Finance user);
	
	int deletefinance(Finance user);
	
	int queryfinancecount(Finance user);
	
	Finance queryfinancebyid(Finance user);
	
	void updatefinance(Finance user);
	
	int deletecount(Finance user);
	
	int storecount(Finance user);
	
	String lasttime();
	
	void updatelasttime(String lasttime);
	
	int querymax(Card card);
	
	int savecard(Card card);
	
	int updatecard(Card card);
}