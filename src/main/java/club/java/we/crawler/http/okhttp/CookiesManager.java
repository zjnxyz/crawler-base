package club.java.we.crawler.http.okhttp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import club.java.we.crawler.support.CookieStore;
import club.java.we.crawler.support.TextFileCookiesStore;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookiesManager implements CookieJar {
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    
    private final CookieStore textStore = new TextFileCookiesStore();
    @Override
    public synchronized void saveFromResponse(HttpUrl httpUrl, List<Cookie> cookies) {
        cookieStore.put(httpUrl.host(),cookies);
        textStore.writeCookies(httpUrl.host(), cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl httpUrl) {
        List<Cookie> cookies = cookieStore.get(httpUrl.host());
        if(cookies == null ){
        	cookies = textStore.readCookies(httpUrl.host());
        	cookieStore.put(httpUrl.host(), cookies);
        }
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }
    
    /**
     * 清楚cookies
     */
    public void clearCookies(){
    	cookieStore.clear();
    }
    
}