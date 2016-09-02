package club.java.we.crawler.core;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import club.java.we.crawler.annotation.Crawler;
import club.java.we.crawler.annotation.process.HeaderAnnotationProcess;
import club.java.we.crawler.config.CrawlerDefinition;
import club.java.we.crawler.config.HeaderDefination;
import club.java.we.crawler.http.HttpMethod;
import club.java.we.crawler.support.UserAgentPool;
@Log4j2
public abstract class AbstractBaseCrawler implements BaseCrawler {

	public static String KEY_CRAWLER_NAME = "Crawler-%s-Acceptor-%s";

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
//	private Acceptor acceptor;
	
	protected Acceptor[] acceptors;
	
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
		startAcceptorThreads();
	}
	

	protected void push(CrawlerRequest request) {
		CrawlerRequest prevRequest = RequestContentHolder.getCrawlerRequest();
		if(Strings.isNullOrEmpty(request.getQueueName())){
			request.setQueueName(prevRequest.getQueueName());
		}
		if(request.getHeaders() == null){
			request.setHeaders(prevRequest.getHeaders());
		}
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
	
	 /**
     * Acceptor thread count.
     */
    protected int acceptorThreadCount = 1;
    
	public int getAcceptorThreadCount(){
		return acceptorThreadCount;
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
	
	public UserAgentPool getuAgentPool(){
		return new UserAgentPool();
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
			startAcceptorThreads();
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
			for(int i = 0;i<acceptors.length;i++){
				acceptors[i].stop();
				log.info(" stop [{}],receive request...",acceptors[i].getThreadName());
			}
		}
	}
	
	List<String> threadNames = new ArrayList<String>(getAcceptorThreadCount());
	
	 protected final void startAcceptorThreads() {
		 	running = true;
	        int count = getAcceptorThreadCount();
	        acceptors = new Acceptor[count];
	        for(int i=0;i<count;i++){
	        	String threadName = String.format(KEY_CRAWLER_NAME, crawlerName,i);
	        	threadNames.add(threadName);
	        	acceptors[i]= new Acceptor(crawlerContext, crawlerName);
	        	acceptors[i].setThreadName(threadName);
				Thread t = new Thread(acceptors[i], threadName);
				//后台运行
		        t.setDaemon(true);
		        t.start();
		        log.info(" start [{}],receive request...",threadName);
	        }
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
	 * 创建请求头
	 * @param suffix
	 * @return
	 */
	private HeaderDefination createHeader(String suffix){
		HeaderDefination header = new HeaderDefination(suffix);
		header.setUserAgent(getuAgentPool().select());
		HeaderAnnotationProcess.process(clazz, header);
		return header;
	}

	
	int threadIndex = 0;
	
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
				if(threadNames.size() == 0){
					return;
				}
				String queueName = threadNames.get(threadIndex %threadNames.size());
				threadIndex++;
				requestBuilder.queueName(queueName);
				// 绑定header数据
				requestBuilder.headers(createHeader(queueName));
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
				String queueName = threadNames.get(threadIndex %threadNames.size());
				threadIndex++;
				request.setQueueName(queueName);
				request.setHeaders(createHeader(queueName));
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
