package com.lxg.springboot.model;

import java.io.Serializable;


public class Card extends BasicObject {

	/**
	 * author xuhuadong
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String cardNo;

	private String openid;
	
	private int money;
	
	private String time;
	
	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}	
	
}