package com.lxg.springboot.model;

import java.io.Serializable;

public class Apply extends BasicObject {

	/**
	 * author zhenghong@xrfinance.com
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;

	private String field;

	private String deal;

	private String supply;
	
	private int fieldstate;
	
	private int dealstate;
	
	private int supplystate;
	
	private int page;
	
	private int pagenum;

	private String address;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getSupply() {
		return supply;
	}

	public void setSupply(String supply) {
		this.supply = supply;
	}

	public String getDeal() {
		return deal;
	}

	public void setDeal(String deal) {
		this.deal = deal;
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

	public int getFieldstate() {
		return fieldstate;
	}

	public void setFieldstate(int fieldstate) {
		this.fieldstate = fieldstate;
	}

	public int getDealstate() {
		return dealstate;
	}

	public void setDealstate(int dealstate) {
		this.dealstate = dealstate;
	}

	public int getSupplystate() {
		return supplystate;
	}

	public void setSupplystate(int supplystate) {
		this.supplystate = supplystate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
}