package club.java.we.crawler.annotation.process;

import club.java.we.crawler.annotation.Header;
import club.java.we.crawler.config.HeaderDefination;
import club.java.we.crawler.core.AbstractBaseCrawler;

public class HeaderAnnotationProcess {
	
	
	public static void process(Class<? extends AbstractBaseCrawler> clazz, HeaderDefination headerDefination){
		Header header = clazz.getAnnotation(Header.class);
		if(header != null){
			headerDefination.setAccept(header.accept());
			headerDefination.setAcceptEncoding(header.acceptEncoding());
			headerDefination.setAcceptLanguage(header.acceptLanguage());
			headerDefination.setConnection(header.connection());
			headerDefination.setUpgradeInsecureRequests(header.upgradeInsecureRequests());
		}
	}

}
