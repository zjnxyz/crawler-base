package club.java.we.crawler.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Crawler {

	 /**
     * 指定crawler是否启用cookie
     */
    boolean useCookie() default false;

    /**
     * 抓取请求间隔延时，单位为秒
     */
    int delay() default 0;
    
    /**
     * 是否启用系统级去重机制，默认启用
     */
    boolean useUnrepeated() default true;


    /**
     * 支持自定义超时间，单位毫秒，默认15000ms
     */
    int httpTimeOut() default 15000;
    
    
}
