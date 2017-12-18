package com.lxg.springboot.model;

import java.io.Serializable;
import java.util.List;

public class FinancePage implements Serializable {

	/**
	 * author zhenghong@xrfinance.com
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int totalpage;

	private List<Finance> finance;

	public int getTotalpage() {
		return totalpage;
	}

	public void setTotalpage(int totalpage) {
		this.totalpage = totalpage;
	}

	public List<Finance> getFinance() {
		return finance;
	}

	public void setFinance(List<Finance> finance) {
		this.finance = finance;
	}

}