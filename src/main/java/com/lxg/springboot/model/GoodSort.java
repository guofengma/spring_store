package com.lxg.springboot.model;

import java.io.Serializable;
import java.util.List;

public class GoodSort implements Serializable {

	/**
	 * author zhenghong@xrfinance.com
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private char letter;

	private List<Good> good;

	public char getLetter() {
		return letter;
	}

	public void setLetter(char letter) {
		this.letter = letter;
	}

	public List<Good> getGood() {
		return good;
	}

	public void setGood(List<Good> good) {
		this.good = good;
	}

}