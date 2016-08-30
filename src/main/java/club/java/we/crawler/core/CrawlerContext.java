package club.java.we.crawler.core;

import java.util.Map;
import java.util.Properties;

import lombok.Getter;
import lombok.Setter;
import com.google.common.collect.Maps;

import club.java.we.crawler.config.CrawlerDefinition;
import club.java.we.crawler.config.CrawlerProperties;

/**
 * 爬虫需要信息的上下，需要初始化使用
 * @author riverzu
 *
 */
@Setter
@Getter
public class CrawlerContext {
	
	private Map<String, CrawlerDefinition> crawlerMap = Maps.newConcurrentMap();
	/**
	 * 使用的队列
	 */
	private CrawlerQueue queue;
	
	private ExecutorEngine executorEngine;
	

	private CrawlerContext(CrawlerQueue queue,Properties props) {
		if(props == null){
			props = new Properties();
		}
		if(queue == null){
			queue = new DefaultLocalQueue();
		}
		this.queue = queue;
		executorEngine = new ExecutorEngine(new CrawlerProperties(props));
	}
	
	
	public void addCrawler(CrawlerDefinition crawlerDefinition){
		crawlerMap.put(crawlerDefinition.getCrawlerName(), crawlerDefinition);
	}
	
	public CrawlerDefinition getCrawler(String clsName){
		return crawlerMap.get(clsName);
	}
	
	
	public static Builder build(){
		return new Builder();
	}
	


	public static class Builder{
		
		private CrawlerQueue queue;
		
		//熟悉
		private Properties props;
		
		public Builder queue( CrawlerQueue queue ){
			this.queue = queue;
			return this;
		}
		
		public Builder props(Properties props){
			this.props = props;
			return this;
		}
		
		public CrawlerContext build(){
			return new CrawlerContext(queue, props);
		}
	}
	
	
	
}
