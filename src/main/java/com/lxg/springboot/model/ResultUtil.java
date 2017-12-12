package com.lxg.springboot.model;

public class ResultUtil {  
    /** 
     * 请求成功返回 
     * @param object 
     * @return 
     */  
    public static Msg success(Object object){  
        Msg msg=new Msg();  
        msg.setEc("000000");  
        msg.setMsg("请求成功");  
        msg.setData(object);  
        return msg;  
    }
    
    public static Msg fail(String msg1){  
        Msg msg=new Msg();  
        msg.setEc("999999");  
        msg.setMsg(msg1);   
        return msg;  
    }  
    public static Msg success(){  
        return success(null);  
    }  
  
    public static Msg error(String code,String resultmsg){  
        Msg msg=new Msg();  
        msg.setEc(code);  
        msg.setMsg(resultmsg);  
        return msg;  
    }  
  
  
}  