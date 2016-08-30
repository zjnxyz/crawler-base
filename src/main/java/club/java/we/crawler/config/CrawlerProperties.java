package club.java.we.crawler.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import club.java.we.crawler.utils.StringUtils;

import com.google.common.base.Joiner;


public class CrawlerProperties {
	
	private final Properties props;

	public CrawlerProperties(Properties props) {
		this.props = props;
		validate();
	}
	
	private void validate() {
        Set<String> propertyNames = props.stringPropertyNames();
        Collection<String> errorMessages = new ArrayList<>(propertyNames.size());
        for (String each : propertyNames) {
        	CrawlerPropertiesConstant crawlerPropertiesConstant = CrawlerPropertiesConstant.findByKey(each);
            if (null == crawlerPropertiesConstant) {
                continue;
            }
            Class<?> type = crawlerPropertiesConstant.getType();
            String value = props.getProperty(each);
            if (type == boolean.class && !StringUtils.isBooleanValue(value)) {
                errorMessages.add(getErrorMessage(crawlerPropertiesConstant, value));
                continue;
            }
            if (type == int.class && !StringUtils.isIntValue(value)) {
                errorMessages.add(getErrorMessage(crawlerPropertiesConstant, value));
                continue;
            }
            if (type == long.class && !StringUtils.isLongValue(value)) {
                errorMessages.add(getErrorMessage(crawlerPropertiesConstant, value));
            }
        }
        if (!errorMessages.isEmpty()) {
            throw new IllegalArgumentException(Joiner.on(" ").join(errorMessages));
        }
    }
	
	
	private String getErrorMessage(final CrawlerPropertiesConstant crawlerPropertiesConstant, final String invalidValue) {
        return String.format("Value '%s' of '%s' cannot convert to type '%s'.", invalidValue, crawlerPropertiesConstant.getKey(), crawlerPropertiesConstant.getType().getName());
    }
	
	
	 /**
     * 获取配置项属性值.
     * 
     * @param crawlerPropertiesConstant 配置项常量
     * @param <T> 返回值类型
     * @return 配置项属性值
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(final CrawlerPropertiesConstant crawlerPropertiesConstant) {
        String result = props.getProperty(crawlerPropertiesConstant.getKey(), crawlerPropertiesConstant.getDefaultValue());
        if (boolean.class == crawlerPropertiesConstant.getType()) {
            return (T) Boolean.valueOf(result);
        }
        if (int.class == crawlerPropertiesConstant.getType()) {
            return (T) Integer.valueOf(result);
        }
        if (long.class == crawlerPropertiesConstant.getType()) {
            return (T) Long.valueOf(result);
        }
        return (T) result;
    }
	

}
