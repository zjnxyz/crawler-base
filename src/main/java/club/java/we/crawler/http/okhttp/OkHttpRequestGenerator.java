package club.java.we.crawler.http.okhttp;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;

import org.apache.commons.lang3.StringUtils;

import club.java.we.crawler.config.CrawlerDefinition;
import club.java.we.crawler.config.HeaderDefination;
import club.java.we.crawler.core.BaseCrawler;
import club.java.we.crawler.core.CrawlerRequest;
import club.java.we.crawler.http.HttpMethod;
import club.java.we.crawler.http.SeimiAgentContentType;
import static com.google.common.base.Preconditions.*;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;

public class OkHttpRequestGenerator {
	
	public static Request.Builder getOkHttpRequesBuilder(CrawlerRequest request, CrawlerDefinition crawlerDefinition){
        BaseCrawler crawler = crawlerDefinition.getCrawlerInstance();
        Request.Builder requestBuilder = new Request.Builder();
        if (request.isUseSeimiAgent()){
        	checkArgument(Strings.isNullOrEmpty(crawlerDefinition.getSeimiAgentUrl()), "SeimiAgentUrl is blank.");
            
            FormBody.Builder formBodyBuilder = new FormBody.Builder()
                    .add("url", request.getUrl());
            if (StringUtils.isNotBlank(crawler.proxy())){
                formBodyBuilder.add("proxy", crawler.proxy());
            }
            if (request.getSeimiAgentRenderTime() > 0){
                formBodyBuilder.add("renderTime", String.valueOf(request.getSeimiAgentRenderTime()));
            }
            if (StringUtils.isNotBlank(request.getSeimiAgentScript())){
                formBodyBuilder.add("script", request.getSeimiAgentScript());
            }
            //如果针对SeimiAgent的请求设置是否使用cookie，以针对请求的设置为准，默认使用全局设置
            if ((request.getSeimiAgentUseCookie() == null && crawlerDefinition.isUseCookie()) 
            		|| (request.getSeimiAgentUseCookie() != null && request.getSeimiAgentUseCookie())) {
                formBodyBuilder.add("useCookie", "1");
            }
            if (request.getParams() != null && request.getParams().size() > 0) {
                formBodyBuilder.add("postParam", JSON.toJSONString(request.getParams()));
            }
            if (request.getSeimiAgentContentType().val()> SeimiAgentContentType.HTML.val()){
                formBodyBuilder.add("contentType",request.getSeimiAgentContentType().typeVal());
            }
            requestBuilder.url(crawlerDefinition.getSeimiAgentUrl()).post(formBodyBuilder.build()).build();
        }else {
            requestBuilder.url(request.getUrl());
//            requestBuilder.header("User-Agent", crawlerDefinition.isUseCookie() ? crawlerDefinition.getCurrentUA() : crawler.getUserAgent())
//                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
//                    .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
            Map<String, String> headers = request.getHeaders().getHeaders();
            
            for(String key:headers.keySet()){
            	String value = headers.get(key);
            	if(!Strings.isNullOrEmpty(value)){
            		requestBuilder.header(key, value);
            	}
            }
            //组装Referer数据 放到headers中去了
//            if(!Strings.isNullOrEmpty(request.getReferer())){
//            	requestBuilder.header(HeaderDefination.REFERER, request.getReferer());
//            }
            
            if (HttpMethod.POST.equals(request.getHttpMethod())) {
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                if (request.getParams() != null) {
                    for (Map.Entry<String, String> entry : request.getParams().entrySet()) {
                        formBodyBuilder.add(entry.getKey(), entry.getValue());
                    }
                }
                requestBuilder.post(formBodyBuilder.build());
            } else {
                String queryStr = "";
                if (request.getParams()!=null&&!request.getParams().isEmpty()){
                    queryStr += "?";
                    for (Map.Entry<String, String> entry : request.getParams().entrySet()) {
                        queryStr= queryStr+entry.getKey()+"="+entry.getValue()+"&";
                    }
                    requestBuilder.url(request.getUrl()+queryStr);
                }
                requestBuilder.get();
            }
        }
        return requestBuilder;
    }

}
