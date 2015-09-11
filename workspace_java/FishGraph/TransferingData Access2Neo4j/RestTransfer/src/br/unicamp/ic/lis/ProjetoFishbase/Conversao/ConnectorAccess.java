package br.unicamp.ic.lis.ProjetoFishbase.Conversao;

import java.sql.*;

public class ConnectorAccess {
	private Connection conn;
	
	ConnectorAccess() {
		try {
			conn = DriverManager.getConnection("jdbc:ucanaccess:///home/vroth/Downloads/FBApp.mdb"); //<----------- endere�o do arquivo do access
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// send a SQL query to the db
	ResultSet query(String statement) {
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(statement);
			return rs;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}