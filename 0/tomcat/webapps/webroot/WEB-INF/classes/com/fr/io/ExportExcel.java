package com.fr.io;

import java.io.File;
import java.io.FileOutputStream;
import com.fr.base.operator.common.CommonOperator;
import com.fr.chart.activator.ChartBaseActivator;
import com.fr.config.activator.BaseDBActivator;
import com.fr.config.activator.ConfigurationActivator;
import com.fr.data.impl.config.activator.RestrictionActivator;
import com.fr.env.operator.CommonOperatorImpl;
import com.fr.base.Parameter;
import com.fr.io.exporter.ExcelExporter;
import com.fr.io.exporter.LargeDataPageExcelExporter;
import com.fr.io.exporter.PageExcel2007Exporter;
import com.fr.io.exporter.PageExcelExporter;
import com.fr.io.exporter.PageToSheetExcel2007Exporter;
import com.fr.io.exporter.PageToSheetExcelExporter;
import com.fr.io.exporter.excel.stream.StreamExcel2007Exporter;
import com.fr.main.TemplateWorkBook;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.module.Module;
import com.fr.module.tool.ActivatorToolBox;
import com.fr.report.ReportActivator;
import com.fr.report.core.ReportUtils;
import com.fr.report.module.ReportBaseActivator;
import com.fr.stable.WriteActor;
import com.fr.store.StateServerActivator;
import com.fr.workspace.simple.SimpleWork;


public class ExportExcel {
    public static void main(String[] args) {
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
        ResultWorkBook rworkbook = null;
        try {
            // 未执行模板工作薄
            TemplateWorkBook workbook = TemplateWorkBookIO.readTemplateWorkBook("//doc//Primary//Parameter//Parameter.cpt");
            // 获取报表参数并设置值，导出内置数据集时数据集会根据参数值查询出结果从而转为内置数据集
            Parameter[] parameters = workbook.getParameters();
            parameters[0].setValue("华东");
            // 定义parametermap用于执行报表，将执行后的结果工作薄保存为rworkBook
            java.util.Map parameterMap = new java.util.HashMap();
            for (int i = 0; i < parameters.length; i++) {
                parameterMap.put(parameters[i].getName(), parameters[i]
                        .getValue());
            }
            // 定义输出流
            FileOutputStream outputStream;

            //原样导出excel2003
            outputStream = new FileOutputStream(new File("//Users//susie//Downloads//ExcelExport.xls"));
            ExcelExporter excel = new ExcelExporter();
            excel.setVersion(true);
            excel.export(outputStream, workbook.execute(parameterMap,new WriteActor()));

            //原样导出excel2007
            outputStream = new FileOutputStream(new File("//Users//susie//Downloads//ExcelExport.xlsx"));
            StreamExcel2007Exporter excel1 = new StreamExcel2007Exporter();
            excel.export(outputStream, workbook.execute(parameterMap,new WriteActor()));

            //分页导出excel2003
            outputStream = new FileOutputStream(new File("//Users//susie//Downloads//PageExcelExport.xls"));
            PageExcelExporter page = new PageExcelExporter(ReportUtils.getPaperSettingListFromWorkBook(workbook.execute(parameterMap,new WriteActor())));
            page.setVersion(true);
            page.export(outputStream, workbook.execute(parameterMap,new WriteActor()));

            //分页导出excel2007
            outputStream = new FileOutputStream(new File("//Users//susie//Downloads//PageExcelExport.xlsx"));
            PageExcel2007Exporter page1 = new PageExcel2007Exporter(ReportUtils.getPaperSettingListFromWorkBook(rworkbook));
            page1.export(outputStream, workbook.execute(parameterMap,new WriteActor()));

            //分页分sheet导出excel2003
            outputStream = new FileOutputStream(new File("//Users//susie//Downloads//PageSheetExcelExport.xls"));
            PageToSheetExcelExporter sheet = new PageToSheetExcelExporter(ReportUtils.getPaperSettingListFromWorkBook(workbook.execute(parameterMap,new WriteActor())));
            sheet.setVersion(true);
            sheet.export(outputStream, workbook.execute(parameterMap,new WriteActor()));

            //分页分sheet导出excel2007
            outputStream = new FileOutputStream(new File("//Users//susie//Downloads//PageSheetExcelExport.xlsx"));
            PageToSheetExcel2007Exporter sheet1 = new PageToSheetExcel2007Exporter(ReportUtils.getPaperSettingListFromWorkBook(rworkbook));
            sheet1.export(outputStream, workbook.execute(parameterMap,new WriteActor()));

            //大数据量导出
            outputStream = new FileOutputStream(new File("//Users//susie//Downloads//LargeExcelExport.zip"));
            LargeDataPageExcelExporter large = new LargeDataPageExcelExporter(ReportUtils.getPaperSettingListFromWorkBook(workbook.execute(parameterMap,new WriteActor())), true);
            //导出2007版outputStream = new FileOutputStream(new File("//Users//susie//Downloads//LargeExcelExport.xlsx")); excel LargeDataPageExcel2007Exporter large = new LargeDataPageExcel2007Exporter(ReportUtils.getPaperSettingListFromWorkBook(rworkbook), true);
            large.export(outputStream, workbook.execute(parameterMap,new WriteActor()));

            outputStream.close();
            module.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}