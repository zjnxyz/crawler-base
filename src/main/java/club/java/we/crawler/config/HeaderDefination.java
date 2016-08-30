package club.java.we.crawler.config;

import java.util.Map;

import com.google.common.collect.Maps;

import club.java.we.crawler.http.okhttp.CookiesManager;

/**
 * 请求头数据定义
 * @author riverzu
 *
 */
public class HeaderDefination {
	
	private final Map<String,String> headers = Maps.newHashMap();
	
	public final static String ACCEPT= "Accept";
	
	public final static String ACCEPT_ENCODING="Accept-Encoding";
	
	public final static String ACCEPT_LANGUAGE="Accept-Language";
	
	public final static String CONNECTION="connection";
	
	public final static String UPGRADE_INSECURE_REQUESTS="Upgrade-Insecure-Requests";
	
	public final static String USER_AGENT = "User-Agent";
	
	public final static String REFERER = "Referer";
	
	public final static String DEFAULT_ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
	
	public final static String DEFAULT_ACCEPT_LANGUAGE = "zh-CN,zh;q=0.8,en;q=0.6";
	
	
	private String accept;
	
	private String acceptEncoding;
	
	private String acceptLanguage;
	
	private String connection;
	
	private String upgradeInsecureRequests;
	
	private String userAgent;
	
	private CookiesManager cookiesManager = new CookiesManager();

	public String getAccept() {
		return accept;
	}

	public void setAccept(String accept) {
		this.accept = accept;
	}

	public String getAcceptEncoding() {
		return acceptEncoding;
	}

	public void setAcceptEncoding(String acceptEncoding) {
		this.acceptEncoding = acceptEncoding;
	}

	public String getAcceptLanguage() {
		return acceptLanguage;
	}

	public void setAcceptLanguage(String acceptLanguage) {
		this.acceptLanguage = acceptLanguage;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getUpgradeInsecureRequests() {
		return upgradeInsecureRequests;
	}

	public void setUpgradeInsecureRequests(String upgradeInsecureRequests) {
		this.upgradeInsecureRequests = upgradeInsecureRequests;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public CookiesManager getCookiesManager() {
		return cookiesManager;
	}

	public void setCookiesManager(CookiesManager cookiesManager) {
		this.cookiesManager = cookiesManager;
	}

	public Map<String, String> getHeaders() {
		headers.put(ACCEPT, getAccept() == null ? DEFAULT_ACCEPT : getAccept());
//		headers.put(ACCEPT_ENCODING, getAcceptEncoding() );
		headers.put(ACCEPT_LANGUAGE, getAcceptLanguage() == null ? DEFAULT_ACCEPT_LANGUAGE : getAcceptLanguage());
		headers.put(CONNECTION, getConnection() );
		headers.put(UPGRADE_INSECURE_REQUESTS,getUpgradeInsecureRequests());
		return headers;
	}
	
 
}
