package com.dosomething.util;

import org.apache.commons.lang.StringUtils;

public class StringUtil {
	private StringUtil() {
		throw new AssertionError();
	}

	public static String replaceChars(String str) {
		String ret = str;
		//ret = ret.replaceAll("'", "&#39;");
		ret = ret.replaceAll("&", "&amp;"); // must call it in first
		ret = ret.replaceAll("<", "&lt;");
		ret = ret.replaceAll(">", "&gt;");
		ret = ret.replaceAll("\"", "&quot;");
		return ret;
	}

	
	/**
	 * split '=' and "'" String , ex, one=1,sh=2
	 * @param input
	 * @return
	 */
	public static String[][] spiltKeyValueString(String input) {
		// 取得 exchange tax ratio data , 依照=及,字元分割出來的陣列裡面0,2,4,6是名字, 1,3,5,7是稅
		String[] temp = StringUtils.split(input, "=,");
		String[][] result = new String[temp.length/2][2];
		for(int i=0;i<temp.length;) {
			result[i/2] = new String[] { temp[i++], temp[i++] };
		}
		return result;
	}
	
	public static String filter(String input) {
		StringBuffer filtered = new StringBuffer(input.length());
		char c;
		for (int i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			if (c == '<') {
				filtered.append("&lt;");
			} else if (c == '>') {
				filtered.append("&gt;");
			} else if (c == '"') {
				filtered.append("&quot;");
			} else if (c == '“') {
				filtered.append("&quot;");
			} else if (c == '‘') {
				filtered.append("&#39;");
			} else if (c == '\'') {
				filtered.append("&#39;");
			} else if (c == '&') {
				filtered.append("&amp;");
			} else if (c == 10) {
				filtered.append("<br/>");
			} else {
				filtered.append(c);
			}
		}
		return (filtered.toString());
	}

	public static String notNull(String str) {
		return (str == null ? "" : str);
	}
	
	public static String notNull(Object obj) {
		return (obj == null ? "" : notNull(obj.toString()));
	}	

	/**
	 * 將陣列轉成字串. ex: 陣列:{"1","2","3"}, 間隔符號:逗號(,) => 1,2,3
	 * @param str 	陣列
	 * @param sign	間隔符號
	 * @return
	 */
	public static String toString(String[] str, String sign) {
		if (str == null || sign==null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for(String s : str){
			sb.append(sign).append(s);
		}
		sb.delete(0, 1);
		return sb.toString();
	}
	
	/**
	 * 比對是否在字串內. ex: 來源字串:1,2,3,45,26   要比對的字串:4, 間隔符號:逗號(,) => false
	 * @param sourceStr	來源字串
	 * @param matchStr	要比對的字串
	 * @param sign		間隔符號
	 * @return
	 */
	public static boolean matches(String sourceStr, String matchStr, String sign) {
		if(sourceStr==null || sourceStr.trim().length()==0){return false;}
		String pattern = "(\\A||.*["+sign+"])" + matchStr+"(\\z||["+sign+"].*)";
		return sourceStr.trim().matches(pattern);
	}	
	// public static String join(String[] s, String delimiter) {
	// StringBuffer buffer = new StringBuffer();
	// for(int i=0, len = s.length;i<s.length;i++) {
	// buffer.append(s[i]);
	// if(i+1 < len) {
	// buffer.append(delimiter);
	// }
	// }
	// return buffer.toString();
	// }

	public static String reverseChars(String s){
		s = s.replaceAll("&#39;", "\'");
		s = s.replaceAll("&quot;", "\""); // must call it in first
		s = s.replaceAll("&#96;", "`");
		s = s.replaceAll("&#92;", "\\");
		s = s.replaceAll("&#59;", ";");
		return s;
	}
	
	/**
	 * 縮短字串度為15+3個.以利顯示
	 * @param sourceStr	來源字串
	 * @return			縮短後的字串
	 */
	public static String shortString(String sourceStr) {
		return shortString(sourceStr, 15);
	}
	
	/**
	 * 縮短字串以利顯示
	 * @param sourceStr	來源字串
	 * @param length    縮短後的長度（不包含...）
	 * @return			縮短後的字串
	 */
	public static String shortString(String sourceStr, int absLength) {
		if(sourceStr != null && sourceStr.length() > 0) {
			StringBuffer sb = new StringBuffer();
			int l = 0;
			int i = 0;
			int length = sourceStr.length();
			for (i = 0; i < length; i++) {
				l += countLength(sourceStr.charAt(i));
				if (l > absLength) {
					break;
				}
				sb.append(sourceStr.charAt(i));
			}
			if (i != length) {
				sb.append("...");
			}
			return sb.toString();
		}
		return sourceStr;
	}

	private static int countLength(char c) {
		// 單字元
		if ((int) c < 128) {
			return 1; // or < 256
		}
		// 中文字, 雙字元
		return 2;
	}
	
	public static int countByteArrayLengthOfString(CharSequence sequence) {
		final int len = sequence.length();
		int count = len;
		for (int i = 0; i < len; i++) {
			char ch = sequence.charAt(i);
			if (ch <= 0x7F) {
				// count++;
			} else if (ch <= 0x7FF) {
				count += 1;
			} else if (ch >= 0xD800 && ch <= 0xDBFF) {
				count += 2;
				++i;
			} else {
				count += 2;
			}
		}
		return count;
	}
	
	public static boolean isNotEmpty(String str) {
		if(str != null && str.trim().length() > 0){
			return true;
		}
		return false;	
	}
	
	public static String upperCaseFirst(String s) {
		char[] charArray = s.toCharArray(); 
		charArray[0] = Character.toUpperCase(charArray[0]); 
		return String.valueOf(charArray);
	}
	
	/**
	 * fast than data.split(",")
	 * 
	 * @param data
	 * @param qoute
	 * @return
	 */
	public static String[] split(String data, String qoute) {
		int[] positions = new int[(data.length() + 1) * 2];
		int pos = 0, temp = 0;
		int i = 0;
		int len = data.length();

		while (true) {
			temp = data.indexOf(qoute, pos);
			positions[i++] = pos;
			if (temp < 0) {
				// 表示末端是逗號
				if (pos == len) {
					// clear and back
					positions[--i] = -1;
					// check special case
					while (i > 0) {
						if (positions[i - 1] == positions[i - 2]) {
							positions[--i] = -1;
							positions[--i] = -1;
						} else {
							break;
						}
					}
				} else {
					positions[i++] = len;
				}
				break;
			} else {
				if (pos == temp && len == 1) {
					positions[--i] = -1;
				} else {
					positions[i++] = temp;
				}
			}

			pos = temp + 1;
		}

		if (i < positions.length) {
			// set -1 mean end
			positions[i] = -1;
		}

		String[] result = new String[i / 2];
		i = 0;
		for (int j = 0; j < positions.length;) {
			int start = positions[j++];
			if (start == -1) {
				break;
			}
			int end = positions[j++];
			if (end == -1) {
				result[i++] = data.substring(start);
				break;
			}
			result[i++] = data.substring(start, end);
		}
		return result;
	}
}