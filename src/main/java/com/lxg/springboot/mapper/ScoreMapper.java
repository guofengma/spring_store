package com.lxg.springboot.mapper;

import com.lxg.springboot.model.Score;

public interface ScoreMapper {

	int save(Score score);
	
	int update(Score score);
	
	int query(String id);

	int querybytype(Score score);
	
	int count(Score score);
	
}