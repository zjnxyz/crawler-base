package club.java.we.crawler.core;


public interface CrawlerDownloader {
	
	 /**
     * 处理抓取请求生成response
     */
    CrawlerResponse process() throws Exception;

    /**
     * 处理meta标签refresh场景
     *
     * @param nextUrl 重定向URL
     * @return 请求的最终返回体
     */
    CrawlerResponse metaRefresh(String nextUrl) throws Exception;

    /**
     * http请求状态
     * @return http状态码
     */
    int statusCode();

}
