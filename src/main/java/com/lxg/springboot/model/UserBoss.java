package com.lxg.springboot.model;

import java.io.Serializable;

public class UserBoss extends BasicObject {

	/**
	 * author zhenghong@xrfinance.com
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String openid;

	private String nickname;

	private String sex;

	private String age;

	private String phoneno;
	
	private String address;
	
	private String role;
	
	private String password;
	
	private String startDate;//时间
	
	private String endDate;//时间

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getPhoneno() {
		return phoneno;
	}

	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "User [openid=" + openid + ", nickname=" + nickname + ", sex=" + sex + ", age=" + age + ", phoneno="
				+ phoneno + ", address=" + address + "]";
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}