package com.fr.demo;
 
import com.fr.io.utils.ResourceIOUtils;
import com.fr.web.weblet.Formlet;
import com.fr.form.main.Form;
import javax.servlet.http.HttpServletRequest;
import com.fr.log.FineLoggerFactory;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
 
 
 
public class ReadfrmFromDatabase extends Formlet {
 
 
    public Form createForm(HttpServletRequest reportletRequest) {
 
        Form form= new Form();
        String name = reportletRequest.getParameter("reportname").toString();
        try {
        	// 定义数据连接(根据你实际数据库信息进行修改)
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/test";
            String user = "root";
            String pass = "123456";
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, user, pass);
            // 从数据库中读模板
            String sql = "select B from report where A = '" + name
                    + "'";
            Statement smt = conn.createStatement();
            ResultSet rs = smt.executeQuery(sql);
            while (rs.next()) {
                Blob blob = rs.getBlob(1);
                FineLoggerFactory.getLogger().info(blob.toString());
                InputStream ins = blob.getBinaryStream();
                form.readStream(ins);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(ResourceIOUtils.getRealPath("assets"));
        return form;
    }
 
    @Override
    public void setParameterMap(Map<String, Object> arg0) {
        // TODO Auto-generated method stub
 
    }
 
 
}