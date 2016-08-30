package club.java.we.crawler.config;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CrawlerPropertiesConstant {

	 /**
     * 最小空闲工作线程数量.
     * 
     * <p>
     * 默认值: 0
     * </p>
     */
    EXECUTOR_MIN_IDLE_SIZE("executor.min.idle.size", "1", int.class),
    
    /**
     * 最大工作线程数量.
     * 
     * <p>
     * 默认值: CPU的核数 * 2
     * </p>
     */
    EXECUTOR_MAX_SIZE("executor.max.size", String.valueOf(Runtime.getRuntime().availableProcessors() * 2), int.class),
    
	/**
     * 工作线程空闲时超时时间.
     * 
     * <p>
     * 单位: 毫秒.
     * 默认值: 60000毫秒.
     * </p>
     */
    EXECUTOR_MAX_IDLE_TIMEOUT_MILLISECONDS("executor.max.idle.timeout.millisecond", "60000", long.class);
    
	
	private final String key;
    
    private final String defaultValue;
    
    private final Class<?> type;
    
    /**
     * 根据属性键查找枚举.
     * 
     * @param key 属性键
     * @return 枚举值
     */
    public static CrawlerPropertiesConstant findByKey(final String key) {
        for (CrawlerPropertiesConstant each : CrawlerPropertiesConstant.values()) {
            if (each.getKey().equals(key)) {
                return each;
            }
        }
        return null;
    }
}
