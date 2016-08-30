package club.java.we.crawler.core;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j2;

import club.java.we.crawler.config.CrawlerProperties;
import club.java.we.crawler.config.CrawlerPropertiesConstant;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
@Log4j2
public final class ExecutorEngine {
	
	private final ListeningExecutorService executorService;
	
	public ExecutorEngine(final CrawlerProperties crawlerProperties) {
        int executorMinIdleSize = crawlerProperties.getValue(CrawlerPropertiesConstant.EXECUTOR_MIN_IDLE_SIZE);
        int executorMaxSize = crawlerProperties.getValue(CrawlerPropertiesConstant.EXECUTOR_MAX_SIZE);
        long executorMaxIdleTimeoutMilliseconds = crawlerProperties.getValue(CrawlerPropertiesConstant.EXECUTOR_MAX_IDLE_TIMEOUT_MILLISECONDS);
        executorService = MoreExecutors.listeningDecorator(MoreExecutors.getExitingExecutorService(
                new ThreadPoolExecutor(executorMinIdleSize, executorMaxSize, executorMaxIdleTimeoutMilliseconds, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>())));
    }
	
	public  <I> void execute(final Collection<I> inputs, final ExecuteUnit<I> executeUnit){
		for(I input:inputs){
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					try {
						executeUnit.execute(input);
					} catch (Exception e) {
						log.error("ExecutorEngine excute fail:{}",e);
					}
				}
			});
		}
	}

}
