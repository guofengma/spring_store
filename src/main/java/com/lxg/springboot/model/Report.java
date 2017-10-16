package com.lxg.springboot.model;

import java.io.Serializable;

public class Report extends BasicObject {

	/**
	 * author zhenghong@xrfinance.com
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String report;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}
	
}