package com.lxg.springboot.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 签名
 * 
 */

public class Signature {

	/**
	 * 签名算法
	 * 
	 * @param o
	 *            要参与签名的数据对象
	 * @return 签名
	 * @throws IllegalAccessException
	 */
	public static String getSign(Object o) throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		Class<? extends Object> cls = o.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field f : fields) {
			f.setAccessible(true);
			if (f.get(o) != null && f.get(o) != "") {
				String name = f.getName();
				XStreamAlias anno = f.getAnnotation(XStreamAlias.class);
				if (anno != null)
					name = anno.value();
				list.add(name + "=" + f.get(o) + "&");
			}
		}
		int size = list.size();
		String[] arrayToSort = list.toArray(new String[size]);
		Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(arrayToSort[i]);
		}
		String result = new String(sb.toString().getBytes("UTF-8"));
		result += "key=79m1jyaofjonvahln1wnoq606rvbk2gi";
		System.out.println("签名数据：" + result);
		result = MD5.MD5Encode(result).toUpperCase();
		return result;
	}

	public static String getSign(Map<String, Object> map) throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != "") {
				list.add(entry.getKey() + "=" + entry.getValue() + "&");
			}
		}
		int size = list.size();
		String[] arrayToSort = list.toArray(new String[size]);
		Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(arrayToSort[i]);
		}
		String result = new String(sb.toString().getBytes("UTF-8"));
		result += "key=79m1jyaofjonvahln1wnoq606rvbk2gi";
		// Util.log("Sign Before MD5:" + result);
		result = MD5.MD5Encode(result).toUpperCase();
		// Util.log("Sign Result:" + result);
		return result;
	}

}
