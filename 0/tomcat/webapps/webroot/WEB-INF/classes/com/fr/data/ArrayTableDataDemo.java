package com.fr.data;

public class ArrayTableDataDemo extends AbstractTableData {
    /**
     * 定义程序数据集的列名与数据保存位置
     */
    private String[] columnNames;
    private Object[][] rowData;

    /**
     * 实现构建函数，在构建函数中准备数据
     */
    public ArrayTableDataDemo() {
        String[] columnNames = {"Name", "Score"};
        Object[][] datas = {{"Alex", 15},
                {"Helly", 22}, {"Bobby", 99}};
        this.columnNames = columnNames;
        this.rowData = datas;
    }

    //实现ArrayTableData的其他四个方法，因为AbstractTableData已经实现了hasRow方法

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public int getRowCount() {
        return rowData.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rowData[rowIndex][columnIndex];
    }
}