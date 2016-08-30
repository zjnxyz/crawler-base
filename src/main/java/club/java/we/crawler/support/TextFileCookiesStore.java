package club.java.we.crawler.support;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import okhttp3.Cookie;
import okhttp3.Cookie.Builder;
import okhttp3.internal.http.HttpDate;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * 将cookies一行行写入文件
 * @author riverzu
 *
 */
public class TextFileCookiesStore implements CookieStore{
	
	/** cookie的分隔符 */
	private final static String LINE_SPLIT_SYMBOL="\r\n";
	
	private Map<String, File> fileCache = Maps.newConcurrentMap();

	public void writeCookies(String host,List<Cookie> cookies){
		StringBuffer buffer = new StringBuffer();
		for(Cookie cookie:cookies){
			buffer.append(cookie.toString()).append(LINE_SPLIT_SYMBOL);
		}
		write(host, buffer.toString());
	}
	
	public List<Cookie> readCookies(String host){
		String cookieStr = read(host);
		if(Strings.isNullOrEmpty(cookieStr)){
			return new ArrayList<Cookie>(0);
		}
		List<String> cookieList = Splitter.on(LINE_SPLIT_SYMBOL).omitEmptyStrings().trimResults().splitToList(cookieStr);
		List<Cookie> cookies = new ArrayList<Cookie>(cookieList.size());
		Cookie.Builder builder;
		for(String cstr:cookieList){
			 builder = new Builder();
			 List<String> elements = Splitter.on(";").omitEmptyStrings().trimResults().splitToList(cstr);
			 for(String element:elements){
				List<String> nv= Splitter.on("=").omitEmptyStrings().trimResults().splitToList(element);
				if(nv.size() == 1){
					String name = nv.get(0);
					if(CookieStore.SECURE.equals(name)){
						builder.secure();
					}else if(CookieStore.HTTPONLY.equals(name)){
						builder.httpOnly();
					}
				}else if(nv.size() >= 2){
					String name = nv.get(0);
					String value = nv.get(1);
					if(nv.size()>2){
						 value = StringUtils.join(nv.toArray(), "=", 1, nv.size());
					}
					if(CookieStore.EXPIRES.equals(name)){
						builder.expiresAt(HttpDate.parse(value).getTime());
					}else if(CookieStore.DOMAIN.equals(name)){
						builder.domain(value);
					}else if(CookieStore.PATH.equals(name)){
						builder.path(value);
					}else{
						builder.name(name);
						builder.value(value);
					}
				}
			 }
			 cookies.add(builder.build());
		}
		return cookies;
	}
	
	private void write(String host,String text){
		File file = getFile(host);
		 BufferedSink bSink=null;
		try {
			  bSink=Okio.buffer(Okio.sink(file));
		      bSink.writeUtf8(text);
		      bSink.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String read(String host){
		BufferedSource bufferedSource = null;
		File file = getFile(host);
		try {
			
			bufferedSource = Okio.buffer(Okio.source(file));
			String buffer = bufferedSource.readString(Charset.forName("utf-8"));
			return buffer;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(bufferedSource != null){
				try {
					bufferedSource.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	private File getFile(String host){
		File file = fileCache.get(host);
		if(file == null){
			 file = new File(host);
			 if(!file.exists()){
				 try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				 fileCache.put(host, file);
			 }
		}
		return file;
	}
}
