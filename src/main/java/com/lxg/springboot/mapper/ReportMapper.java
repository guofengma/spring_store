package com.lxg.springboot.mapper;

import com.lxg.springboot.model.Score;

import java.util.List;

import com.lxg.springboot.model.Report;	
import com.lxg.springboot.model.Good;;

public interface ReportMapper {

	void save(Report report);
	
	Report query(Report report);
	
}