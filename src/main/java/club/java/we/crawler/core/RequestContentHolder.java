package club.java.we.crawler.core;

/**
 * 请求参数的上下文
 * @author riverzu
 *
 */
public class RequestContentHolder {
	
	private static final ThreadLocal<CrawlerRequest> crawlerRequestHolder = new ThreadLocal<CrawlerRequest>();
	
	
	public static void setCrawlerRequest(CrawlerRequest request){
		crawlerRequestHolder.set(request);
	}
	
	public static CrawlerRequest getCrawlerRequest(){
		return crawlerRequestHolder.get();
	}

}
