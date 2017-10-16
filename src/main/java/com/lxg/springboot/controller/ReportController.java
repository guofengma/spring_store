package com.lxg.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import com.lxg.springboot.mapper.ReportMapper;
import com.lxg.springboot.model.Result;
import com.lxg.springboot.model.Report;
import com.lxg.springboot.model.Good;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

/**
 * Created by zhenghong
 * on 2017/4/25.
 */
@RestController
@RequestMapping("CVS/report/")
public class ReportController extends BaseController {
	
	@Resource
    private ReportMapper reportMapper;

    @RequestMapping("save")
    public Result save(Report report) {
    
    	reportMapper.save(report);
    	return new Result();
    }    
    
    
    @RequestMapping("query")
    public Report query(Report report) {	
    	return reportMapper.query(report);  	
    } 
    
}
