package com.fr.demo;

import com.fr.main.TemplateWorkBook;
import com.fr.main.impl.WorkBook;
import com.fr.web.core.Reportlet;
import com.fr.web.request.ReportletRequest;
import com.fr.log.FineLoggerFactory;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;



public class ReadFromDatabase extends Reportlet {
    public TemplateWorkBook createReport(ReportletRequest reportletRequest) {

        WorkBook workbook = new WorkBook();
        String name = reportletRequest.getParameter("cptname").toString();
        try {
            // 定义数据连接(根据你实际数据库信息进行修改)
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://review.finedevelop.com:3306/susie";
            String user = "root";
            String pass = "ilovejava";
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, user, pass);
            // 从数据库中读模板
            String sql = "select cpt from report where cptname = '" + name
                    + "'";
            Statement smt = conn.createStatement();
            ResultSet rs = smt.executeQuery(sql);
            while (rs.next()) {
                Blob blob = rs.getBlob(1); // 取第一列的值，即cpt列
                FineLoggerFactory.getLogger().info(blob.toString());
                InputStream ins = blob.getBinaryStream();
                workbook.readStream(ins);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workbook;
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