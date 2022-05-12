package com.fr.io;

import com.fr.base.background.ColorBackground;
import com.fr.base.operator.common.CommonOperator;
import com.fr.chart.activator.ChartBaseActivator;
import com.fr.config.activator.BaseDBActivator;
import com.fr.config.activator.ConfigurationActivator;
import com.fr.data.impl.config.activator.RestrictionActivator;
import com.fr.env.operator.CommonOperatorImpl;
import com.fr.general.Background;
import com.fr.io.exporter.EmbeddedTableDataExporter;
import com.fr.main.impl.WorkBook;
import com.fr.main.parameter.ReportParameterAttr;
import com.fr.module.Module;
import com.fr.module.tool.ActivatorToolBox;
import com.fr.report.ReportActivator;
import com.fr.report.module.ReportBaseActivator;
import com.fr.store.StateServerActivator;
import com.fr.workspace.simple.SimpleWork;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;

public class SetParameterWindow {
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
            String envpath= "//Applications//FineReport10_325//webapps//webroot//WEB-INF"; //工程路径
            SimpleWork.checkIn(envpath);
            module.start();
            WorkBook workbook = (WorkBook) TemplateWorkBookIO
                    .readTemplateWorkBook(
                            "//doc//Primary//Parameter//Parameter.cpt");
            // 获取WorkBook工作薄的参数属性ReportParameterAttr 
            ReportParameterAttr paraAttr = workbook.getReportParameterAttr();
            /* 参数界面的布局
             * 0 : 靠左
             * 1 ：居中
             * 2 ： 靠右
             */
            paraAttr.setAlign(1);
            /*
             * 设置参数界面背景
             * ColorBackground ：颜色背景
             * GradientBackground ：渐变色背景
             * ImageBackground ：图片背景
             * PatternBackground ：图案背景
             * TextureBackground ：纹理背景
             */
            Background background = ColorBackground.getInstance(new Color(0, 255, 255));
            paraAttr.setBackground(background);
            // 重新设置参数属性,导出最终结果 
            workbook.setReportParameterAttr(paraAttr);
            FileOutputStream outputStream = new FileOutputStream(new File(
                    "//Users//susie//Downloads//newParameter.cpt"));
            EmbeddedTableDataExporter templateExporter = new EmbeddedTableDataExporter();
            templateExporter.export(outputStream, workbook);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SimpleWork.checkOut();
        }
    }
}