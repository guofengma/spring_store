package com.lxg.springboot.model;

import java.io.Serializable;

public abstract class BasicObject implements Serializable {

	/**
	 * author xuhuadong
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String storeid	;

	public String getStoreid() {
		return storeid;
	}

	public void setStoreid(String storeid) {
		this.storeid = storeid;
	}

	
	
}