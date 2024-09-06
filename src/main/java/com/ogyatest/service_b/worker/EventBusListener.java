package com.ogyatest.service_b.worker;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ResourceUtils;

import com.google.common.eventbus.Subscribe;
import com.ogyatest.service_b.dao.ExcelFileRepository;
import com.ogyatest.service_b.dao.PdfFileRepository;
import com.ogyatest.service_b.dto.TransactionDto;
import com.ogyatest.service_b.model.ExcelFile;
import com.ogyatest.service_b.model.PdfFile;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

public class EventBusListener {

	private static Logger log = Logger.getLogger(EventBusListener.class.getName());
	private PdfFileRepository pdfFileRepository;
	private ExcelFileRepository excelFileRepository;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

	public EventBusListener(PdfFileRepository pdfFileRepository, ExcelFileRepository excelFileRepository) {
		this.pdfFileRepository = pdfFileRepository;
		this.excelFileRepository = excelFileRepository;
	}

	@Subscribe
	public void onMessage(TransactionDto dto) {
		log.info("RECEIVE A MESSAGE ...............");
		try {
			createExcel(dto);
			createPdf(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	private void createPdf(TransactionDto dto) throws Exception {
		// 1. Reading template
		File file = ResourceUtils.getFile("classpath:template.html");
		FileReader freader = new FileReader(file);
		BufferedReader breader = new BufferedReader(freader);
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = breader.readLine()) != null) {
			sb.append(line);
		}
		breader.close();

		// 2. Replace values in template with values from dto
		String htmlString = sb.toString();
		htmlString = htmlString.replaceAll("#A#", dto.getTransactionId().toString());
		htmlString = htmlString.replaceAll("#B#", dto.getCustomerId().toString());
		htmlString = htmlString.replaceAll("#C#", dto.getCustomerName());
		htmlString = htmlString.replaceAll("#D#", dto.getListItemName());
		htmlString = htmlString.replaceAll("#E#", dto.getCustomerChange().toString());
		htmlString = htmlString.replaceAll("#F#", dto.getTotalCost().toString());
		htmlString = htmlString.replaceAll("#G#", dto.getCustomerOldBalance().toString());
		htmlString = htmlString.replaceAll("#H#", dto.getCustomerNewBalance().toString());
		htmlString = htmlString.replaceAll("\"", "'");
		log.info(htmlString);

		// 3. Creating PDF doc as bytes
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PdfRendererBuilder builder = new PdfRendererBuilder();
		builder.withHtmlContent(htmlString, "");
		builder.toStream(bos);
		builder.run();
		bos.close();

		// 4. Persists bytes of PDF to database
		String fname = dto.getCustomerId() + "_" + sdf.format(dto.getCreated()) + ".pdf";
		PdfFile o = PdfFile.builder().customerId(dto.getCustomerId()).fileName(fname).created(new Date())
				.file(bos.toByteArray()).build();
		pdfFileRepository.addNewFile(o);
	}

	@SuppressWarnings("resource")
	private void createExcel(TransactionDto dto) throws Exception {
		XSSFWorkbook wb = new XSSFWorkbook();

		Font font = wb.createFont();
		font.setBold(true);

		CellStyle headerStyle = wb.createCellStyle();
		headerStyle.setFont(font);

		CellStyle wrapStyle = wb.createCellStyle();
		wrapStyle.setWrapText(true);
		wrapStyle.setVerticalAlignment(VerticalAlignment.TOP);

		CellStyle topStyle = wb.createCellStyle();
		topStyle.setVerticalAlignment(VerticalAlignment.TOP);

		XSSFSheet ws = wb.createSheet();

		Row header = ws.createRow(0);
		Cell trxId_H = header.createCell(0);
		trxId_H.setCellValue("Transaction ID");
		trxId_H.setCellStyle(headerStyle);
		Cell cusId_H = header.createCell(1);
		cusId_H.setCellValue("Customer ID");
		cusId_H.setCellStyle(headerStyle);
		Cell cusName_H = header.createCell(2);
		cusName_H.setCellValue("Customer Name");
		cusName_H.setCellStyle(headerStyle);
		Cell lsItemName_H = header.createCell(3);
		lsItemName_H.setCellValue("List Item Name");
		lsItemName_H.setCellStyle(headerStyle);
		Cell cusChg_H = header.createCell(4);
		cusChg_H.setCellValue("Customer Change");
		cusChg_H.setCellStyle(headerStyle);
		Cell totCost_H = header.createCell(5);
		totCost_H.setCellValue("Total Cost");
		totCost_H.setCellStyle(headerStyle);
		Cell oldBal_H = header.createCell(6);
		oldBal_H.setCellValue("Customer Old Balance");
		oldBal_H.setCellStyle(headerStyle);
		Cell newBal_H = header.createCell(7);
		newBal_H.setCellValue("Customer New Balance");
		newBal_H.setCellStyle(headerStyle);

		Row row = ws.createRow(1);
		Cell trxId_V = row.createCell(0);
		trxId_V.setCellValue(dto.getTransactionId().toString());
		trxId_V.setCellStyle(topStyle);
		Cell cusId_V = row.createCell(1);
		cusId_V.setCellValue(dto.getCustomerId().toString());
		cusId_V.setCellStyle(topStyle);
		Cell cusName_V = row.createCell(2);
		cusName_V.setCellValue(dto.getCustomerName());
		cusName_V.setCellStyle(topStyle);
		Cell lsItemName_V = row.createCell(3);
		lsItemName_V.setCellValue(dto.getListItemName());
		lsItemName_V.setCellStyle(wrapStyle);
		Cell cusChg_V = row.createCell(4);
		cusChg_V.setCellValue(dto.getCustomerChange());
		cusChg_V.setCellStyle(topStyle);
		Cell totCost_V = row.createCell(5);
		totCost_V.setCellValue(dto.getTotalCost());
		totCost_V.setCellStyle(topStyle);
		Cell oldBal_V = row.createCell(6);
		oldBal_V.setCellValue(dto.getCustomerOldBalance());
		oldBal_V.setCellStyle(topStyle);
		Cell newBal_V = row.createCell(7);
		newBal_V.setCellValue(dto.getCustomerNewBalance());
		newBal_V.setCellStyle(topStyle);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		wb.write(bos);
		bos.close();

		String fname = dto.getCustomerId() + "_" + sdf.format(dto.getCreated()) + ".xlsx";
		ExcelFile o = ExcelFile.builder().customerId(dto.getCustomerId()).fileName(fname).created(new Date())
				.file(bos.toByteArray()).build();
		excelFileRepository.addNewFile(o);
	}

}
