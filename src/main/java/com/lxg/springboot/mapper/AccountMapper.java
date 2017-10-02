package com.lxg.springboot.mapper;

import com.lxg.springboot.model.Score;

import java.util.List;

import com.lxg.springboot.model.Account;	
import com.lxg.springboot.model.Good;;

public interface AccountMapper {

	void save(Account account);
	
	List<Account> query(Account account);
	
}