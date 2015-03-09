
/*
 * Copyright (C) 2010 Sergey Panasenko
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 */

package org.oranj.utils;

public class StringUtils {

	public static boolean stringToBoolean(String value) {
		if (value==null) return false;
		if (value.equalsIgnoreCase("true")) return true;
		return false;
	}
	
	public static String[] split(String s, String delimeter) {
		if (isEmpty(s) || isEmpty(delimeter)) return null;
		String[] result = s.split(delimeter);
		for (String string : result) 
			string = string.trim();
		return result;
	}	
	
	public static String[] splitByDot(String s) {
		return split(s, ".");
	}
	
	//TODO: apache commons StringUtils!
	public static boolean isEmpty(String s) {
		return (s==null || s.length()==0);
	}
	
	public static String concatStrings(String s1, String s2, String separator) {
		String result="";
		if (s1!=null && s1.length()>0) {
			result = s1;
			if (s2!=null && s2.length()>0)
				result += separator + s2;								
		}			
		else
			result = s2;
		return result;
	}

	public static String dotStrings(String s1, String s2) {
		return concatStrings(s1,s2,".");
	}
	
	public static String dotStrings(String s1, String s2, String s3) {
		return dotStrings(dotStrings(s1,s2),s3);
	}

}
