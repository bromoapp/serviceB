package com.ogyatest.service_b.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ogyatest.service_b.mapper.PdfFileMapper;
import com.ogyatest.service_b.model.PdfFile;

@Repository
public class PdfFileRepository {

	private static final String tbl_name = "pdf_file";
	private JdbcTemplate template;

	public PdfFileRepository(DataSource ds) {
		this.template = new JdbcTemplate(ds);
	}

	public String addNewFile(PdfFile o) {
		try {
			UUID uuid = UUID.randomUUID();
			String sql = "INSERT INTO " + tbl_name + " (id,cust_id,file_name,created,file) VALUES(?,?,?,?,?);";
			template.update(conn -> {
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, uuid.toString());
				ps.setString(2, o.getCustomerId().toString());
				ps.setString(3, o.getFileName());
				ps.setDate(4, new java.sql.Date(o.getCreated().getTime()));
				ps.setBytes(5, o.getFile());
				return ps;
			});
			return uuid.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public PdfFile findFileByCustomerId(UUID uuid) {
		try {
			String sql = "SELECT * FROM " + tbl_name + " AS o WHERE o.cust_id = '" + uuid.toString() + "'";
			return template.queryForObject(sql, new PdfFileMapper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
