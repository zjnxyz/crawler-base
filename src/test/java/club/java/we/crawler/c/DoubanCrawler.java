package club.java.we.crawler.c;

import com.google.common.base.Strings;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import cn.wanghaomiao.xpath.model.JXDocument;
import club.java.we.crawler.annotation.Crawler;
import club.java.we.crawler.annotation.Header;
import club.java.we.crawler.core.AbstractBaseCrawler;
import club.java.we.crawler.core.CrawlerContext;
import club.java.we.crawler.core.CrawlerRequest;
import club.java.we.crawler.core.CrawlerResponse;
import club.java.we.crawler.utils.StrUtils;

@Crawler(delay=500,useCookie=true,httpTimeOut=5000)
@Header(connection="keep-alive",acceptEncoding="gzip, deflate, sdch, br",upgradeInsecureRequests="1")
public class DoubanCrawler extends AbstractBaseCrawler{

	public DoubanCrawler(CrawlerContext crawlerContext) {
		super(crawlerContext, DoubanCrawler.class);
	}

//	@Override
//	public String proxy() {
//		// TODO Auto-generated method stub
//		return "https://101.231.46.34:8000";
//	}
	@Override
	public String[] startUrls() {
		return new String[]{"https://www.douban.com/link2/?url=http%3A%2F%2Fwww.douban.com%2Fgroup%2Ftianhezufang%2F&query=%E5%B9%BF%E5%B7%9E%E7%A7%9F%E6%88%BF&cat_id=1019&type=search&pos=0"};
//		return new String[]{"https://www.douban.com"};

	}

	@Override
	public void start(CrawlerResponse response) {
//		System.out.println(response.getContent());
		JXDocument doc = response.document();
		System.out.println(response.getUrl());
		System.out.println(response.getRealUrl());
		try {
			String nextUrl = StrUtils.join(doc.sel("//*[@id='group-topics']/div[2]/table/tbody/tr[2]/td[1]/a/@href"));
			if(!Strings.isNullOrEmpty(nextUrl)){
				
				System.out.println("nextUrl:"+nextUrl);
				push(CrawlerRequest.build(nextUrl, "next").referer(response.getUrl()).build());
			}
		} catch (XpathSyntaxErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void next(CrawlerResponse response){
		JXDocument doc = response.document();
		System.out.println(response.getUrl());
		System.out.println(response.getRealUrl());
		try {
			String nextUrl = StrUtils.join(doc.sel("//div[@class='paginator']/span[@class='next']/a/@href"));
			if(!Strings.isNullOrEmpty(nextUrl)){
				System.out.println("nextUrl:"+nextUrl);
				push(CrawlerRequest.build(nextUrl, "next").referer(response.getUrl()).build());
			}
			
		} catch (XpathSyntaxErrorException e) {
			e.printStackTrace();
		}
	}

}
