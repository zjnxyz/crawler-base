package club.java.we.crawler.http.okhttp;


public class CookiesMgrProvider {
    private static class CookiesMgrProviderHolder{
        public static CookiesManager cookiesManager = new CookiesManager();
    }

    public static CookiesManager getInstance(){
        return CookiesMgrProviderHolder.cookiesManager;
    }
}
