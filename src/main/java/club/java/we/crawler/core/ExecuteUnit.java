package club.java.we.crawler.core;


/**
 * 执行单元.
 * 
 * @param <I> 入参类型
 * @param <O> 出参类型
 * 
 */
public interface ExecuteUnit<I> {
    
    /**
     * 执行任务.
     * 
     * @param input 输入待处理数据
     * @return 返回处理结果
     * @throws Exception 执行期异常
     */
    void execute(I input) throws Exception;
}
