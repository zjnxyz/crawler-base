package club.java.we.crawler.support;

import java.util.List;

import okhttp3.Cookie;

public interface CookieStore {
	
	final static String EXPIRES="expires";
	
	final static String DOMAIN="domain";
	
	final static String PATH="path";
	
	final static String SECURE="secure";
	
	final static String HTTPONLY="httponly";
	
	public void writeCookies(String host,List<Cookie> cookies);
	
	public List<Cookie> readCookies(String host);

}
