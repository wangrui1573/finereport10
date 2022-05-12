package com.fr.io;

import com.fr.base.Parameter;
import com.fr.base.operator.common.CommonOperator;
import com.fr.chart.activator.ChartBaseActivator;
import com.fr.config.activator.BaseDBActivator;
import com.fr.config.activator.ConfigurationActivator;
import com.fr.data.impl.config.activator.RestrictionActivator;
import com.fr.env.operator.CommonOperatorImpl;
import com.fr.general.ModuleContext;
import com.fr.io.exporter.CSVExporter;
import com.fr.io.exporter.EmbeddedTableDataExporter;
import com.fr.io.exporter.ExcelExporter;
import com.fr.io.exporter.ImageExporter;
import com.fr.io.exporter.PDFExporter;
import com.fr.io.exporter.SVGExporter;
import com.fr.io.exporter.TextExporter;
import com.fr.io.exporter.WordExporter;
import com.fr.io.exporter.excel.stream.StreamExcel2007Exporter;
import com.fr.main.impl.WorkBook;
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


public class ExportApi {
    public static void main(String[] args) {
        // 定义报表运行环境,用于执行报表
        Module module = ActivatorToolBox.simpleLink(new BaseDBActivator(),
                new ConfigurationActivator(),
                new StateServerActivator(),
                new ReportBaseActivator(),
                new RestrictionActivator(),
                new ReportActivator(),
                new ChartBaseActivator());
        SimpleWork.supply(CommonOperator.class, new CommonOperatorImpl());
        String envpath = "//Applications//FineReport10_325//webapps//webroot//WEB-INF";//工程路径
        SimpleWork.checkIn(envpath);
        module.start();
        ResultWorkBook rworkbook = null;
        try {
            // 未执行模板工作薄
            WorkBook workbook = (WorkBook) TemplateWorkBookIO
                    .readTemplateWorkBook("//doc//Primary//Parameter//Parameter.cpt");
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
            // 将未执行模板工作薄导出为内置数据集模板
            outputStream = new FileOutputStream(new File("/Users//susie//Downloads//EmbExport.cpt"));
            EmbeddedTableDataExporter templateExporter = new EmbeddedTableDataExporter();
            templateExporter.export(outputStream, workbook);
            // 将模板工作薄导出模板文件，在导出前您可以编辑导入的模板工作薄，可参考报表调用章节
            outputStream = new FileOutputStream(new File("/Users//susie//Downloads//TmpExport.cpt"));
            ((WorkBook) workbook).export(outputStream);
            // 将结果工作薄导出为2003Excel文件
            outputStream = new FileOutputStream(new File("/Users//susie//Downloads//ExcelExport.xls"));
            ExcelExporter ExcelExport = new ExcelExporter();
            ExcelExport.export(outputStream, workbook.execute(parameterMap, new WriteActor()));
            // 将结果工作薄导出为Excel文件
            outputStream = new FileOutputStream(new File("/Users//susie//Downloads//ExcelExport.xlsx"));
            StreamExcel2007Exporter ExcelExport1 = new StreamExcel2007Exporter();
            ExcelExport1.export(outputStream, workbook.execute(parameterMap, new WriteActor()));
            // 将结果工作薄导出为Word文件
            outputStream = new FileOutputStream(new File("/Users//susie//Downloads//WordExport.doc"));
            WordExporter WordExport = new WordExporter();
            WordExport.export(outputStream, workbook.execute(parameterMap, new WriteActor()));
            // 将结果工作薄导出为Pdf文件
            outputStream = new FileOutputStream(new File("/Users//susie//Downloads//PdfExport.pdf"));
            PDFExporter PdfExport = new PDFExporter();
            PdfExport.export(outputStream, workbook.execute(parameterMap, new WriteActor()));
            // 将结果工作薄导出为Txt文件（txt文件本身不支持表格、图表等，被导出模板一般为明细表）
            outputStream = new FileOutputStream(new File("/Users//susie//Downloads//TxtExport.txt"));
            TextExporter TxtExport = new TextExporter();
            TxtExport.export(outputStream, workbook.execute(parameterMap, new WriteActor()));
            // 将结果工作薄导出为Csv文件
            outputStream = new FileOutputStream(new File("/Users//susie//Downloads//CsvExport.csv"));
            CSVExporter CsvExport = new CSVExporter();
            CsvExport.export(outputStream, workbook.execute(parameterMap, new WriteActor()));
            //将结果工作薄导出为SVG文件
            outputStream = new FileOutputStream(new File("/Users//susie//Downloads//SvgExport.svg"));
            SVGExporter SvgExport = new SVGExporter();
            SvgExport.export(outputStream, workbook.execute(parameterMap, new WriteActor()));
            //将结果工作薄导出为image文件
            outputStream = new FileOutputStream(new File("/Users//susie//Downloads//PngExport.png"));
            ImageExporter ImageExport = new ImageExporter();
            ImageExport.export(outputStream, workbook.execute(parameterMap, new WriteActor()));
            outputStream.close();
            module.stop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SimpleWork.checkOut();
        }
    }
}