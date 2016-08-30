package club.java.we.crawler.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class StrFormatUtils {
	
	private static final Pattern charsetPattern = Pattern.compile("charset=([1-9a-zA-Z-]+)$");
    public static String info(String pattern,Object... params){
        for (Object p:params){
            pattern = StringUtils.replaceOnce(pattern, "{}", p.toString());
        }
        return pattern;
    }

    public static String getFirstEmStr(List<Object> list,String def){
    	if(list == null || list.size() == 0){
    		return def;
    	}
       
        return list.get(0).toString();
    }

    public static String parseCharset(String target){
        Matcher matcher = charsetPattern.matcher(target);
        if (matcher.find()){
            return matcher.group(1);
        }
        return "";
    }

}
