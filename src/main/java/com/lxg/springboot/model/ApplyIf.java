package com.lxg.springboot.model;

import java.io.Serializable;

public class ApplyIf {

	/**
	 * author zhenghong@xrfinance.com
	 * 
	 */
	private static final long serialVersionUID = 1L;

	boolean field;
	
	boolean deal;
	
	boolean supply;
	
	public void setField(boolean field) {
		this.field = field;
	}

	public boolean getField() {
		return field;
	}
	
	public void setDeal(boolean deal) {
		this.deal = deal;
	}

	public boolean getDeal() {
		return deal;
	}
	
	public void setSupply(boolean supply) {
		this.supply = supply;
	}

	public boolean getSupply() {
		return supply;
	}
}