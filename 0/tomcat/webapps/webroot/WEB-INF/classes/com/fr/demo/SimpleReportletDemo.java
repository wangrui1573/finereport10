//程序网络报表
package com.fr.demo;

import com.fr.io.TemplateWorkBookIO;
import com.fr.main.TemplateWorkBook;
import com.fr.web.core.Reportlet;
import com.fr.web.request.ReportletRequest;
import java.util.Map;


public class SimpleReportletDemo extends Reportlet {
    public TemplateWorkBook createReport(ReportletRequest reportletrequest) {
        //新建一个WorkBook对象，用于保存最终返回的报表

        TemplateWorkBook WorkBook = null;
        try {
            //读取模板，将模板保存为workbook对象并返回  
            WorkBook = TemplateWorkBookIO.readTemplateWorkBook("//doc//Primary//Parameter//Parameter.cpt");
        } catch (Exception e) {
            e.getStackTrace();
        }
        return WorkBook;
    }

    @Override
    public void setParameterMap(Map arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTplPath(String arg0) {
        // TODO Auto-generated method stub

    }
}