package club.java.we.crawler.config;

import java.lang.reflect.Method;
import java.net.Proxy;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import com.google.common.collect.Maps;

import club.java.we.crawler.annotation.process.HeaderAnnotationProcess;
import club.java.we.crawler.core.AbstractBaseCrawler;
import club.java.we.crawler.core.CrawlerQueue;
import club.java.we.crawler.utils.ProxyResolveUtils;
import club.java.we.crawler.utils.ReflectionUtils;

/**
 * 爬虫需要的属性定义
 * 
 * @author riverzu
 *
 */
@Setter
@Getter
@Log4j2
public class CrawlerDefinition {

	private Map<String, Method> memberMethods;

	private String crawlerName;

	private boolean useCookie = false;

	private String currentUA;

	private boolean useUnrepeated = true;

	private int delay = 0;

	private int httpTimeOut;

	private Class<? extends AbstractBaseCrawler> crawlerClazz;

	private AbstractBaseCrawler crawlerInstance;

	private CrawlerQueue queueInstance;

	private Class<? extends CrawlerQueue> queueClass;
	
	private HeaderDefination headerDefination;

	// 代理
	private Proxy stdProxy;
	
	//代理地址对应的字符串类型
	private String proxyStr;
	
	/** */
	private String seimiAgentUrl;

	public static Builder build(String crawlerName) {
		return new Builder(crawlerName);
	}

	public CrawlerDefinition(String crawlerName, boolean useCookie,
			boolean useUnrepeated, int delay, int httpTimeOut,
			Class<? extends AbstractBaseCrawler> crawlerClazz,
			AbstractBaseCrawler crawlerInstance, CrawlerQueue queueInstance,
			Class<? extends CrawlerQueue> queueClass,String seimiAgentUrl,String proxyStr) {
		super();
		this.crawlerName = crawlerName;
		this.useCookie = useCookie;
		this.useUnrepeated = useUnrepeated;
		this.delay = delay;
		this.httpTimeOut = httpTimeOut;
		this.crawlerClazz = crawlerClazz;
		this.crawlerInstance = crawlerInstance;
		this.queueInstance = queueInstance;
		this.queueClass = queueClass;
		this.stdProxy = ProxyResolveUtils.resolveProxy(proxyStr);
		this.proxyStr = proxyStr;
		init();
	}

	private void init() {
		memberMethods = Maps.newConcurrentMap();
		ReflectionUtils.doWithMethods(crawlerClazz,
				new ReflectionUtils.MethodCallback() {
					@Override
					public void doWith(Method method)
							throws IllegalArgumentException,
							IllegalAccessException {
						memberMethods.put(method.getName(), method);
					}
				});

		this.currentUA = crawlerInstance.getUserAgent();
		
		this.headerDefination = HeaderAnnotationProcess.process(crawlerClazz);
		headerDefination.setUserAgent(currentUA);
		log.info("Crawler[{}] init complete.", crawlerName);

	}
	
	public void setProxyStr(String proxyStr){
		this.stdProxy = ProxyResolveUtils.resolveProxy(proxyStr);
		this.proxyStr = proxyStr;
	}
	


	public static class Builder {

		private String crawlerName;

		private boolean useCookie = false;

		private boolean useUnrepeated = true;

		private int delay = 0;

		private int httpTimeOut;

		private Class<? extends AbstractBaseCrawler> crawlerClazz;

		private AbstractBaseCrawler crawlerInstance;

		private CrawlerQueue queueInstance;

		private Class<? extends CrawlerQueue> queueClass;

		private String seimiAgentUrl;
		
		private String proxyStr;

		public Builder(String crawlerName) {
			this.crawlerName = crawlerName;
		}

		public Builder useCookie(boolean useCookie) {
			this.useCookie = useCookie;
			return this;
		}

		public Builder useUnrepeated(boolean useUnrepeated) {
			this.useUnrepeated = useUnrepeated;
			return this;
		}

		public Builder delay(int delay) {
			this.delay = delay;
			return this;
		}

		public Builder httpTimeOut(int httpTimeOut) {
			this.httpTimeOut = httpTimeOut;
			return this;
		}

		public Builder crawlerClazz(
				Class<? extends AbstractBaseCrawler> crawlerClazz) {
			this.crawlerClazz = crawlerClazz;
			return this;
		}

		public Builder crawlerInstance(AbstractBaseCrawler crawlerInstance) {
			this.crawlerInstance = crawlerInstance;
			return this;
		}

		public Builder queueInstance(CrawlerQueue queueInstance) {
			this.queueInstance = queueInstance;
			return this;
		}

		public Builder queueClass(Class<? extends CrawlerQueue> queueClass) {
			this.queueClass = queueClass;
			return this;
		}

		public Builder proxy(String proxy) {
			this.proxyStr = proxy;
			return this;
		}
		
		public Builder seimiAgentUrl(String seimiAgentUrl) {
			this.seimiAgentUrl = seimiAgentUrl;
			return this;
		}

		public CrawlerDefinition build() {
			return new CrawlerDefinition(crawlerName, useCookie, useUnrepeated,
					delay, httpTimeOut, crawlerClazz, crawlerInstance,
					queueInstance, queueClass,seimiAgentUrl,proxyStr);
		}

	}

}
