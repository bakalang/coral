package com.dosomething.commons.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.dosomething.commons.dto.Securitys;
import com.dosomething.commons.model.database.DBQueryRunner;


public class SecuritysDAO {
	
	public static List<Securitys> findAllSecuritys(final Connection conn) throws Exception {
		String sql = "SELECT * from SECURITYS ";
		return DBQueryRunner.getBeanList(conn, Securitys.class, sql);
	}
	
	public static int save(final Connection conn, String securityId, String url) throws SQLException {
		String sql = "INSERT INTO SECURITYS (SECURITY_ID ,URL ,LAST_MODIFIED_DATE) VALUES (?, ?, CURRENT_TIMESTAMP)";
		return DBQueryRunner.update(conn, sql, securityId, url);
	}	
}