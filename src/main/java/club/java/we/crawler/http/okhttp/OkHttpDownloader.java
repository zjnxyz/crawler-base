package club.java.we.crawler.http.okhttp;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import cn.wanghaomiao.xpath.model.JXDocument;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import club.java.we.crawler.config.CrawlerDefinition;
import club.java.we.crawler.config.HeaderDefination;
import club.java.we.crawler.constant.BodyType;
import club.java.we.crawler.core.AbstractBaseCrawler;
import club.java.we.crawler.core.CrawlerDownloader;
import club.java.we.crawler.core.CrawlerRequest;
import club.java.we.crawler.core.CrawlerResponse;
import club.java.we.crawler.utils.StrFormatUtils;
/**
 * 
 * @author riverzu
 *
 */
@Log4j2
public class OkHttpDownloader implements CrawlerDownloader{
	
	private CrawlerDefinition crawlerDefinition;
	
	private CrawlerRequest currentRequest;
	
	private okhttp3.Request.Builder currentRequestBuilder;
	
    private OkHttpClient okHttpClient;
    
    private okhttp3.Response lastResponse;
    

	public OkHttpDownloader(CrawlerDefinition crawlerDefinition,
			CrawlerRequest request) {
		this.crawlerDefinition = crawlerDefinition;
		this.currentRequest = request;
		initOkHttp();
	}
	
	private void initOkHttp(){
		OkHttpClient.Builder okHttpBuilder = OkHttpClientBuilderProvider.getInstance();
        if (crawlerDefinition.isUseCookie()){
//            okHttpBuilder.cookieJar(CookiesMgrProvider.getInstance());
        	okHttpBuilder.cookieJar(currentRequest.getHeaders().getCookiesManager());
        }
        if (crawlerDefinition.getStdProxy()!=null){
            okHttpBuilder.proxy(crawlerDefinition.getStdProxy());
        }
        okHttpBuilder.readTimeout(crawlerDefinition.getHttpTimeOut(), TimeUnit.MILLISECONDS);
        okHttpClient = okHttpBuilder.build();
        currentRequestBuilder = OkHttpRequestGenerator.getOkHttpRequesBuilder(currentRequest, crawlerDefinition);
	}

	@Override
	public CrawlerResponse process() throws Exception {
		lastResponse = okHttpClient.newCall(currentRequestBuilder.build()).execute();
//		int i=0;
//		while(i<3){
//			try{
//				lastResponse = okHttpClient.newCall(currentRequestBuilder.build()).execute();
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			//如果状态码不是200，或302（重定向），更换header里面的数据，反防爬虫
//			if( lastResponse != null &&(lastResponse.code() == 200 || lastResponse.code() == 302)){
//				break;
//			}
//			refreshHeader();
//			i++;
//		}
        return renderResponse(lastResponse,currentRequest);
	}
	
	@Deprecated
	private void refreshHeader(){
//		AbstractBaseCrawler abstractBaseCrawler = crawlerDefinition.getCrawlerInstance();
//		Proxy proxy = crawlerDefinition.getStdProxy();
//		if( proxy != null){
//			int i=0;
//			while(i<3){
//				String proxyStr = abstractBaseCrawler.proxy();
//				if(!crawlerDefinition.getProxyStr().equals(proxyStr)){
//					crawlerDefinition.setProxyStr(proxyStr);
//					break;
//				}
//				i++;
//			}
//		}
//		
//		//刷新UA数据
//		int loopCount = 0;
//		HeaderDefination hd = currentRequest.getHeaders();
//		String oldUA = hd.getUserAgent();
//		if(!Strings.isNullOrEmpty(oldUA)){
//			while(loopCount <3){
//				String ua = abstractBaseCrawler.getUserAgent();
//				if(ua == null){
//					break;
//				}
//				if(!ua.equals(oldUA)){
//					hd.setUserAgent(ua);
//					break;
//				}
//				loopCount++;
//			}
//		}
//		//清空对应的cookies数据
////		hd.getCookiesManager().clearCookies();
//		//删除referer数据
////		currentRequest.setReferer(null);
//		//重新生成请求参数
//		currentRequestBuilder = OkHttpRequestGenerator.getOkHttpRequesBuilder(currentRequest, crawlerDefinition);
		
	}

	@Override
	public CrawlerResponse metaRefresh(String nextUrl) throws Exception {
		HttpUrl lastUrl = lastResponse.request().url();
        if (!nextUrl.startsWith("http")){
            String prefix = lastUrl.scheme()+"://"+lastUrl.host()+lastUrl.encodedPath();
            nextUrl = prefix + nextUrl;
        }
        log.info("Seimi refresh url to={} from={}",nextUrl,lastUrl.toString());
        currentRequestBuilder.url(nextUrl);
        lastResponse = okHttpClient.newCall(currentRequestBuilder.build()).execute();
        return renderResponse(lastResponse,currentRequest);
	}

	@Override
	public int statusCode() {
		return lastResponse.code();
	}
	
	 private CrawlerResponse renderResponse(okhttp3.Response okResponse,CrawlerRequest request){
		 
		 	CrawlerResponse response = new CrawlerResponse();
		 
		 	response.setRealUrl(lastResponse.request().url().toString());
		 	response.setUrl(request.getUrl());
		 	
		 	// response不再存放这个值，直接从RequestContentHolder拿数据
//		 	response.setRequest(request);
		 	
		 	response.setMeta(request.getMeta());
//		 	response.setReferer(hcResponse.header("Referer"));
	        ResponseBody okResponseBody = okResponse.body();
	        if (okResponseBody!=null){
	            String type = okResponseBody.contentType().type().toLowerCase();
	            String subtype = okResponseBody.contentType().subtype().toLowerCase();
	            if (type.contains("text")||type.contains("json")||type.contains("ajax")||subtype.contains("json")
	                    ||subtype.contains("ajax")){
	            	response.setBodyType(BodyType.TEXT);
	                try {
	                    byte[] data = okResponseBody.bytes();
	                    String utfContent = new String(data,"UTF-8");
	                    String charsetFinal = renderRealCharset(utfContent);
	                    if (charsetFinal.equals("UTF-8")){
	                    	response.setContent(utfContent);
	                    }else {
	                    	response.setContent(new String(data,charsetFinal));
	                    }
	                } catch (Exception e) {
	                    log.error("no content data");
	                }
	            }else {
	            	response.setBodyType(BodyType.BINARY);
	                try {
	                	response.setData(okResponseBody.bytes());
	                } catch (Exception e) {
	                    log.error("no content data");
	                }
	            }
	        }
	        return response;
	    }
	 //TODO 可以优化，存在两次解析的过程
	 private String renderRealCharset(String content) throws XpathSyntaxErrorException {
		 	JXDocument doc = new JXDocument(content);
	        String charset = StrFormatUtils.getFirstEmStr(doc.sel("//meta[@charset]/@charset"),"").trim();
	        if (StringUtils.isBlank(charset)){
	            charset = StrFormatUtils.getFirstEmStr(doc.sel("//meta[@http-equiv='charset']/@content"),"").trim();
	        }
	        if (StringUtils.isBlank(charset)){
	            String ct = StringUtils.join(doc.sel("//meta[@http-equiv='Content-Type']/@content|//meta[@http-equiv='content-type']/@content"),";").trim();
	            charset = StrFormatUtils.parseCharset(ct.toLowerCase());
	        }
	        return Strings.isNullOrEmpty(charset)?"UTF-8":charset;
	    }

}
