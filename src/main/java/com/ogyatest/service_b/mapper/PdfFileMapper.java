package com.ogyatest.service_b.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;

import com.ogyatest.service_b.model.PdfFile;

public class PdfFileMapper implements RowMapper<PdfFile> {

	@Override
	public PdfFile mapRow(ResultSet rs, int rowNum) throws SQLException {
		PdfFile o = PdfFile.builder().id(UUID.fromString(rs.getString("id")))
				.customerId(UUID.fromString(rs.getString("cust_id"))).fileName(rs.getString("file_name"))
				.created(rs.getDate("created")).file(rs.getBytes("file")).build();
		return o;
	}

}
