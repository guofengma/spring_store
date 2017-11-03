package com.lxg.springboot.model;

public class Msg<T> {  
	  
    /*错误码*/  
    private String ec;  
  
    /*提示信息 */  
    private String em;  
  
    /*具体内容*/  
    private  T data;  
  
    public String getEc() {  
        return ec;  
    }  
  
    public void setEc(String ec) {  
        this.ec = ec;  
    }  
  
    public String getEm() {  
        return em;  
    }  
  
    public void setMsg(String em) {  
        this.em = em;  
    }  
  
    public T getData() {  
        return data;  
    }  
  
    public void setData(T data) {  
        this.data = data;  
    }  
} 