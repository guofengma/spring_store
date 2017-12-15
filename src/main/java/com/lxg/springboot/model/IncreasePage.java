package com.lxg.springboot.model;

import java.io.Serializable;
import java.util.List;

public class IncreasePage implements Serializable {

	/**
	 * author zhenghong@xrfinance.com
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int totalpage;

	private List<IncreaseMoney> increasMoney;


	public List<IncreaseMoney> getIncreasMoney() {
		return increasMoney;
	}

	public void setIncreasMoney(List<IncreaseMoney> increasMoney) {
		this.increasMoney = increasMoney;
	}

	public int getTotalpage() {
		return totalpage;
	}

	public void setTotalpage(int totalpage) {
		this.totalpage = totalpage;
	}




}