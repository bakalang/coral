package com.dosomething.api;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.dosomething.util.LogUtils;

public class JsoupConnect {
	
	public static Document jsoupRetry(String url) throws IOException, InterruptedException {

        Document doc = null;
        int i = 0;
        while(i < 3){
            Connection connection = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .timeout(10000);
            Connection.Response rsp = connection.execute();
            LogUtils.coral.info(">> "+url+" (retry "+i+"), statusCode="+rsp.statusCode());
            if(rsp.statusCode() == 200) {
                doc = connection.get();
                break;
            }else{
            	Thread.sleep(500);
                i++;
            }
        }
        return doc;
    }
}
