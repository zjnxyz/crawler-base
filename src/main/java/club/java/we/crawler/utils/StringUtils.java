package club.java.we.crawler.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {
    
    /**
     * 判断是否为boolean值.
     * 
     * @param str 待判断的字符串
     * @return 是否为boolean值
     */
    public static boolean isBooleanValue(final String str) {
        return Boolean.TRUE.toString().equalsIgnoreCase(str) || Boolean.FALSE.toString().equalsIgnoreCase(str);
    }
    
    /**
     * 判断是否为int值.
     * 
     * @param str 待判断的字符串
     * @return 是否为int值
     */
    public static boolean isIntValue(final String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (final NumberFormatException ex) {
            return false;
        }
    }
    
    /**
     * 判断是否为long值.
     *
     * @param str 待判断的字符串
     * @return 是否为long值
     */
    public static boolean isLongValue(final String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (final NumberFormatException ex) {
            return false;
        }
    }
}

