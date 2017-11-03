package com.lxg.springboot.model;

import java.io.Serializable;
import java.util.List;

public class Applypage implements Serializable {

	/**
	 * author zhenghong@xrfinance.com
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int totalpage;

	private List<Apply> apply;

	public int getTotalpage() {
		return totalpage;
	}

	public void setTotalpage(int totalpage) {
		this.totalpage = totalpage;
	}

	public List<Apply> getApply() {
		return apply;
	}

	public void setApply(List<Apply> apply) {
		this.apply = apply;
	}


}