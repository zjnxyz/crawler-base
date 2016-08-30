package club.java.we.crawler.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.extern.log4j.Log4j2;

import org.apache.commons.codec.digest.DigestUtils;

@Log4j2
public class DefaultLocalQueue implements CrawlerQueue{
	
	private Map<String,LinkedBlockingQueue<CrawlerRequest>> queueMap = new HashMap<>();
    private Map<String,ConcurrentSkipListSet<String>> processedData = new HashMap<>();

    @Override
    public CrawlerRequest bPop(String crawlerName) {
        try {
            LinkedBlockingQueue<CrawlerRequest> queue = getQueue(crawlerName);
            return queue.take();
        } catch (InterruptedException e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    @Override
    public boolean push(CrawlerRequest req) {
        try {
            LinkedBlockingQueue<CrawlerRequest> queue = getQueue(req.getCrawlerName());
            queue.put(req);
            return true;
        } catch (InterruptedException e) {
            log.error(e.getMessage(),e);
        }
        return false;
    }

    @Override
    public long len(String crawlerName) {
        LinkedBlockingQueue<CrawlerRequest> queue = getQueue(crawlerName);
        return queue.size();
    }

    @Override
    public boolean isProcessed(CrawlerRequest req) {
        ConcurrentSkipListSet<String> set = getProcessedSet(req.getCrawlerName());
        String sign = DigestUtils.md5Hex(req.getUrl());
        return set.contains(sign);
    }

    @Override
    public void addProcessed(CrawlerRequest req) {
        ConcurrentSkipListSet<String> set = getProcessedSet(req.getCrawlerName());
        String sign = DigestUtils.md5Hex(req.getUrl());
        set.add(sign);
    }

    @Override
    public long totalCrawled(String crawlerName) {
        ConcurrentSkipListSet<String> set = getProcessedSet(crawlerName);
        return set.size();
    }

    public LinkedBlockingQueue<CrawlerRequest> getQueue(String crawlerName){
        LinkedBlockingQueue<CrawlerRequest> queue = queueMap.get(crawlerName);
        if (queue==null){
            queue = new LinkedBlockingQueue<>();
            queueMap.put(crawlerName,queue);
        }
        return queue;
    }

    public ConcurrentSkipListSet<String> getProcessedSet(String crawlerName){
        ConcurrentSkipListSet<String> set = processedData.get(crawlerName);
        if (set == null){
            set = new ConcurrentSkipListSet<>();
            processedData.put(crawlerName,set);
        }
        return set;
    }

}
