package com.fr.log;

/**
 * 后台输出log信息-http://help.finereport.com/doc-view-746.html
 */
public class LogApi {
    public static void main(String[] args) {
        FineLoggerFactory.getLogger().info( "This is level info");    //需要服务器log级别为info时才会显示
        FineLoggerFactory.getLogger().warn("This is level warning");   //需要服务器log级别为info、warning时才会显示
        FineLoggerFactory.getLogger().error("This is level error");   //需要服务器log级别为info、warning、error时才会显示,10.0取消了server级别日志记录
    }
}