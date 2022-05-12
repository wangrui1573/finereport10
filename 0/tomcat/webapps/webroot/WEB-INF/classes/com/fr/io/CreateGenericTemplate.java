package com.fr.io;
import com.fr.base.TableData;
import com.fr.base.operator.common.CommonOperator;
import com.fr.chart.activator.ChartBaseActivator;
import com.fr.config.activator.BaseDBActivator;
import com.fr.config.activator.ConfigurationActivator;
import com.fr.data.impl.DBTableData;
import com.fr.data.impl.NameDatabaseConnection;
import com.fr.data.impl.config.activator.RestrictionActivator;
import com.fr.env.operator.CommonOperatorImpl;
import com.fr.general.data.TableDataColumn;
import com.fr.module.Module;
import com.fr.module.tool.ActivatorToolBox;
import com.fr.report.ReportActivator;
import com.fr.report.cell.DefaultTemplateCellElement;
import com.fr.report.cell.TemplateCellElement;
import com.fr.report.cell.cellattr.core.group.DSColumn;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import com.fr.main.impl.WorkBook;
import com.fr.report.module.ReportBaseActivator;
import com.fr.report.worksheet.WorkSheet;
import com.fr.store.StateServerActivator;
import com.fr.workspace.simple.SimpleWork;

public class CreateGenericTemplate {
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
        WorkBook wb = new WorkBook();
        //添加新的模板数据集
        TableData td = genericTableData("FRDemo", "SELECT * FROM Equipment");
        wb.putTableData("公司信息", td);
        //创建第一个report 也就是sheet页
        WorkSheet report = new WorkSheet();
        wb.addReport(report);
        //数据库暂时先不读，弄点假数据
        HashMap<Integer, String> map = new HashMap<>();
        map.put(0, "Company");
        map.put(1, "Tel");
        //添加列头
        DefaultTemplateCellElement title = genericCell(0, 0, null, "这是标题");
        title.setRowSpan(2);
        title.setColumnSpan(2);
        report.addCellElement(title);
        //添加数据列
        for (Integer key : map.keySet()) {
            TemplateCellElement cellHeaer = genericCell(2, key, null, map.get(key));
            report.addCellElement(cellHeaer);
            TemplateCellElement cell = genericCell(3, key, "公司信息", map.get(key));
            report.addCellElement(cell);
        }
        //导出模板
        FileOutputStream outputStream = new FileOutputStream(new File("//Users//susie//Downloads//company.cpt"));
        wb.export(outputStream);
        outputStream.close();
        module.stop();
        System.out.println("finished");
    }
    /*
     *生成TableData
     */
    private static TableData genericTableData(String conString, String sqlQuery) {
        NameDatabaseConnection database = new NameDatabaseConnection(conString);
        TableData td = new DBTableData(database, sqlQuery);
        return td;
    }
    /**
     * 添加单元格
     * row 行号
     * column 列号
     * dsName 数据集名称 如 ds1
     * 数据列名称如 ds1中的id列，则输入 id
     */
    private static DefaultTemplateCellElement genericCell(int row, int column, String dsName, String columnName) {
        DefaultTemplateCellElement dtCell = new DefaultTemplateCellElement(row, column);
        if (dsName != null) {
            DSColumn dsColumn = new DSColumn();
            dsColumn.setDSName(dsName);
            dsColumn.setColumn(TableDataColumn.createColumn(columnName));
            dtCell.setValue(dsColumn);
        } else {
            dtCell.setValue(columnName);
        }
        return dtCell;
    }
}
//自动执行存储过程，从里面获取结果集结的部分等研究一下jdbc再补