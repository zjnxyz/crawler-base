package club.java.we.crawler.http.okhttp;

import okhttp3.OkHttpClient;

public class OkHttpClientBuilderProvider {
    private static class OkHttpclientBuilderProviderHolder {
        public static OkHttpClientBuilderBox okHttpClientBuilderBox = new OkHttpClientBuilderBox();
    }
    public static OkHttpClient.Builder getInstance(){
        return OkHttpclientBuilderProviderHolder.okHttpClientBuilderBox.instance();
    }
}
