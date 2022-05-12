package com.fr.io;

import com.fr.base.Style;
import com.fr.base.operator.common.CommonOperator;
import com.fr.chart.activator.ChartBaseActivator;
import com.fr.config.activator.BaseDBActivator;
import com.fr.config.activator.ConfigurationActivator;
import com.fr.data.impl.config.activator.RestrictionActivator;
import com.fr.env.operator.CommonOperatorImpl;
import com.fr.general.FRFont;
import com.fr.main.impl.WorkBook;
import com.fr.module.Module;
import com.fr.module.tool.ActivatorToolBox;
import com.fr.report.ReportActivator;
import com.fr.report.cell.CellElement;
import com.fr.report.elementcase.TemplateElementCase;
import com.fr.report.module.ReportBaseActivator;
import com.fr.store.StateServerActivator;
import com.fr.workspace.simple.SimpleWork;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;

public class SimpleDemo {
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
        try {
            WorkBook workbook = (WorkBook) TemplateWorkBookIO
                    .readTemplateWorkBook(
                            "//doc//Primary//Parameter//Parameter.cpt");

            // 获得WorkBook中的WorkSheet，进而修改A1单元格的前景色为红色
            TemplateElementCase report = (TemplateElementCase) workbook
                    .getReport(0);
            // getCellElement(int column, int
            // row),column和row都从0开始，因此A1单元格便是第0列第0行
            CellElement cellA1 = report.getCellElement(0, 0);
            FRFont frFont = FRFont.getInstance();
            frFont = frFont.applyForeground(Color.red);
            Style style = Style.getInstance();
            style = style.deriveFRFont(frFont);
            cellA1.setStyle(style);
            // 保存模板
            FileOutputStream outputStream = new FileOutputStream(new File(
                    "/Users//susie//Downloads//newParameter1.cpt"));
            ((WorkBook) workbook).export(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SimpleWork.checkOut();
        }
    }
}