package com.lxg.springboot.model;

import java.io.Serializable;
import java.util.List;

public class WithdrawPage implements Serializable {

	/**
	 * author zhenghong@xrfinance.com
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int totalpage;

	private List<WithDrawDay> withDrawDay;

	public int getTotalpage() {
		return totalpage;
	}

	public void setTotalpage(int totalpage) {
		this.totalpage = totalpage;
	}

	public List<WithDrawDay> getWithDrawDay() {
		return withDrawDay;
	}

	public void setWithDrawDay(List<WithDrawDay> withDrawDay) {
		this.withDrawDay = withDrawDay;
	}


}