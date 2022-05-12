package com.fr;

import com.fr.base.DynamicUnitList;
import com.fr.base.FRContext;
import com.fr.base.ResultFormula;
import com.fr.general.ComparatorUtils;
import com.fr.general.DeclareRecordType;
import com.fr.general.ModuleContext;
import com.fr.io.TemplateWorkBookIO;
import com.fr.io.exporter.AppExporter;
import com.fr.io.exporter.PDFExporter;
import com.fr.io.exporter.WordExporter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.main.TemplateWorkBook;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.page.PageSetProvider;
import com.fr.page.PaperSettingProvider;
import com.fr.report.cell.CellElement;
import com.fr.report.cell.DefaultTemplateCellElement;
import com.fr.report.cell.ResultCellElement;
import com.fr.report.cell.cellattr.PageExportCellElement;
import com.fr.report.core.ReportUtils;
import com.fr.report.core.block.PolyResultWorkSheet;
import com.fr.report.module.EngineModule;
import com.fr.report.worksheet.PageRWorkSheet;
import com.fr.stable.CodeUtils;
import com.fr.stable.ColumnRow;
import com.fr.stable.CommonCodeUtils;
import com.fr.stable.PageActor;
import com.fr.stable.unit.FU;
import com.fr.stable.unit.UNIT;
import com.fr.web.Browser;
import com.fr.web.core.ErrorHandlerHelper;
import com.fr.web.core.utils.ExportUtils;
import com.fr.web.utils.WebUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class myexporter extends HttpServlet {
    private static boolean offlineWriteAble = true;
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("[^{,}]*:[^{,}]*");
    private static final long serialVersionUID = 1L;

    public myexporter() {
    }

    public static void dealResponse4Export(HttpServletResponse res) {
        res.setHeader("Cache-Control", "public");
        res.setHeader("Cache-Control", "max-age=3");
        res.reset();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            ModuleContext.startModule(EngineModule.class.getName());
            dealResponse4Export(res);
            String fileName = WebUtils.getHTTPRequestParameter(req, "__filename__");
            String format = WebUtils.getHTTPRequestParameter(req, "format");
            Browser browser = Browser.resolve(req);
            fileName = browser.getEncodedFileName4Download(fileName);
            List paraMapList = new ArrayList();
            List reportPathList = new ArrayList();
            PaperSettingProvider paperSettingProvider = null;
            if (WebUtils.getHTTPRequestParameter(req, "reportlets") != null) {
                createReportsFromReportlets(WebUtils.getHTTPRequestParameter(req, "reportlets"), reportPathList, paraMapList);
                ResultWorkBook[] resultWorkBook = new ResultWorkBook[reportPathList.size()];
                PolyResultWorkSheet allInOneSheet = new PageRWorkSheet();

                for (int i = 0; i < reportPathList.size(); ++i) {
                    TemplateWorkBook workbook = TemplateWorkBookIO.readTemplateWorkBook(String.valueOf(reportPathList.get(i)));
                    Map paraMap = (Map) paraMapList.get(i);
                    resultWorkBook[i] = workbook.execute(paraMap, new PageActor());
                    if (i == 0) {
                        paperSettingProvider = (PaperSettingProvider) ReportUtils.getPaperSettingListFromWorkBook(workbook).get(0);
                    }
                }

                int length = resultWorkBook.length;
                long[] lengthx = new long[length];
                long[] lengthy = (long[]) lengthx.clone();
                if (length > 0) {
                    lengthx[0] = lengthy[0] = 0L;
                }

                for (int i = 1; i < length; ++i) {
                    long sumy = 0L;

                    for (int county = resultWorkBook[i - 1].getElementCaseReport(0).getRowCount(); county-- > 0; sumy += resultWorkBook[i - 1].getElementCaseReport(0).getRowHeight(county).getLen()) {
                        ;
                    }

                    lengthx[i] = 0L;
                    lengthy[i] = sumy + lengthy[i - 1];
                }

                ArrayList<UNIT> verticalList = new ArrayList();
                ArrayList<UNIT> horizontalList = new ArrayList();
                analyElementColumnRow(verticalList, horizontalList, resultWorkBook, lengthx, lengthy);
                allInOneSheet = setNewColRowSize(verticalList, horizontalList, allInOneSheet);
                allInOneSheet = fillBlankCell(verticalList.size(), horizontalList.size(), allInOneSheet);
                int i = 0;

                for (int len = reportPathList.size(); i < len; ++i) {
                    allInOneSheet = addElemToSheet(verticalList, horizontalList, allInOneSheet, resultWorkBook[i], lengthx[i], lengthy[i]);
                }

                if (paperSettingProvider == null) {
                    return;
                }

                doExport(req, res, format, fileName, false, browser, allInOneSheet.generateReportPageSet(paperSettingProvider));
            }
        } catch (Exception var24) {
            var24.printStackTrace();
        }

    }

    private static void doExport(HttpServletRequest req, HttpServletResponse res, String format, String fileName, boolean isEmbbed, Browser browser, PageSetProvider page) throws Exception {
        AppExporter[] exporters = new AppExporter[]{null};
        DeclareRecordType[] exportTypes = new DeclareRecordType[]{null};
        OutputStream outputStream = res.getOutputStream();
        getExporterAndTypeAndexport(req, res, format, fileName, isEmbbed, browser, exporters, exportTypes, outputStream, page);
        DeclareRecordType exportType = exportTypes[0];
        if (exportType == null) {
            ErrorHandlerHelper.getErrorHandler().error(req, res, "Cannot recognize the specifed export format:" + format + ",\nThe correct format can be PDF,Excel,Word,SVG,CSV,Text or Image.");
        } else {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException var12) {
                ;
            }

        }
    }

    private static void getExporterAndTypeAndexport(HttpServletRequest req, HttpServletResponse res, String format, String fileName, boolean isEmbbed, Browser browser, AppExporter[] exporters, DeclareRecordType[] exportTypes, OutputStream out, PageSetProvider page) throws Exception {
        if (format.equalsIgnoreCase("PDF")) {
            ExportUtils.setPDFContent(res, fileName, isEmbbed);
            PDFExporter PDFExport = new PDFExporter();
            PDFExport.export(out, page);
        } else if (format.equalsIgnoreCase("Word")) {
            ExportUtils.setWordConetent(res, fileName);
            WordExporter WordExport = new WordExporter();
            WordExport.export(out, page);
        }

    }

    public static PolyResultWorkSheet fillBlankCell(int rowCount, int colCount, PolyResultWorkSheet allInOneSheet) {
        for (int i = 0; i < rowCount; ++i) {
            for (int j = 0; j < colCount; ++j) {
                ResultCellElement ce = createDefaultCellElement(j, i);
                allInOneSheet.addCellElement(ce);
            }
        }

        allInOneSheet.setRowMappingArray(new int[0]);
        allInOneSheet.setColumnMappingArray(new int[0]);
        return allInOneSheet;
    }

    public static ResultCellElement createDefaultCellElement(int col, int row) {
        return new PageExportCellElement(new DefaultTemplateCellElement(col, row));
    }

    public static PolyResultWorkSheet addElemToSheet(ArrayList<UNIT> verticalList, ArrayList<UNIT> horizontalList, PolyResultWorkSheet page_sheet, ResultWorkBook resultWorkBook, long lengthx, long lengthy) {
        UNIT x = FU.getInstance(lengthx);
        UNIT y = FU.getInstance(lengthy);
        DynamicUnitList newRowList = page_sheet.getRowHeightList_DEC();
        DynamicUnitList newColList = page_sheet.getColumnWidthList_DEC();
        int rowCount = page_sheet.getRowCount();
        int colCount = page_sheet.getColumnCount();
        DynamicUnitList rowHeightList = resultWorkBook.getElementCaseReport(0).getRowHeightList_DEC();
        DynamicUnitList colWidthList = resultWorkBook.getElementCaseReport(0).getColumnWidthList_DEC();
        Iterator<CellElement> it = resultWorkBook.getElementCaseReport(0).cellIterator();
        HashMap<String, String> columnRowMap = new HashMap();
        HashMap formulaMap = new HashMap();

        while (it.hasNext()) {
            CellElement ce = (CellElement) it.next();
            UNIT ceX = x.add(colWidthList.getRangeValueFromZero(ce.getColumn()));
            UNIT ceWidth = colWidthList.getRangeValue(ce.getColumn(), ce.getColumn() + ce.getColumnSpan());
            UNIT ceY = y.add(rowHeightList.getRangeValueFromZero(ce.getRow()));
            UNIT ceHeight = rowHeightList.getRangeValue(ce.getRow(), ce.getRow() + ce.getRowSpan());
            int newCeCol = horizontalList.indexOf(ceX) + 1;
            int newCeColSpan = getNewSpan(newCeCol, newColList, ceWidth, colCount);
            int newCeRow = verticalList.indexOf(ceY) + 1;
            int newCeRowSpan = getNewSpan(newCeRow, newRowList, ceHeight, rowCount);
            ColumnRow oriCR = ColumnRow.valueOf(ce.getColumn(), ce.getRow());
            ColumnRow newCR = ColumnRow.valueOf(newCeCol, newCeRow);
            columnRowMap.put(oriCR.toString(), newCR.toString());
            ResultCellElement newCe = (ResultCellElement) ce.deriveCellElement(newCeCol, newCeRow, newCeColSpan, newCeRowSpan);
            page_sheet.addCellElement(newCe);
            if (ce.getValue() instanceof ResultFormula) {
                formulaMap.put(newCe, (ResultFormula) ce.getValue());
            }
        }

        modifyAllFormula(formulaMap, columnRowMap);
        return page_sheet;
    }

    public static int getNewSpan(int newCeColRow, DynamicUnitList newColRowList, UNIT ceWidthHeight, int count) {
        for (int i = newCeColRow; i < count + 1; ++i) {
            if (ComparatorUtils.equals(ceWidthHeight, newColRowList.getRangeValue(newCeColRow, i))) {
                return i - newCeColRow;
            }
        }

        return 0;
    }

    public static PolyResultWorkSheet setNewColRowSize(ArrayList<UNIT> verticalList, ArrayList<UNIT> horizontalList, PolyResultWorkSheet allInOneSheet) {
        int i;
        Object lastCoordinate;
        FU colWidth;
        for (i = 0; i < verticalList.size(); ++i) {
            lastCoordinate = i == 0 ? UNIT.ZERO : (UNIT) verticalList.get(i - 1);
            colWidth = ((UNIT) verticalList.get(i)).subtract((UNIT) lastCoordinate);
            allInOneSheet.setRowHeight(i, colWidth);
        }

        for (i = 0; i < horizontalList.size(); ++i) {
            lastCoordinate = i == 0 ? UNIT.ZERO : (UNIT) horizontalList.get(i - 1);
            colWidth = ((UNIT) horizontalList.get(i)).subtract((UNIT) lastCoordinate);
            allInOneSheet.setColumnWidth(i, colWidth);
        }

        return allInOneSheet;
    }

    public static void analyElementColumnRow(ArrayList<UNIT> verticalList, ArrayList<UNIT> horizontalList, ResultWorkBook[] resultWorkBooks, long[] lengthx, long[] lengthy) {
        int length = resultWorkBooks.length;

        for (int i = 0; i < length; ++i) {
            ResultWorkBook resultWorkBook = resultWorkBooks[i];
            UNIT y = FU.getInstance(lengthy[i]);
            int rowCount = resultWorkBook.getElementCaseReport(0).getRowCount();
            DynamicUnitList rowHeightList = resultWorkBook.getElementCaseReport(0).getRowHeightList_DEC();
            analyColumnRow(y, verticalList, rowHeightList, rowCount);
            UNIT x = FU.getInstance(lengthx[i]);
            int colCount = resultWorkBook.getElementCaseReport(0).getColumnCount();
            DynamicUnitList colWidthList = resultWorkBook.getElementCaseReport(0).getColumnWidthList_DEC();
            analyColumnRow(x, horizontalList, colWidthList, colCount);
        }

        sort(verticalList, horizontalList);
    }

    public static void analyColumnRow(UNIT startPoint, ArrayList<UNIT> verticalSet, DynamicUnitList rowHeightList, int count) {
        verticalSet.add(startPoint);

        for (int i = 0; i < count; ++i) {
            UNIT rowHeight = rowHeightList.getRangeValueFromZero(i + 1);
            UNIT rowY = rowHeight.add(startPoint);
            verticalSet.add(rowY);
        }

    }

    public static void sort(ArrayList<UNIT> verticalList, ArrayList<UNIT> horizontalList) {
        Comparator<UNIT> compare = new Comparator<UNIT>() {
            public int compare(UNIT o1, UNIT o2) {
                if (o1.subtract(o2).equal_zero()) {
                    return 0;
                }
                return o1.subtract(o2).more_than_zero() ? 1 : -1;
            }
        };
        Collections.sort(verticalList, compare);
        Collections.sort(horizontalList, compare);
    }

    private static void modifyAllFormula(HashMap<CellElement, ResultFormula> formulaMap, HashMap<String, String> columnRowMap) {
        Iterator formulaIt = formulaMap.entrySet().iterator();

        while (formulaIt.hasNext()) {
            Entry<CellElement, ResultFormula> entry = (Entry) formulaIt.next();
            CellElement ce = (CellElement) entry.getKey();
            ResultFormula formula = (ResultFormula) entry.getValue();
            String content = formula.getTransferContent();
            Iterator crIt = columnRowMap.entrySet().iterator();

            while (crIt.hasNext()) {
                Entry<String, String> crEntry = (Entry) crIt.next();
                String oriCR = (String) crEntry.getKey();
                String newCR = (String) crEntry.getValue();
                if (content.indexOf(oriCR) != -1) {
                    content = getNewFormula(content, oriCR, newCR);
                    formula.setTransferContent(content);
                    ce.setValue(formula);
                }
            }
        }

    }

    public static String getNewFormula(String formulaContent, String oriCR, String newCR) {
        String[] array = formulaContent.toUpperCase().split(oriCR);
        StringBuffer sb = new StringBuffer();
        int i = 0;

        for (int len = array.length; i < len; ++i) {
            sb.append(array[i]);
            if (formulaContent.endsWith(oriCR) || i != len - 1) {
                String nodeCR = getNewColumnRow(array[i], i + 1, array, oriCR, newCR);
                sb.append(nodeCR);
            }
        }

        return sb.toString();
    }

    private static String getNewColumnRow(String formulaPart, int nextIdx, String[] array, String oriCR, String newCR) {
        if (formulaPart.endsWith("$")) {
            return oriCR;
        } else {
            return nextIdx < array.length && startsWithDigit(array[nextIdx]) ? oriCR : newCR;
        }
    }

    private static boolean startsWithDigit(String s) {
        return Pattern.compile("^[0-9]").matcher(s.trim()).find();
    }

    public static String transferReportletsInfo(String reportletsInfo) {
        try {
            reportletsInfo = CodeUtils.cjkDecode(reportletsInfo.trim());
        } catch (Exception var2) {
            ;
        }

        String jsonString = null;
        if (reportletsInfo.length() > 0 && reportletsInfo.charAt(0) == '(') {
            jsonString = transferToJSONString(reportletsInfo);
        } else {
            jsonString = reportletsInfo;
        }

        return jsonString;
    }

    public static String transferToJSONString(String reportletsInfo) {
        if (reportletsInfo == null) {
            return null;
        } else {
            reportletsInfo = reportletsInfo.trim();
            if (reportletsInfo.length() <= 0) {
                return null;
            } else {
                reportletsInfo = reportletsInfo.substring(1, reportletsInfo.length() - 1);
                StringBuffer rpSB = new StringBuffer("[");
                Matcher matcher = KEY_VALUE_PATTERN.matcher(reportletsInfo);
                int start = 0;
                boolean var4 = false;

                while (matcher.find()) {
                    int end = matcher.start();
                    String tmpText = reportletsInfo.substring(start, end);
                    if (tmpText != null && tmpText.length() > 0) {
                        rpSB.append(tmpText);
                    }

                    start = matcher.end();
                    String tmpStr = matcher.group();
                    String[] tmpStrs = tmpStr.split(":");
                    if (tmpStrs.length != 2) {
                        rpSB.append(tmpStr);
                    } else {
                        rpSB.append(tmpStrs[0]).append(':').append(quote(tmpStrs[1]));
                    }
                }

                rpSB.append(reportletsInfo.substring(start, reportletsInfo.length()));
                rpSB.append(']');
                return rpSB.toString();
            }
        }
    }

    private static String quote(String v) {
        return "\"" + CommonCodeUtils.javascriptEncode(v) + "\"";
    }

    public static void createReportsFromReportlets(String reportletsInfo, List reportPathList, List paraMapList) {
        reportPathList.clear();
        paraMapList.clear();
        if (reportletsInfo != null) {
            String jsonString = transferReportletsInfo(reportletsInfo);
            FRContext.getLogger().info("reportletsInfo:" + jsonString);
            if (jsonString != null) {
                createFromReportlets(jsonString, reportPathList, paraMapList);
            }
        }
    }

    private static void createFromReportlets(String jsonString, List reportPathList, List paraMapList) {
        try {
            JSONArray reportlets = new JSONArray(jsonString);

            for (int i = 0; i < reportlets.length(); ++i) {
                Map paraMap = new HashMap();
                JSONObject jsonObject = reportlets.getJSONObject(i);
                Iterator keys = jsonObject.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Object value = jsonObject.get(key);
                    value = value instanceof String ? CodeUtils.decodeText(String.valueOf(value)) : value;
                    paraMap.put(key, value);
                }

                String reportletPath = (String) paraMap.get("reportlet");
                if (reportletPath != null) {
                    try {
                        reportPathList.add(reportletPath);
                        paraMapList.add(paraMap);
                    } catch (Exception var11) {
                        FRContext.getLogger().error(var11.getMessage(), var11);
                    }
                }
            }
        } catch (JSONException var12) {
            FRContext.getLogger().error(var12.getMessage(), var12);
        }

    }
}