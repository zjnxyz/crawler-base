package club.java.we.crawler.core;

import java.util.List;

import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import club.java.we.crawler.annotation.Crawler;
import club.java.we.crawler.annotation.Header;
import club.java.we.crawler.config.CrawlerDefinition;
import club.java.we.crawler.http.HttpMethod;
@Log4j2
public abstract class AbstractBaseCrawler implements BaseCrawler {

	protected String[] defUAs = new String[] {
			"Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.43 Safari/537.31",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.60 Safari/537.17",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1309.0 Safari/537.17",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.2; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0)",
			"Mozilla/5.0 (Windows; U; MSIE 7.0; Windows NT 6.0; en-US)",
			"Mozilla/5.0 (Windows; U; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)",
			"Mozilla/6.0 (Windows NT 6.2; WOW64; rv:16.0.1) Gecko/20121011 Firefox/16.0.1",
			"Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:15.0) Gecko/20100101 Firefox/15.0.1",
			"Mozilla/5.0 (Windows NT 6.2; WOW64; rv:15.0) Gecko/20120910144328 Firefox/15.0.2",
			"Mozilla/5.0 (Windows; U; Windows NT 6.1; rv:2.2) Gecko/20110201",
			"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9a3pre) Gecko/20070330",
			"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.13; ) Gecko/20101203",
			"Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14",
			"Opera/9.80 (X11; Linux x86_64; U; fr) Presto/2.9.168 Version/11.50",
			"Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; de) Presto/2.9.168 Version/11.52",
			"Mozilla/5.0 (Windows; U; Win 9x 4.90; SG; rv:1.9.2.4) Gecko/20101104 Netscape/9.1.0285",
			"Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:1.8.1.7pre) Gecko/20070815 Firefox/2.0.0.6 Navigator/9.0b3",
			"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.12) Gecko/20080219 Firefox/2.0.0.12 Navigator/9.0.0.6" };

	protected CrawlerQueue queue;

	protected String crawlerName;

	// 是否使用cookie
	private boolean useCookie = false;

	private boolean useUnrepeated = true;

	// 间隔时间，单位是毫秒
	private int delay = 0;

	// 请求过期时间，单位是毫秒
	private int httpTimeOut = 15000;

	private Class<? extends AbstractBaseCrawler> clazz;

	// 起始调用方法
	public final static String METHOD_START = "start";

	// 爬虫上下文
	private CrawlerContext crawlerContext;
	
	//任务接收器
	private Acceptor acceptor;
	
	//爬虫是否在运行中
	protected volatile boolean running = false;

	public AbstractBaseCrawler(CrawlerContext crawlerContext,
			Class<? extends AbstractBaseCrawler> clazz) {
		this.crawlerContext = crawlerContext;
		queue = crawlerContext.getQueue();
		this.clazz = clazz;
		this.crawlerName = this.clazz.getName();
		crawlerAnnotationProcess();
		register();
		startAcceptor();
	}
	

	@Override
	public String getUserAgent() {
		int index = RandomUtils.nextInt(0, defUAs.length);
		return defUAs[index];
	}

	protected void push(CrawlerRequest request) {
		request.setCrawlerName(crawlerName);
		queue.push(request);
	}

	public void setQueue(CrawlerQueue queue) {
		this.queue = queue;
	}

	@Override
	public String[] allowRules() {
		return null;
	}

	@Override
	public String[] denyRules() {
		return null;
	}

	@Override
	public String proxy() {
		return null;
	}

	@Override
	public void handleErrorRequest(CrawlerRequest request) {
		log.info("Seimi got a error request={}", request);
	}

	@Override
	public String seimiAgentHost() {
		return null;
	}

	@Override
	public int seimiAgentPort() {
		return 80;
	}

	@Override
	public List<CrawlerRequest> startRequests() {
		return null;
	}

	public boolean isUseCookie() {
		return useCookie;
	}

	public void setUseCookie(boolean useCookie) {
		this.useCookie = useCookie;
	}

	public boolean isUseUnrepeated() {
		return useUnrepeated;
	}

	public void setUseUnrepeated(boolean useUnrepeated) {
		this.useUnrepeated = useUnrepeated;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getHttpTimeOut() {
		return httpTimeOut;
	}

	public void setHttpTimeOut(int httpTimeOut) {
		this.httpTimeOut = httpTimeOut;
	}

	/**
	 * 每个爬虫都需要实现这个start方法，实现第一个页面解析
	 */
	public abstract void start(CrawlerResponse response);

	/**
	 * 对外统一调用该方法启动爬虫
	 */
	public void runCrawler() {
		if(!running){
			startAcceptor();
		}
		sendRequest();
	}

	/**
	 * 停止爬虫
	 */
	public void stopCrawler() {
		if(running){
			running = false;
			//停止对应的线程
			acceptor.stop();
			log.trace(" stop [{}],receive request...",acceptor.getThreadName());
		}
	}
	
	private void startAcceptor(){
		running = true;
		
		acceptor = new Acceptor(crawlerContext, crawlerName);
		String threadName = "Crawler-"+crawlerName+"-Acceptor";
		acceptor.setThreadName(threadName);
		Thread t = new Thread(acceptor, threadName);
		//后台运行
        t.setDaemon(true);
        t.start();
        log.trace(" start [{}],receive request...",threadName);
       
	}

	/**
	 * 注册爬虫信息
	 */
	private void register() {
		CrawlerDefinition.Builder builder = CrawlerDefinition
				.build(crawlerName);
		builder.crawlerClazz(clazz).crawlerInstance(this).delay(delay)
				.httpTimeOut(httpTimeOut).proxy(proxy()).useCookie(useCookie)
				.useUnrepeated(useUnrepeated).queueInstance(queue);
		crawlerContext.addCrawler(builder.build());
	}

	/**
	 * 初始化方法
	 */
	private void sendRequest() {
		String[] startUrls = startUrls();
		boolean trigger = false;
		if (ArrayUtils.isNotEmpty(startUrls)) {
			for (String url : startUrls) {
				CrawlerRequest.Builder requestBuilder = CrawlerRequest.build(
						url, METHOD_START);
				List<String> urlPies = Splitter.on("##").omitEmptyStrings()
						.splitToList(url);
				if (urlPies.size() >= 2
						&& HttpMethod.POST.val().equalsIgnoreCase(
								urlPies.get(0))) {
					requestBuilder.httpMethod(HttpMethod.POST);
				}
				requestBuilder.crawlerName(crawlerName);
				queue.push(requestBuilder.build());
				log.info("{} url={} started", crawlerName, url);
			}
			trigger = true;
		}

		List<CrawlerRequest> requests = startRequests();

		if (requests != null && requests.size() > 0) {
			for (CrawlerRequest request : requests) {
				if (Strings.isNullOrEmpty(request.getCallBack())) {
					request.setCallBack(METHOD_START);
				}
				queue.push(request);
				log.info("{} url={} started", crawlerName, request.getUrl());
			}
			trigger = true;
		}

		if (!trigger) {
			log.error("crawler:{} can not find start urls!", crawlerName);
		}
	}
	
	
	private void crawlerAnnotationProcess(){
		
		Crawler c = clazz.getAnnotation(Crawler.class);
		if(c != null){
			delay=c.delay();
			useCookie = c.useCookie();
			useUnrepeated = c.useUnrepeated();
			httpTimeOut = c.httpTimeOut();
			
		}
	}
	
	
}
