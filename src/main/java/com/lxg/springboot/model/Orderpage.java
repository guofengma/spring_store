package com.lxg.springboot.model;

import java.io.Serializable;
import java.util.List;

public class Orderpage implements Serializable {

	/**
	 * author zhenghong@xrfinance.com
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int totalpage;

	private List<Order> order;

	public int getTotalpage() {
		return totalpage;
	}

	public void setTotalpage(int totalpage) {
		this.totalpage = totalpage;
	}

	public List<Order> getOrder() {
		return order;
	}

	public void setOrder(List<Order> order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "Diagnosispage [totalpage=" + totalpage + ", order=" + order + "]";
	}	
}