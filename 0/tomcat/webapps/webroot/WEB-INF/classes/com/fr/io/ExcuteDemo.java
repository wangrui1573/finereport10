package com.fr.io;
import com.fr.base.operator.common.CommonOperator;
import com.fr.chart.activator.ChartBaseActivator;
import com.fr.config.activator.BaseDBActivator;
import com.fr.config.activator.ConfigurationActivator;
import com.fr.data.impl.config.activator.RestrictionActivator;
import com.fr.env.operator.CommonOperatorImpl;
import com.fr.io.exporter.ExcelExporter;
import com.fr.main.TemplateWorkBook;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.module.Module;
import com.fr.module.tool.ActivatorToolBox;
import com.fr.report.ReportActivator;
import com.fr.report.module.ReportBaseActivator;
import com.fr.stable.WriteActor;
import com.fr.store.StateServerActivator;
import com.fr.workspace.simple.SimpleWork;
import java.io.File;
import java.io.FileOutputStream;


public class ExcuteDemo {
    public static void main(String[] args) {
        try {
            // 首先需要定义执行所在的环境，这样才能正确读取数据库信息
            // 定义报表运行环境,用于执行报表
            Module module = ActivatorToolBox.simpleLink(new BaseDBActivator(),
                    new ConfigurationActivator(),
                    new StateServerActivator(),
                    new ReportBaseActivator(),
                    new RestrictionActivator(),
                    new ReportActivator(),
                    new ChartBaseActivator());
            SimpleWork.supply(CommonOperator.class, new CommonOperatorImpl());
            String envpath= "//Applications//FineReport10_325//webapps//webroot//WEB-INF"; //工程路径
            SimpleWork.checkIn(envpath);
            module.start();
            // 读取模板
            TemplateWorkBook workbook = TemplateWorkBookIO.readTemplateWorkBook("//doc//Primary//Parameter//Parameter.cpt");
            /*
             * 生成参数map，注入参数与对应的值，用于执行报表 该模板中只有一个参数地区，给其赋值华北
             * 若参数在发送请求时传过来，可以通过req.getParameter(name)获得
             * 获得的参数put进map中，paraMap.put(paraname,paravalue)
             */
            java.util.Map paraMap = new java.util.HashMap();
            paraMap.put("地区", "华北");
            // 使用paraMap执行生成结果
            ResultWorkBook result = workbook.execute(paraMap, new WriteActor());
            // 使用结果如导出至excel
            FileOutputStream outputStream = new FileOutputStream(new File(
                    "//Users//susie//Downloads//Parameter.xls"));
            ExcelExporter excelExporter = new ExcelExporter();
            excelExporter.export(outputStream,result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SimpleWork.checkOut();
        }
    }
}