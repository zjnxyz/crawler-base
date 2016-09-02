package club.java.we.crawler.core;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import club.java.we.crawler.config.HeaderDefination;
import club.java.we.crawler.http.HttpMethod;
import club.java.we.crawler.http.SeimiAgentContentType;

/**
 * 请求数据
 * @author riverzu
 *
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
public class CrawlerRequest {

	private String crawlerName;
	
	/**
	 * 需要请求的url
	 */
	@NonNull
	private String url;
	/**
	 * 要请求的方法类型 get,post,put...
	 */
	private HttpMethod httpMethod;
	/**
	 * 如果请求需要参数，那么将参数放在这里
	 */
	private Map<String, String> params;
	/**
	 * 这个主要用于存储向下级回调函数传递的一些自定义数据
	 */
	private Map<String, String> meta;
	/**
	 * 回调函数方法名
	 */
	@NonNull
	private String callBack;
	
	/**
	 * 最大可被重新请求次数
	 */
	private int maxReqCount = 3;

	/**
	 * 用来记录当前请求被执行过的次数
	 */
	private int currentReqCount = 0;

	/**
	 * 用来指定一个请求是否要经过去重机制
	 */
	private boolean skipDuplicateFilter = false;

	/**
	 * 针对该请求是否启用SeimiAgent
	 */
	private boolean useSeimiAgent = false;

	/**
	 * 定义SeimiAgent的渲染时间，单位毫秒
	 */
	private long seimiAgentRenderTime = 0;

	/**
	 * 用于支持在SeimiAgent上执行指定的js脚本
	 */
	private String seimiAgentScript;

	/**
	 * 指定提交到SeimiAgent的请求是否使用cookie
	 */
	private Boolean seimiAgentUseCookie;
	
	 /**
     * 告诉SeimiAgent将结果渲染成何种格式返回，默认HTML
     */
    private SeimiAgentContentType seimiAgentContentType = SeimiAgentContentType.HTML;
    
    /** 当前请求使用的队列*/
	private String queueName;
	
	/** 当前请求使用的headers */
	private HeaderDefination headers;
	
	public void incrReqCount(){
        this.currentReqCount +=1;
    }
	
	public static Builder build(String url, String callBack){
		return new Builder(url,callBack);
	}
	
	/**
	 * 请求构造器
	 * @author riverzu
	 *
	 */
	public static class Builder{
		
		private String crawlerName;
		/**
		 * 需要请求的url
		 */
		private String url;
		/**
		 * 要请求的方法类型 get,post,put...
		 */
		private HttpMethod httpMethod;
		/**
		 * 如果请求需要参数，那么将参数放在这里
		 */
		private Map<String, String> params;
		/**
		 * 这个主要用于存储向下级回调函数传递的一些自定义数据
		 */
		private Map<String, String> meta;
		/**
		 * 回调函数方法名
		 */
		private String callBack;
	
		/**
		 * 最大可被重新请求次数
		 */
		private int maxReqCount = 3;

		/**
		 * 用来记录当前请求被执行过的次数
		 */
		private int currentReqCount = 0;

		/**
		 * 用来指定一个请求是否要经过去重机制
		 */
		private boolean skipDuplicateFilter = false;

		/**
		 * 针对该请求是否启用SeimiAgent
		 */
		private boolean useSeimiAgent = false;

		/**
		 * 定义SeimiAgent的渲染时间，单位毫秒
		 */
		private long seimiAgentRenderTime = 0;

		/**
		 * 用于支持在SeimiAgent上执行指定的js脚本
		 */
		private String seimiAgentScript;

		/**
		 * 指定提交到SeimiAgent的请求是否使用cookie
		 */
		private Boolean seimiAgentUseCookie;
		
		/** 当前请求使用的队列*/
		private String queueName;
		
		/** 当前请求使用的headers */
		private HeaderDefination headers;
		
		 /**
	     * 告诉SeimiAgent将结果渲染成何种格式返回，默认HTML
	     */
	    private SeimiAgentContentType seimiAgentContentType = SeimiAgentContentType.HTML;
	    
	    public Builder(String url, String callBack){
	    	this.url=url;
	    	this.callBack=callBack;
	    }
	    
	    public Builder crawlerName(String crawlerName){
	    	this.crawlerName = crawlerName;
	    	return this;
	    }
	    
	    public Builder queueName(String queueName){
	    	this.queueName = queueName;
	    	return this;
	    }
	    
	    public Builder httpMethod(HttpMethod httpMethod){
	    	this.httpMethod = httpMethod;
	    	return this;
	    }
	    
	    public Builder params(Map<String, String> params){
	    	this.params = params;
	    	return this;
	    }
	    
	    public Builder meta(Map<String, String> meta){
	    	this.meta = meta;
	    	return this;
	    }
	  
	    
	    public Builder maxReqCount(int maxReqCount ){
	    	this.maxReqCount = maxReqCount;
	    	return this;
	    }
	    
	    public Builder currentReqCount(int currentReqCount){
	    	this.currentReqCount = currentReqCount;
	    	return this;
	    }
	    
	    public Builder skipDuplicateFilter(boolean skipDuplicateFilter){
	    	this.skipDuplicateFilter = skipDuplicateFilter;
	    	return this;
	    }
	    
	    public Builder useSeimiAgent(boolean useSeimiAgent){
	    	this.useSeimiAgent = useSeimiAgent;
	    	return this;
	    }
	    
	    public Builder httpMethod(long seimiAgentRenderTime){
	    	this.seimiAgentRenderTime = seimiAgentRenderTime;
	    	return this;
	    }
	    
	    public Builder seimiAgentScript( String seimiAgentScript){
	    	this.seimiAgentScript = seimiAgentScript;
	    	return this;
	    }
	    
	    public Builder seimiAgentUseCookie(Boolean seimiAgentUseCookie){
	    	this.seimiAgentUseCookie = seimiAgentUseCookie;
	    	return this;
	    }
	    
	    public Builder httpMethod(SeimiAgentContentType seimiAgentContentType){
	    	this.seimiAgentContentType = seimiAgentContentType;
	    	return this;
	    }
	    
	    
	    public Builder headers(HeaderDefination headers){
	    	this.headers = headers;
	    	return this;
	    }
	    
	    public CrawlerRequest build(){
	    	return new CrawlerRequest(crawlerName, url, httpMethod, params, meta,
	    			callBack, maxReqCount, currentReqCount, skipDuplicateFilter,
	    			useSeimiAgent, seimiAgentRenderTime, seimiAgentScript,
	    			seimiAgentUseCookie, seimiAgentContentType,queueName,headers);
	    }
	    
	    
	    
	    
		
	}

}
