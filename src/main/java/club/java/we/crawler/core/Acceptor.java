package club.java.we.crawler.core;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;

import static com.google.common.base.Preconditions.*;
import club.java.we.crawler.config.CrawlerDefinition;
import club.java.we.crawler.constant.BodyType;
import club.java.we.crawler.http.okhttp.OkHttpDownloader;
import club.java.we.crawler.utils.StructValidatorUtils;


/**
 * 任务接收器
 * @author riverzu
 *
 */
@Log4j2
public class Acceptor implements Runnable {
	

	public enum AcceptorState {
        NEW, RUNNING, PAUSED, ENDED
    }
	
	protected volatile AcceptorState state = AcceptorState.NEW;
	
	private Pattern metaRefresh = Pattern.compile("<(?:META|meta|Meta)\\s+(?:HTTP-EQUIV|http-equiv)\\s*=\\s*\"refresh\".*(?:url|URL)=(\\S*)\".*/?>");
	
    protected volatile boolean running = true;
    
    protected volatile boolean paused = false;
    
    private CrawlerContext context;
    
    /** 当前线程使用的队列名称 */
    private String queueName;
    
    private String crawlerName;
	
    public Acceptor(CrawlerContext context, String crawlerName) {
		this.context = context;
		this.crawlerName = crawlerName;
	}
    
	public final AcceptorState getState() {
        return state;
    }

    private String threadName;
    protected final void setThreadName(final String threadName) {
        this.threadName = threadName;
        this.queueName = threadName;
    }
    protected final String getThreadName() {
        return threadName;
    }
    
	@Override
	public void run() {
		
		 while (running) {
			CrawlerDefinition crawlerDefinition = context.getCrawler(crawlerName);
			checkNotNull(crawlerDefinition,"crawler init failure");
			CrawlerQueue queue = context.getQueue();
			checkNotNull(queue,"not found crawler queue");
			
			if(crawlerDefinition.getDelay() > 0){
				paused = true;
			}
			
			if( running && paused){
				log.trace("crawler[{}] paused,wait {} microseconds",crawlerDefinition.getCrawlerName(),crawlerDefinition.getDelay());
				state = AcceptorState.PAUSED;
				try {
					int delay = RandomUtils.nextInt(crawlerDefinition.getDelay(), crawlerDefinition.getDelay()+1000);
					TimeUnit.MILLISECONDS.sleep(delay);
				} catch (InterruptedException e) {
					log.error("crawler [{}] delay fail.",crawlerDefinition.getCrawlerName(),e.getCause());
				}
				paused = false;
			}
			
			if(!running){
				break;
			}
			state = AcceptorState.RUNNING;
			//  多线程处理业务
			if(running && !paused){
				CrawlerRequest request = queue.bPop(queueName);
				if(request == null){
					log.trace("crawler[{}] queue is empty!",crawlerDefinition.getCrawlerName());
					continue;
				}
				final Processor processor = new Processor(crawlerDefinition);
				context.getExecutorEngine().execute(Arrays.asList(request), new ExecuteUnit<CrawlerRequest>() {
					@Override
					public void execute(CrawlerRequest input) throws Exception {
						 processor.onProcessor(input);
					}
				});
			}
			
		 }
		
	}
	public boolean isRunning() {
		return running;
	}
	
	
	public void stop(){
		this.running = false;
	}
	
	/**
	 * 请求实际处理类
	 * @author riverzu
	 *
	 */
	class Processor{
		
		private CrawlerDefinition crawlerDefinition;
		
		public Processor(CrawlerDefinition crawlerDefinition){
			this.crawlerDefinition = crawlerDefinition;
		}
		
		public void onProcessor(CrawlerRequest request){
			BaseCrawler crawler = crawlerDefinition.getCrawlerInstance();
			 if (!StructValidatorUtils.validateAllowRules(crawler.allowRules(), request.getUrl())) {
                 log.warn("Request={} will be dropped by allowRules=[{}]", JSON.toJSONString(request), StringUtils.join(crawler.allowRules(), ","));
                 return;
             }
             if (StructValidatorUtils.validateDenyRules(crawler.denyRules(), request.getUrl())) {
            	 log.warn("Request={} will be dropped by denyRules=[{}]", JSON.toJSONString(request), StringUtils.join(crawler.denyRules(), ","));
                 return;
             }
             
             //如果启用了系统级去重机制并且为首次处理则判断一个Request是否已经被处理过了
             if (request.getCurrentReqCount() >= request.getMaxReqCount()) {
                 return;
             }
             
             CrawlerQueue queue = crawlerDefinition.getQueueInstance();
             
             if (!request.isSkipDuplicateFilter() && crawlerDefinition.isUseUnrepeated() && queue.isProcessed(request) && request.getCurrentReqCount() == 0) {
            	 log.info("This request has bean processed,so current request={} will be dropped!", JSON.toJSONString(request));
                 return;
             }
             queue.addProcessed(request);
             
             //初始化下载器
             CrawlerDownloader downloader = new OkHttpDownloader(crawlerDefinition, request);
             try {
				 CrawlerResponse response = downloader.process();
				 if (BodyType.TEXT.equals(response.getBodyType())) {
	                    Matcher mm = metaRefresh.matcher(response.getContent());
	                    int refreshCount = 0;
	                    while (!request.isUseSeimiAgent() && mm.find() && refreshCount < 3) {
	                        String nextUrl = mm.group(1).replaceAll("'", "");
	                        response = downloader.metaRefresh(nextUrl);
	                        mm = metaRefresh.matcher(response.getContent());
	                        refreshCount += 1;
	                    }
	                }
				 
				 Method requestCallback = crawlerDefinition.getMemberMethods().get(request.getCallBack());
                 if (requestCallback == null) {
                	 log.trace("Crawler[{}],url={} ,have not callback ", crawlerDefinition.getCrawlerName(), request.getUrl());
                     return;
                 }
                  //把request放到上下文中(先设置下一次请求使用的referer)
                 request.getHeaders().setReferer(response.getUrl());
     		 	 RequestContentHolder.setCrawlerRequest(request);
     		 	
                 requestCallback.invoke(crawler, response);
                 log.debug("Crawler[{}] ,url={} ,responseStatus={}", crawlerDefinition.getCrawlerName(), request.getUrl(), downloader.statusCode());

			} catch (Exception e) {
				log.error(e.getMessage(), e);
                if (request.getCurrentReqCount() < request.getMaxReqCount()) {
                    request.incrReqCount();
                    queue.push(request);
                    log.info("Request process error,req will go into queue again,url={},maxReqCount={},currentReqCount={}", request.getUrl(), request.getMaxReqCount(), request.getCurrentReqCount());
                } else if (request.getCurrentReqCount() >= request.getMaxReqCount() && request.getMaxReqCount() > 0) {
                    crawler.handleErrorRequest(request);
                }
			}
             
		}
		
		
	}

	

}
