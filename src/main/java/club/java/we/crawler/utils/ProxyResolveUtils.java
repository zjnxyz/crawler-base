package club.java.we.crawler.utils;

import java.net.InetSocketAddress;
import java.net.Proxy;

import lombok.extern.log4j.Log4j2;

import com.google.common.base.Strings;
/**
 * 代理解析帮助类
 * @author riverzu
 *
 */
@Log4j2
public class ProxyResolveUtils {
	
	public static Proxy resolveProxy(String proxyStr){
        Proxy proxy = null;
        if (Strings.isNullOrEmpty(proxyStr)){
            return null;
        }
        if (proxyStr.matches("(http|https|socket)://([0-9a-zA-Z]+\\.?)+:\\d+")){
            String[] pies = proxyStr.split(":");
            String scheme = pies[0];
            int port = Integer.parseInt(pies[2]);
            String host = pies[1].substring(2);
            if (scheme.equals("socket")){
                proxy = new Proxy(Proxy.Type.SOCKS,new InetSocketAddress(host,port));
            }else {
                proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(host,port));
            }
        }else {
        	log.error("proxy must like ‘http|https|socket://host:port’");
        }
        return proxy;
    }

}
