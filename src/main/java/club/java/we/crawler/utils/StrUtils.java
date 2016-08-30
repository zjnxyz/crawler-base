package club.java.we.crawler.utils;

import java.util.List;

import com.google.common.base.Joiner;

public class StrUtils {
	
	/**
	 * 字符拼接，连接符为空字符
	 * @param list
	 * @return
	 */
	public static String join(List<?> list){
		return Joiner.on("").skipNulls().join(list);
	}
	

}
