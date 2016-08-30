package club.java.we.crawler.http;

public enum SeimiAgentContentType {
    /**
     * 向SeimiAgent请求返回内容为HTML
     */
    HTML(1,"html"),
    /**
     * 向SeimiAgent请求返回内容为图片，实际图片格式为png
     */
    IMG(2,"img"),
    /**
     * 向SeimiAgent请求返回内容为PDF
     */
    PDF(3,"pdf");
    private int val;
    private String seimiAgentType;
    SeimiAgentContentType(int val,String typeStr){
        this.val = val;
        this.seimiAgentType = typeStr;
    }
    public int val(){
        return this.val;
    }

    public String typeVal(){
        return seimiAgentType;
    }
}

