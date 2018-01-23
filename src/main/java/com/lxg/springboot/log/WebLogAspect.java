package com.lxg.springboot.log;
import java.util.Arrays;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
 
/**
 * 实现Web层的日志切面
 * @author Angel(QQ:412887952)
 * @version v.0.1
 */
@Aspect
@Component
@Order(-5)
public class WebLogAspect {
    private Logger logger =  LoggerFactory.getLogger(this.getClass());
   
    /**
     * 定义一个切入点.
     * 解释下：
     *
     * ~ 第一个 * 代表任意修饰符及任意返回值.
     * ~ 第二个 * 任意包名
     * ~ 第三个 * 代表任意方法.
     * ~ 第四个 * 定义在web包或者子包
     * ~ 第五个 * 任意方法
     * ~ .. 匹配任意数量的参数.
     */
     @Pointcut("execution(public * com.lxg.springboot.controller..*.*(..))")
     public void webLog(){}
     
     @Before("webLog()")
     public void doBefore(JoinPoint joinPoint){
    	try{      
       // 接收到请求，记录请求内容
        logger.info("WebLogAspect.doBefore()");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
         HttpServletRequest request = attributes.getRequest();
        
        
      // 记录下请求内容
        logger.warn("URL : " + request.getRequestURL().toString()+"\n"+"INPUT : " + JSON.toJSONString(joinPoint.getArgs()));
 //       logger.warn("HTTP_METHOD : " + request.getMethod());
 //       logger.warn("IP : " + request.getRemoteAddr());
 //      logger.warn("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
 //       logger.warn("INPUT : " + Arrays.toString(joinPoint.getArgs()));
       //获取所有参数方法一：
        Enumeration<String> enu=request.getParameterNames(); 
        while(enu.hasMoreElements()){ 
            String paraName=(String)enu.nextElement(); 
            logger.info(paraName+": "+request.getParameter(paraName)); 
        } 
    	}
    	catch(Exception e){
    		logger.info("log error");	
    	}
     }
     
     @AfterReturning(returning = "rvt", pointcut = "webLog()")  
     public void  doAfterReturning(JoinPoint joinPoint,Object rvt){
    	try{
       // 处理完请求，返回内容
        logger.info("WebLogAspect.doAfterReturning()");
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
             
     // 记录下请求内容
        logger.warn("URL : " + request.getRequestURL().toString()+"\n"+"RETURN : " +  JSON.toJSONString(rvt));
 //       logger.warn("RETURN : " +  JSON.toJSONString(rvt));
        
    	}catch(Exception e){
    		logger.info("log error");	
    	}
     }
     
}