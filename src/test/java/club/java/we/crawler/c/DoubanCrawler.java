package club.java.we.crawler.c;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import cn.wanghaomiao.xpath.model.JXDocument;
import cn.wanghaomiao.xpath.model.JXNode;
import club.java.we.crawler.annotation.Crawler;
import club.java.we.crawler.annotation.Header;
import club.java.we.crawler.core.AbstractBaseCrawler;
import club.java.we.crawler.core.CrawlerContext;
import club.java.we.crawler.core.CrawlerRequest;
import club.java.we.crawler.core.CrawlerResponse;
import club.java.we.crawler.core.RequestContentHolder;
import club.java.we.crawler.utils.StrUtils;

@Crawler(delay=2000,useCookie=true,httpTimeOut=5000)
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
		return new String[]{"https://www.douban.com/link2/?url=http%3A%2F%2Fwww.douban.com%2Fgroup%2Ftianhezufang%2F&query=%E5%B9%BF%E5%B7%9E%E7%A7%9F%E6%88%BF&cat_id=1019&type=search&pos=0",
				"https://www.douban.com/link2/?url=http%3A%2F%2Fwww.douban.com%2Fgroup%2Fhaizhuzufang%2F&query=%E5%B9%BF%E5%B7%9E%E7%A7%9F%E6%88%BF&cat_id=1019&type=search&pos=2",
				"https://www.douban.com/link2/?url=http%3A%2F%2Fwww.douban.com%2Fgroup%2Fpanyuzufang%2F&query=%E5%B9%BF%E5%B7%9E%E7%A7%9F%E6%88%BF&cat_id=1019&type=search&pos=3",
				"https://www.douban.com/link2/?url=http%3A%2F%2Fwww.douban.com%2Fgroup%2FHZhome%2F&query=%E5%B9%BF%E5%B7%9E%E7%A7%9F%E6%88%BF&cat_id=1019&type=search&pos=7",
				"https://www.douban.com/link2/?url=http%3A%2F%2Fwww.douban.com%2Fgroup%2F378279%2F&query=%E5%B9%BF%E5%B7%9E%E7%A7%9F%E6%88%BF&cat_id=1019&type=search&pos=20",
				"https://www.douban.com/link2/?url=http%3A%2F%2Fwww.douban.com%2Fgroup%2F537239%2F&query=%E5%B9%BF%E5%B7%9E%E7%A7%9F%E6%88%BF&cat_id=1019&type=search&pos=23",
				"https://www.douban.com/link2/?url=http%3A%2F%2Fwww.douban.com%2Fgroup%2Fmaquezufang%2F&query=%E5%B9%BF%E5%B7%9E%E7%A7%9F%E6%88%BF&cat_id=1019&type=search&pos=29"
				};
//		return new String[]{"https://www.douban.com"};

	}

	@Override
	public void start(CrawlerResponse response) {
//		System.out.println(response.getContent());
		JXDocument doc = response.document();
//		System.out.println(response.getUrl());
//		System.out.println(response.getRealUrl());
		if(response.getContent().startsWith("<script>")){
			handerCheck(RequestContentHolder.getCrawlerRequest().getHeaders().getUserAgent(), RequestContentHolder.getCrawlerRequest().getUrl());
		}
		try {
			
			List<JXNode> jxNodes = doc.selN("//table[@class='olt']/tbody/tr[position()>1]");
			for(JXNode node:jxNodes){
				if(!node.isText()){
					String nextUrl = StrUtils.join(node.sel("/td[@class='title']/a/@href"));
					if(!Strings.isNullOrEmpty(nextUrl)){
//						System.out.println("detail---nextUrl:"+nextUrl);
						push(CrawlerRequest.build(nextUrl, "next").build());
					}
				}
			}
			
			//进入more，下一页
			String nextUrl = StrUtils.join(doc.sel("//div[@class='group-topics-more']/a/@href"));
			if(!Strings.isNullOrEmpty(nextUrl)){
				System.out.println("list--nextUrl:"+nextUrl);
				push(CrawlerRequest.build(nextUrl, "listArtice").build());
			}
			
			
		} catch (XpathSyntaxErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void handerCheck(String ua,String rurl){
		String url = "https://sec.douban.com/a?c=45e83d&d=%s&r=%s";
		String nextUrl = String.format(url, "MacIntel|"+ua+"|Google Inc.",rurl);
		push(CrawlerRequest.build(nextUrl, "start").build());
	}
	
	public void listArtice(CrawlerResponse response){
//		System.out.println(response.getContent());
		JXDocument doc = response.document();
		try {
			
			List<JXNode> jxNodes = doc.selN("//table[@class='olt']/tbody/tr[position()>1]");
			for(JXNode node:jxNodes){
				if(!node.isText()){
					String nextUrl = StrUtils.join(node.sel("/td[@class='title']/a/@href"));
					if(!Strings.isNullOrEmpty(nextUrl)){
//						System.out.println("detail---nextUrl:"+nextUrl);
						push(CrawlerRequest.build(nextUrl, "next").build());
					}
				}
			}
			
			String nextUrl = StrUtils.join(doc.sel("//div[@class='paginator']/span[@class='next']/a/@href"));
			if(!Strings.isNullOrEmpty(nextUrl)){
				System.out.println("listArtice--nextUrl:"+nextUrl);
				push(CrawlerRequest.build(nextUrl, "listArtice").build());
			}
		} catch (XpathSyntaxErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void prase(){
		
	}
	
	public void next(CrawlerResponse response){
		JXDocument doc = response.document();
//		System.out.println(response.getUrl());
//		System.out.println(response.getRealUrl());
		
		CrawlerRequest prevRequest = RequestContentHolder.getCrawlerRequest();
		try {
			System.out.println("--正在处理---"+prevRequest.getQueueName()+"--"+prevRequest.getUrl());
			String nextUrl = StrUtils.join(doc.sel("//div[@class='paginator']/span[@class='next']/a/@href"));
			if(!Strings.isNullOrEmpty(nextUrl)){
//				System.out.println("comment--nextUrl:"+nextUrl);
//				push(CrawlerRequest.build(nextUrl, "next").referer(response.getUrl()).crawlerName(response.getRequest().getCrawlerName()).build());
			}
			
		} catch (XpathSyntaxErrorException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getAcceptorThreadCount() {
		return 7;
	}
	
}
