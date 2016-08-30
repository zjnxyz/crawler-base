package club.java.we.crawler.core;

import java.util.Map;

import cn.wanghaomiao.xpath.model.JXDocument;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import club.java.we.crawler.constant.BodyType;
/**
 * 返回结果
 * @author riverzu
 *
 */
@Setter
@Getter
@ToString
public class CrawlerResponse {

	private BodyType bodyType;
	private CrawlerRequest request;
	private String charset;
	private String referer;
	private byte[] data;
	private String content;
	/**
	 * 这个主要用于存储上游传递的一些自定义数据
	 */
	private Map<String, String> meta;
	private String url;
	private Map<String, String> params;
	/**
	 * 网页内容真实源地址
	 */
	private String realUrl;
	
	private JXDocument document;
	
	public JXDocument document() {
		if( document == null){
			document = BodyType.TEXT.equals(bodyType) && content != null ? new JXDocument(content) : null;
		}
        return document;
    }

}
