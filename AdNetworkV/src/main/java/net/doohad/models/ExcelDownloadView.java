package net.doohad.models;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import net.doohad.utils.Util;

@SuppressWarnings("deprecation")
public class ExcelDownloadView extends AbstractExcelView {
	@Override
	protected void buildExcelDocument(Map<String, Object> ModelMap,
			HSSFWorkbook workbook, HttpServletRequest req,
			HttpServletResponse response) throws Exception {
		String excelFilename = "ExcelData";
		ArrayList<ArrayList<String>> excelData = new ArrayList<ArrayList<String>>();
		
		Map<String, ?> fm = RequestContextUtils.getInputFlashMap(req);
		if (fm != null) {
			String tmpFilename = (String) ModelMap.get("excelFile");
			if (tmpFilename != null && !tmpFilename.isEmpty()) {
				excelFilename = tmpFilename;
			}
			
			@SuppressWarnings("unchecked")
			ArrayList<ArrayList<String>> tmpData = 
					(ArrayList<ArrayList<String>>) ModelMap.get("excelData");
			if (tmpData != null) {
				excelData = tmpData;
			}
		}
		
		response.setContentType("Application/Msexcel");
		response.setHeader("Content-Disposition", "attachment; filename="
				+ excelFilename + ".xls");

		HSSFSheet Sheet = workbook.createSheet(excelFilename);
		
		CellStyle styleTitle = workbook.createCellStyle();
		styleTitle.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		styleTitle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		HSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.WHITE.index);
		font.setFontHeightInPoints((short) 11);
		font.setFontName(Util.excelDefaultFontName(RequestContextUtils.getLocale(req)));
		styleTitle.setFont(font);
		
		CellStyle style = workbook.createCellStyle();
		
		HSSFFont normalFont = workbook.createFont();
		normalFont.setColor(HSSFColor.BLACK.index);
		normalFont.setFontHeightInPoints((short) 11);
		normalFont.setFontName(Util.excelDefaultFontName(RequestContextUtils.getLocale(req)));
		style.setFont(normalFont);
		
		int rowNo = 0, colNo = 0;
		for(ArrayList<String> row : excelData) {
			colNo = 0;
			HSSFRow Row = Sheet.createRow(rowNo++);
			for(String col : row) {
				HSSFCell cell = Row.createCell(colNo++);
				
				if (rowNo == 1) {
					cell.setCellStyle(styleTitle);
				} else {
					cell.setCellStyle(style);
				}
				
				cell.setCellValue(new HSSFRichTextString(col));
			}
		}
	}
	
	public static void makeExcelData(String excelFilename, DataSourceRequest request, DataSourceResult result,
			HttpServletRequest req, HttpServletResponse res) {
    	@SuppressWarnings("unchecked")
		ArrayList<ExcelData> list = (ArrayList<ExcelData>) result.getData(); 
    	
    	ArrayList<ArrayList<String>> excelData = new ArrayList<ArrayList<String>>();
    	ArrayList<String> excelRow = null;
    	
    	excelRow = new ArrayList<String>();
		for (DataSourceRequest.ColumnDescriptor column : request.getColumn())
		{
			excelRow.add(column.getTitle());
		}
		excelData.add(excelRow);

		for(ExcelData item : list) {
    		excelRow = new ArrayList<String>();
    		
    		for (DataSourceRequest.ColumnDescriptor column : request.getColumn())
    		{
    			excelRow.add(item.get(column.getField()));
    		}
    		
    		excelData.add(excelRow);
    	}
		
		excelFilename = (excelFilename == null || excelFilename.isEmpty()) ? "Data" : excelFilename;
    	
    	FlashMap fm = RequestContextUtils.getOutputFlashMap(req);
    	fm.put("excelData", excelData);
    	fm.put("excelFile", excelFilename);
    	fm.startExpirationPeriod(3);
    	
    	RequestContextUtils.getFlashMapManager(req).saveOutputFlashMap(fm, req, res);
	}
	
	public static void makeExcelData(String excelFilename,ArrayList<ArrayList<String>> excelData,
			HttpServletRequest req, HttpServletResponse res) {
		excelFilename = (excelFilename == null || excelFilename.isEmpty()) ? "Data" : excelFilename;
    	
    	FlashMap fm = RequestContextUtils.getOutputFlashMap(req);
    	fm.put("excelData", excelData);
    	fm.put("excelFile", excelFilename);
    	fm.startExpirationPeriod(3);
    	
    	RequestContextUtils.getFlashMapManager(req).saveOutputFlashMap(fm, req, res);
	}
}
