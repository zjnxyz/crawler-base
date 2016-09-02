package club.java.we.crawler.support;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.RandomUtils;

import com.google.common.collect.Maps;

/**
 * 默认代理池实现
 * @author riverzu
 *
 */
public class DefaultProxyPool extends AbstractProxyPool{
	
	/** 有效的代理池*/
	private Map<String, CopyOnWriteArrayList<String>> effectivePoolMap = Maps.newConcurrentMap();
	
	/** 无效代理池 */
	private Map<String, CopyOnWriteArrayList<String>> invalidPoolMap = Maps.newConcurrentMap();
	
	/** 正在被使用的代理 */
	private Map<String, CopyOnWriteArrayList<String>> useProxyMap = Maps.newConcurrentMap();

	@Override
	public String get(String crawlerName) {
		CopyOnWriteArrayList<String> proxyPool = getProxyPool(crawlerName);
		int size = proxyPool.size();
		if(size == 0){
			return null;
		}
		return getValidProxy(crawlerName,proxyPool,size);
	}
	
	private String getValidProxy(String crawlerName,CopyOnWriteArrayList<String> proxyPool,int size){
		int i = 0;
		int index = RandomUtils.nextInt(0, size);
		CopyOnWriteArrayList<String> useProxies = getUseProxies(crawlerName);
		String proxy=null;
		while(i<size){
			String proxyUrl = proxyPool.get(index%size);
			if(!useProxies.contains(proxyUrl)){
				proxy = proxyUrl;
				useProxies.add(proxyUrl);
				break;
			}
			i++;
			//移动到下一个位置
			index++;
		}
		return proxy;
	}
	

	@Override
	public void addAll(String crawlerName, List<String> proxyUrls) {
		CopyOnWriteArrayList<String> proxyPool = getProxyPool(crawlerName);
		for(String proxyUrl:proxyUrls){
			proxyPool.add(proxyUrl);
		}
	}

	@Override
	public void add(String crawlerName, String proxyUrl) {
		 getProxyPool(crawlerName).add(proxyUrl);
	}

	@Override
	public int len(String crawlerName) {
		return getProxyPool(crawlerName).size();
	}

	@Override
	public void remove(String crawlerName, String proxyUrl) {
		getProxyPool(crawlerName).remove(proxyUrl);
		getUseProxies(crawlerName).remove(proxyUrl);
		//加入到失效的代理列表中
		getInvalidPoolMap(crawlerName).add(proxyUrl);
	}
	
	/**
	 * 获取代理池
	 * @param crawlerName
	 * @return
	 */
	private CopyOnWriteArrayList<String> getProxyPool(String crawlerName){
		CopyOnWriteArrayList<String> proxyPool = effectivePoolMap.get(crawlerName);
		if(proxyPool == null){
			proxyPool = new CopyOnWriteArrayList<String>();
			effectivePoolMap.put(crawlerName, proxyPool);
		}
		return proxyPool;
	}
	
	/**
	 * 正在被使用的代理
	 * @param crawlerName
	 * @return
	 */
	private CopyOnWriteArrayList<String> getUseProxies(String crawlerName){
		CopyOnWriteArrayList<String> useProxies = useProxyMap.get(crawlerName);
		if(useProxies == null){
			useProxies = new CopyOnWriteArrayList<String>();
			useProxyMap.put(crawlerName, useProxies);
		}
		return useProxies;
	}
	
	/**
	 * 失效的代理
	 * @param crawlerName
	 * @return
	 */
	private CopyOnWriteArrayList<String> getInvalidPoolMap(String crawlerName){
		CopyOnWriteArrayList<String> invalidProxies = invalidPoolMap.get(crawlerName);
		if(invalidProxies == null){
			invalidProxies = new CopyOnWriteArrayList<String>();
			invalidPoolMap.put(crawlerName, invalidProxies);
		}
		return invalidProxies;
	}

}
