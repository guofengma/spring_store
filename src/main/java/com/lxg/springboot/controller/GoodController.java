package com.lxg.springboot.controller;

import com.lxg.springboot.mapper.GoodMapper;
import com.lxg.springboot.model.Cart;
import com.lxg.springboot.model.Good;
import com.lxg.springboot.model.GoodSort;
import com.lxg.springboot.util.CharacterUtil;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

/**
 * Created by xuhuadong
 */
@RestController
@RequestMapping("CVS/good/")
public class GoodController extends BaseController {
	
	@Resource
    private GoodMapper goodMapper;
	private CharacterUtil characterParser;  
    
    @RequestMapping("queryall")
    public List<Good> query(Good good) {	
    	return goodMapper.queryall(good);  	
    } 
    
    @RequestMapping("queryallsort")
    public List<GoodSort> queryall(Good good) {	
    	List<Good> temp;
    	characterParser = CharacterUtil.getInstance(); 
    	temp = goodMapper.queryall(good);
    	List<GoodSort> goodsort = new ArrayList<GoodSort>();
    	for(char a='a';a<='z';a++){
    		GoodSort tempsort = new GoodSort();
    		tempsort.setLetter(a);
    		List<Good> tempgood =  new ArrayList<Good>();
    		for(int i=0;i<temp.size();i++){
    		String pinyin = characterParser.getSelling(temp.get(i).getName());
    		char first = pinyin.charAt(0);
    		if(first == a){
    			tempgood.add(temp.get(i));
    		}
    		}
    		tempsort.setGood(tempgood);
    		goodsort.add(tempsort);
    	}
    	return goodsort;
    } 
    
    @RequestMapping("query")
    public Good queryReferee(Good good) {
      		
    	Good returngood = new Good();
    	returngood=goodMapper.query(good);
    	return returngood;  	
    }  

    @RequestMapping("querybyCode")
    public Good querybyCode(Good good) {
      		
    	Good returngood = new Good();
    	returngood=goodMapper.querybyCode(good);
    	return returngood;  	
    }  

       
}
