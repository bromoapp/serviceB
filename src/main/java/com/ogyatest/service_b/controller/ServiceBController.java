package com.ogyatest.service_b.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ogyatest.service_b.dto.TransactionDto;
import com.ogyatest.service_b.model.ExcelFile;
import com.ogyatest.service_b.model.PdfFile;
import com.ogyatest.service_b.service.ServiceBService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class ServiceBController {

	private ServiceBService serviceB;

	@PostMapping("/store_in_file")
	public ResponseEntity<Boolean> storeAsFiles(@RequestBody TransactionDto dto) {
		serviceB.storeAsFiles(dto);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(true);
	}

	@GetMapping("/download_excel/{customerId}")
	public ResponseEntity<?> downloadExcel(@PathVariable("customerId") String customerId) {
		try {
			ExcelFile file = serviceB.getExcelFileByCustomerId(customerId);
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFileName());

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(file.getFile());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
		}
	}

	@GetMapping("/download_pdf/{customerId}")
	public ResponseEntity<?> downloadPdf(@PathVariable("customerId") String customerId) {
		try {
			PdfFile file = serviceB.getPdfFileByCustomerId(customerId);
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFileName());

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(file.getFile());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
		}
	}

}
