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
    public CrawlerRequest bPop(String queueName) {
        try {
            LinkedBlockingQueue<CrawlerRequest> queue = getQueue(queueName);
            return queue.take();
        } catch (InterruptedException e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    @Override
    public boolean push(CrawlerRequest req) {
        try {
            LinkedBlockingQueue<CrawlerRequest> queue = getQueue(req.getQueueName());
            queue.put(req);
            return true;
        } catch (InterruptedException e) {
            log.error(e.getMessage(),e);
        }
        return false;
    }

    @Override
    public long len(String queueName) {
        LinkedBlockingQueue<CrawlerRequest> queue = getQueue(queueName);
        return queue.size();
    }

    @Override
    public boolean isProcessed(CrawlerRequest req) {
        ConcurrentSkipListSet<String> set = getProcessedSet(req.getQueueName());
        String sign = DigestUtils.md5Hex(req.getUrl());
        return set.contains(sign);
    }

    @Override
    public void addProcessed(CrawlerRequest req) {
        ConcurrentSkipListSet<String> set = getProcessedSet(req.getQueueName());
        String sign = DigestUtils.md5Hex(req.getUrl());
        set.add(sign);
    }

    @Override
    public long totalCrawled(String queueName) {
        ConcurrentSkipListSet<String> set = getProcessedSet(queueName);
        return set.size();
    }

    public LinkedBlockingQueue<CrawlerRequest> getQueue(String queueName){
        LinkedBlockingQueue<CrawlerRequest> queue = queueMap.get(queueName);
        if (queue==null){
            queue = new LinkedBlockingQueue<>();
            queueMap.put(queueName,queue);
        }
        return queue;
    }

    public ConcurrentSkipListSet<String> getProcessedSet(String queueName){
        ConcurrentSkipListSet<String> set = processedData.get(queueName);
        if (set == null){
            set = new ConcurrentSkipListSet<>();
            processedData.put(queueName,set);
        }
        return set;
    }

}
