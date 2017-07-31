package com.lxg.springboot.model;

import java.io.Serializable;

public class Score implements Serializable {

	/**
	 * author zhenghong@xrfinance.com
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String openid;

	// 0 分享 1 录入病例 2 完善信息 9 总分
	private String type;

	private int score;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "Score [openid=" + openid + ", type=" + type + ", score=" + score + "]";
	}

}