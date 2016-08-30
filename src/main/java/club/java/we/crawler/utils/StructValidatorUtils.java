package club.java.we.crawler.utils;

import static com.google.common.base.Preconditions.*;
import lombok.extern.log4j.Log4j2;
@Log4j2
public class StructValidatorUtils {

	    public static boolean validateAnno(Object object){
//	        for (Field field:object.getClass().getDeclaredFields()){
//	            NotNull notNullCheck = field.getAnnotation(NotNull.class);
//	            if (notNullCheck!=null){
//	                try {
//	                    Object val = FieldUtils.readField(field,object,true);
//	                    if (StringUtils.isBlank(String.valueOf(val))){
//	                        logger.error("Field={}.{} can not be null!",object.getClass().getSimpleName(),field.getName());
//	                        return false;
//	                    }
//	                } catch (IllegalAccessException e) {
//	                    logger.error(e.getMessage(),e);
//	                }
//	            }
//	        }
	        return true;
	    }

	    public static boolean validateAllowRules(String[] rules,String target){
	    	log.trace("AllowRules execute");
	        if (rules == null || rules.length == 0){
	            return true;
	        }
	        checkNotNull(target, "rule target can not be null");
	        for (String rule:rules){
	            if (target.matches(rule)){
	                return true;
	            }
	        }
	        return false;
	    }

	    public static boolean validateDenyRules(String[] rules,String target){
	    	log.trace("denyrules execute");
	    	if (rules == null || rules.length == 0){
	            return false;
	        }
	        checkNotNull(target, "rule target can not be null");
	        for (String rule:rules){
	            if (target.matches(rule)){
	                return true;
	            }
	        }
	        return false;
	    }

}
