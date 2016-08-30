package club.java.we.crawler.support;

import java.util.List;

/**
 * 支持ip代理池
 * @author riverzu
 *
 */
public interface ProxyPool {

	public String get(String crawlerName);
	
	public void addAll(String crawlerName,List<String> proxyUrls);
	
	public void add(String crawlerName,String proxyUrl);
	
	public int len(String crawlerName);
	
	public void remove(String crawlerName,String proxyUrl);
	
}
