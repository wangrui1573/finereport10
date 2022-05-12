package com.fr.io;
import com.fr.config.activator.BaseDBActivator;
import com.fr.config.activator.ConfigurationActivator;
import com.fr.data.impl.config.activator.RestrictionActivator;
import com.fr.io.exporter.ExcelExporter;
import com.fr.main.TemplateWorkBook;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.module.Module;
import com.fr.module.tool.ActivatorToolBox;
import com.fr.report.ReportActivator;
import com.fr.report.module.ReportBaseActivator;
import com.fr.stable.StableUtils;
import com.fr.stable.WriteActor;
import com.fr.store.StateServerActivator;
import com.fr.workspace.simple.SimpleWork;
import com.fr.chart.activator.ChartBaseActivator;
import com.fr.base.operator.common.CommonOperator;
import com.fr.env.operator.CommonOperatorImpl;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;


public class ExportBatch {
    public static void main(String[] args) {
        try {
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
            // 读取环境下的模板文件
            TemplateWorkBook workbook = TemplateWorkBookIO.readTemplateWorkBook(
                    "//doc//Primary//Parameter//Parameter.cpt");
            // 读取用于保存的参数值的txt文件
            File parafile = new File(envpath + "//para.txt");
            FileInputStream fileinputstream;
            fileinputstream = new FileInputStream(parafile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileinputstream));
            // 定义保存参数的map，用于执行报表
            java.util.Map paramap = new java.util.HashMap();
            /*
             * 遍历参数值所在txt文件，txt文件中参数保存形式为 para1,para2 江苏,陈羽 江苏,安娜 首先取出第一行保存参数名称
             * 遍历每个参数组合，如para1=江苏、para2=陈羽，根据参数执行模板，并将结果导出excel excel文件名为名称+导出编号
             */
            // 读第一行，保存参数名称
            String lineText = bufferedReader.readLine();
            lineText = lineText.trim();
            String[] paraname = StableUtils.splitString(lineText, ",");
            System.out.println(Arrays.toString(paraname));
            // 遍历每个参数组合，执行模板，导出结果
            int number = 0;
            while ((lineText = bufferedReader.readLine()) != null) {
                lineText = lineText.trim();
                String[] paravalue = StableUtils.splitString(lineText, ",");
                for (int j = 0; j < paravalue.length; j++) {
                    paramap.put(paraname[j], paravalue[j]);
                }
                ResultWorkBook result = workbook.execute(paramap,new WriteActor());
                OutputStream outputstream = new FileOutputStream(new File("//Users//susie//Downloads//ExportEg" + number + ".xls"));
                ExcelExporter excelexporter = new ExcelExporter();
                excelexporter.export(outputstream, result);
                // 最后要清空一下参数map，用于下次计算
                paramap.clear();
                number++;
                outputstream.close();
            }
            module.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}