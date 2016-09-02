package club.java.we.crawler.core;

/**
 * 抓取队列
 * @author riverzu
 *
 */
public interface CrawlerQueue {
	
	/**
     * 阻塞式出队一个请求
     * @return
     */
    CrawlerRequest bPop(String queueName);
    /**
     * 入队一个请求
     * @param req
     * @return
     */
    boolean push(CrawlerRequest request);
    /**
     * 任务队列剩余长度
     * @return
     */
    long len(String crawlerName);

    /**
     * 判断一个URL是否处理过了
     * @param req
     * @return
     */
    boolean isProcessed(CrawlerRequest request);

    /**
     * 记录一个处理过的请求
     * @param req
     */
    void addProcessed(CrawlerRequest request);

    /**
     * 目前总共的抓取数量
     * @return
     */
    long totalCrawled(String crawlerName);

}
