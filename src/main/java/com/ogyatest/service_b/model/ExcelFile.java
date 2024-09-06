package com.ogyatest.service_b.model;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class ExcelFile {

	private UUID id;
	private UUID customerId;
	private String fileName;
	private Date created;
	private byte[] file;

}
