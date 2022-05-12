package com.fr.io;

import com.fr.base.operator.common.CommonOperator;
import com.fr.chart.activator.ChartBaseActivator;
import com.fr.cluster.engine.activator.standalone.StandaloneModeActivator;
import com.fr.config.activator.BaseDBActivator;
import com.fr.config.activator.ConfigurationActivator;
import com.fr.env.operator.CommonOperatorImpl;
import com.fr.form.export.FormToWBExecutor;
import com.fr.general.I18nResource;
import com.fr.io.TemplateWorkBookIO;
import com.fr.io.exporter.ImageExporter;
import com.fr.io.exporter.PDFExporter;
import com.fr.io.exporter.excel.stream.StreamExcel2007Exporter;
import com.fr.main.impl.WorkBook;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.module.Module;
import com.fr.module.tool.ActivatorToolBox;
import com.fr.report.ReportActivator;
import com.fr.report.RestrictionActivator;
import com.fr.report.module.ReportBaseActivator;
import com.fr.report.write.WriteActivator;
import com.fr.scheduler.SchedulerActivator;
import com.fr.stable.WriteActor;
import com.fr.store.StateServerActivator;
import com.fr.workspace.simple.SimpleWork;

import java.io.File;
import java.io.FileOutputStream;

public class ExportApi_frm { 
    public static void main(String[] args) {
        // 定义报表运行环境,用于执行报表
        Module module = ActivatorToolBox.simpleLink(new BaseDBActivator(),
                new ConfigurationActivator(),
                new StandaloneModeActivator(),
                new StateServerActivator(),
                new SchedulerActivator(),
                new ReportBaseActivator(),
                new RestrictionActivator(),
                new ReportActivator(),
                new WriteActivator(),
                new ChartBaseActivator());
        SimpleWork.supply(CommonOperator.class, new CommonOperatorImpl());
        String envpath = "C:\\Users\\hipsh\\Desktop\\apache-tomcat-8.5.38-10.0\\webapps\\webroot\\WEB-INF\\";//工程路径
        SimpleWork.checkIn(envpath);
        I18nResource.getInstance();
        module.start();
        try {
  
            java.util.Map parameterMap = new java.util.HashMap();
            parameterMap.put("aa", "1");
            ResultWorkBook re =  FormToWBExecutor.executeForm("test.frm",parameterMap);
            // 定义输出流
            FileOutputStream outputStream;
            // 将结果工作薄导出为Excel文件
            outputStream = new FileOutputStream(new File("C:\\FTPServer\\1528.xlsx"));
            StreamExcel2007Exporter ExcelExport1 = new StreamExcel2007Exporter();
             ExcelExport1.export(outputStream,re);
            // 将结果工作薄导出为Pdf文件
            outputStream = new FileOutputStream(new File("C:\\FTPServer\\PdfExport.pdf"));
            PDFExporter PdfExport = new PDFExporter();
            PdfExport.export(outputStream, re);
            //将结果工作薄导出为image文件
            outputStream = new FileOutputStream(new File("C:\\FTPServer\\PngExport.png"));
            ImageExporter ImageExport = new ImageExporter();
            ImageExport.export(outputStream, re);
            outputStream.close();
            module.stop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SimpleWork.checkOut();
        }
    }
}