package com.ogyatest.service_b.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ogyatest.service_b.mapper.ExcelFileMapper;
import com.ogyatest.service_b.model.ExcelFile;

@Repository
public class ExcelFileRepository {

	private static final String tbl_name = "excel_file";
	private JdbcTemplate template;

	public ExcelFileRepository(DataSource ds) {
		this.template = new JdbcTemplate(ds);
	}

	public String addNewFile(ExcelFile o) {
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

	public ExcelFile findFileByCustomerId(UUID uuid) {
		try {
			String sql = "SELECT * FROM " + tbl_name + " AS o WHERE o.cust_id = '" + uuid.toString() + "'";
			return template.queryForObject(sql, new ExcelFileMapper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
