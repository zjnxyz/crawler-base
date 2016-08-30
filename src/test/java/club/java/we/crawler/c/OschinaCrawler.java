package club.java.we.crawler.c;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.StringUtils;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import cn.wanghaomiao.xpath.model.JXDocument;
import club.java.we.crawler.annotation.Crawler;
import club.java.we.crawler.core.AbstractBaseCrawler;
import club.java.we.crawler.core.CrawlerContext;
import club.java.we.crawler.core.CrawlerRequest;
import club.java.we.crawler.core.CrawlerResponse;
import club.java.we.crawler.http.HttpMethod;
/**
 * 每次请求间隔两秒
 * 请求需要带上cookie
 * @author riverzu
 *
 */
@Crawler(delay=2000,useCookie=true)
@Log4j2
public class OschinaCrawler extends AbstractBaseCrawler{

	public OschinaCrawler(CrawlerContext crawlerContext) {
		super(crawlerContext, OschinaCrawler.class);
	}

	@Override
	public String[] startUrls() {
		// TODO Auto-generated method stub
		return new String[]{"http://www.oschina.net/"};
	}

	@Override
	public void start(CrawlerResponse response) {
		//提交登陆请求
        CrawlerRequest login = CrawlerRequest.build("https://www.oschina.net/action/user/hash_login","afterLogin").build();

        Map<String,String> params = new HashMap<>();
        params.put("email","469174375@qq.com");
        params.put("pwd","zjn621705");
        params.put("save_login","1");
        params.put("verifyCode","");
        login.setHttpMethod(HttpMethod.POST);
        login.setParams(params);
        push(login);
        System.out.println("第一次请求成功");
	}
	
	 public void afterLogin(CrawlerResponse response){
         log.info(response.getContent());
         push(CrawlerRequest.build("http://www.oschina.net/home/go?page=blog","minePage").build());
         System.out.println("第二次请求成功");
     }

	    public void minePage(CrawlerResponse response){
	        JXDocument doc = response.document();
	        try {
	            log.info("uname:{}", StringUtils.join(doc.sel("//div[@class='name']/a/text()"),""));
	        } catch (XpathSyntaxErrorException e) {
	            log.debug(e.getMessage(),e);
	        }
	        
	        System.out.println("第三次请求成功");
	    }

}
