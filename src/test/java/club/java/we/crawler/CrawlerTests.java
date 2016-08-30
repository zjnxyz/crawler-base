package club.java.we.crawler;

import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j2;
import club.java.we.crawler.c.DoubanCrawler;
import club.java.we.crawler.c.OschinaCrawler;
import club.java.we.crawler.core.CrawlerContext;
@Log4j2
public class CrawlerTests {
	
	public static void main(String[] args) {
		
		log.info("Crawler[{}] init complete.", "helloworld");
		
//		log.info("info------");
//		log.error("error------");
//		log.debug("debug---");
//		log.trace("trace---");
//		log.fatal("fatal");
//		log.warn("warn---");
		
		CrawlerContext context = CrawlerContext.build().build();
		DoubanCrawler doubanCrawler = new DoubanCrawler(context);
		doubanCrawler.runCrawler();
//		
//		OschinaCrawler oschinaCrawler = new OschinaCrawler(context);
//		oschinaCrawler.runCrawler();
//		
		try {
			TimeUnit.SECONDS.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
