package com.lxg.springboot.model;

import java.io.Serializable;

public class Shop implements Serializable {

	/**
	 * author xuhuadong
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String StoreId;

	private String address;

	private String StoreName;
	
	private double lng;
	
	private double lat;
	
	private String deal;
	
	private String dealname;
	
	private String field;
	
	private String fieldname;
	
	private String supply;
	
	private String supplyname;

	public String getStoreId() {
		return StoreId;
	}

	public void setStoreId(String storeId) {
		StoreId = storeId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStoreName() {
		return StoreName;
	}

	public void setStoreName(String storeName) {
		StoreName = storeName;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public String getDeal() {
		return deal;
	}

	public void setDeal(String deal) {
		this.deal = deal;
	}

	public String getDealname() {
		return dealname;
	}

	public void setDealname(String dealname) {
		this.dealname = dealname;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getFieldname() {
		return fieldname;
	}

	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	public String getSupply() {
		return supply;
	}

	public void setSupply(String supply) {
		this.supply = supply;
	}

	public String getSupplyname() {
		return supplyname;
	}

	public void setSupplyname(String supplyname) {
		this.supplyname = supplyname;
	}

}