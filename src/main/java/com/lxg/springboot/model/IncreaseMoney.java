package com.lxg.springboot.model;

import java.io.Serializable;

public class IncreaseMoney extends BasicObject {

	/**
	 * author zhenghong@xrfinance.com
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String openid;

	private String startDate1;
	
	private String startDate2;
	
	private String endDate1;
	
	private String endDate2;
	
	private String finalincrease;
	
	private String storename;
	
	private int score;
	
	private int scorebonus;
	
	private int page;
	
	private int pagenum;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getStartDate1() {
		return startDate1;
	}

	public void setStartDate1(String startDate1) {
		this.startDate1 = startDate1;
	}

	public String getStartDate2() {
		return startDate2;
	}

	public void setStartDate2(String startDate2) {
		this.startDate2 = startDate2;
	}

	public String getEndDate1() {
		return endDate1;
	}

	public void setEndDate1(String endDate1) {
		this.endDate1 = endDate1;
	}

	public String getEndDate2() {
		return endDate2;
	}

	public void setEndDate2(String endDate2) {
		this.endDate2 = endDate2;
	}

	public String getFinalincrease() {
		return finalincrease;
	}

	public void setFinalincrease(String finalincrease) {
		this.finalincrease = finalincrease;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPagenum() {
		return pagenum;
	}

	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
	}

	public String getStorename() {
		return storename;
	}

	public void setStorename(String storename) {
		this.storename = storename;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getScorebonus() {
		return scorebonus;
	}

	public void setScorebonus(int scorebonus) {
		this.scorebonus = scorebonus;
	}

}