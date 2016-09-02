package club.java.we.crawler.http.okhttp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import club.java.we.crawler.support.CookieStore;
import club.java.we.crawler.support.TextFileCookiesStore;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookiesManager implements CookieJar {
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    
    private String suffix = "";
    
    public CookiesManager(){
    	
    }
    
    public CookiesManager(String suffix){
    	this.suffix = "."+DigestUtils.md2Hex(suffix);
    }
    
    
    
    private final CookieStore textStore = new TextFileCookiesStore();
    @Override
    public synchronized void saveFromResponse(HttpUrl httpUrl, List<Cookie> cookies) {
        cookieStore.put(httpUrl.host()+suffix,cookies);
        textStore.writeCookies(httpUrl.host()+suffix, cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl httpUrl) {
        List<Cookie> cookies = cookieStore.get(httpUrl.host());
        if(cookies == null ){
        	cookies = textStore.readCookies(httpUrl.host()+suffix);
        	cookieStore.put(httpUrl.host()+suffix, cookies);
        }
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }
    
    /**
     * 清除cookies
     */
    public synchronized void clearCookies(String host){
    	cookieStore.clear();
    }
    
    /**
     * 修改cookies
     * @param host
     */
    public synchronized void modifyCookies(String host,List<Cookie> cookies){
    	
    }
    
}