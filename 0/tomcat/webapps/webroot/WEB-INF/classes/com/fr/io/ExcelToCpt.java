package com.fr.io;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import com.fr.base.operator.common.CommonOperator;
import com.fr.chart.activator.ChartBaseActivator;
import com.fr.config.activator.BaseDBActivator;
import com.fr.config.activator.ConfigurationActivator;
import com.fr.data.impl.config.activator.RestrictionActivator;
import com.fr.env.operator.CommonOperatorImpl;
import com.fr.main.impl.WorkBook;
import com.fr.io.importer.Excel2007ReportImporter;
import com.fr.main.TemplateWorkBook;
import com.fr.module.Module;
import com.fr.module.tool.ActivatorToolBox;
import com.fr.report.ReportActivator;
import com.fr.report.module.ReportBaseActivator;
import com.fr.store.StateServerActivator;
import com.fr.workspace.simple.SimpleWork;

public class ExcelToCpt {
    public static void main(String[] args) throws Exception {
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
        File excelFile = new File("//Users//susie//Downloads//aa.xlsx"); // 获取EXCEL文件
        FileInputStream a = new FileInputStream(excelFile);

        TemplateWorkBook tpl = new Excel2007ReportImporter().generateWorkBookByStream(a);
        OutputStream outputStream = new FileOutputStream(new File("//Users//susie//Downloads//abc.cpt")); // 转换成cpt模板
        ((WorkBook) tpl).export(outputStream);
        outputStream.close();
        module.stop();
    }
}
