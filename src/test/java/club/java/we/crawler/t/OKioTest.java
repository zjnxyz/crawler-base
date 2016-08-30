package club.java.we.crawler.t;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

public class OKioTest {
	
	private final static String filename = "www.douban.com.txt";
	
	public static void main(String[] args) {
		write();
		read();
	}
	
	public static void read(){
		BufferedSource bufferedSource = null;
		File file = new File(filename);
		
		try {
			bufferedSource = Okio.buffer(Okio.source(file));
			String buffer = bufferedSource.readString(Charset.forName("utf-8"));
			System.out.println(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void write(){
		File file = new File(filename);
		boolean isCreate = false;
		 BufferedSink bSink=null;
		try {
			if(!file.exists()){
				isCreate = file.createNewFile();
			}else{
				isCreate = true;
			}
			if(isCreate){
		            bSink=Okio.buffer(Okio.sink(file));
		            bSink.writeUtf8("1");
		            bSink.writeUtf8("\n");
		            bSink.writeUtf8("this is new file!");
		            bSink.writeUtf8("\n");
		            bSink.writeString("我是每二条",Charset.forName("utf-8"));
		            bSink.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
