package club.java.we.crawler.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义请求头数据
 * @author riverzu
 *
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Header {

	String accept() default "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
	
	String acceptEncoding();
	
	String acceptLanguage() default "zh-CN,zh;q=0.8,en;q=0.6";
	
	String connection();
	
	String upgradeInsecureRequests();
}
