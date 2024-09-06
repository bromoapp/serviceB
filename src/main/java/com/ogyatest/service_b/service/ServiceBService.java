package com.ogyatest.service_b.service;

import java.util.UUID;
import java.util.logging.Logger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.ogyatest.service_b.dao.ExcelFileRepository;
import com.ogyatest.service_b.dao.PdfFileRepository;
import com.ogyatest.service_b.dto.TransactionDto;
import com.ogyatest.service_b.model.ExcelFile;
import com.ogyatest.service_b.model.PdfFile;
import com.ogyatest.service_b.worker.EventBusListener;

@Service
public class ServiceBService {

	private static Logger log = Logger.getLogger(ServiceBService.class.getName());
	private static final String topic = "NEW_TRANS";

	private EventBus eventBus;
	private EventBusListener listener;
	private PdfFileRepository pdfFileRepository;
	private ExcelFileRepository excelFileRepository;

	@Autowired
	public ServiceBService(PdfFileRepository pdfFileRepository, ExcelFileRepository excelFileRepository,
			EventBus eventBus) {
		this.pdfFileRepository = pdfFileRepository;
		this.excelFileRepository = excelFileRepository;
		this.listener = new EventBusListener(pdfFileRepository, excelFileRepository);
		this.eventBus = eventBus;
		this.eventBus.register(listener);
	}

	public void storeAsFiles(TransactionDto dto) {
		this.eventBus.post(dto);
	}

	public ExcelFile getExcelFileByCustomerId(String id) {
		return excelFileRepository.findFileByCustomerId(UUID.fromString(id));
	}

	public PdfFile getPdfFileByCustomerId(String id) {
		return pdfFileRepository.findFileByCustomerId(UUID.fromString(id));
	}

	@KafkaListener(id = "ServiceB", groupId = "ogya-group", topics = { topic })
	public void listen(ConsumerRecord<String, String> payload) throws Exception {
		log.info(payload.key() + " | " + payload.partition() + " | " + payload.value());

		String json = payload.value();
		ObjectMapper mapper = new ObjectMapper();
		TransactionDto dto = mapper.readValue(json, TransactionDto.class);
		this.eventBus.post(dto);
	}

}
