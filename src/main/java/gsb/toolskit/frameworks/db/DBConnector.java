package gsb.toolskit.frameworks.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.sql.DriverManager;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import gsb.toolskit.frameworks.MyExcepts.MyAllException;
import gsb.toolskit.frameworks.predef.StaticValues;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.sql.SQLException;

public class DBConnector {
	public DBConnector() {
		
	}
	
	private SQLExec sql;
	public DBConnector setSql( SQLExec sql) {
		this.sql = sql;
		return this;
	}
	public DBConnector execSql() throws MyAllException {
		boolean execSuccess = true;
		if( sql == null) {
			execSuccess = false;
			error_code =  "interface init failed";
		}
		if( execSuccess) {
			createActiveStatement();
			try {
				s.execute( sql.sqlGenerate( using_table));
				sql.complexResultSetHandle( s.getResultSet());	
			} catch (SQLException e) {
				error_code = StaticValues.SQLTips.StatementError6 + e.getMessage();
				execSuccess = false;
			}
		}
		if( execSuccess == false) {
			throw new MyAllException( error_code);
		}
		return this;
	}
	

	String property_file = "db_config.properties";
	private String using_db;
	private String using_table;
	private boolean parallelConnections;
	public DBConnector setProperty( String file_name) {
		property_file = file_name;
		return this;
	}
	public DBConnector setDB( String db_name) {
		using_db = url + db_name;
		return this;
	}
	public DBConnector setTable(  String table_name) {
		using_table = table_name;
		return this;
	}
	public DBConnector setParallel( boolean parallelConnections) {
		this.parallelConnections = parallelConnections; 
		return this;
	}
	public boolean getParallel() {
		return this.parallelConnections;
	}

	Statement s;
	static Connection c;
	public DBConnector createActiveStatement() throws MyAllException {
		this.closeCurrentStatement();
		if( c == null) {
			this.createConnection();
		}
		try {
			System.out.println( DBConnector.connections_created_amount + ":" + DBConnector.connections_closed_amount);
			s = (Statement) c.createStatement();
		} catch (SQLException e) {
			throw new MyAllException( StaticValues.SQLTips.StatementError1 + e.getMessage());
		}
		return this;
	}
	private void closeCurrentStatement() throws MyAllException {
		if( s != null) {
			try {
				s.close();
				s = null;
			} catch (SQLException e) {
				throw new MyAllException( StaticValues.SQLTips.StatementError2 + e.getMessage());
			}
		}
	}

	static String url = "jdbc:mysql://localhost:3306/";
	static String db_account = "root";
	static String db_password = "";
	public static long connections_created_amount = 0; 
	public static long connections_closed_amount = 0; 
	public DBConnector createConnection( boolean parallelConnection) throws MyAllException {
		this.setParallel(parallelConnection);
		this.createConnection();
		return this;
	}
	private void createConnection() throws MyAllException {
		if( parallelConnections == false) {
			this.closeConnection();	
		}
		boolean connectSuccess = true;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			error_code = StaticValues.SQLTips.StatementError3 + e.getMessage();
			connectSuccess = false;
		}
		if( connectSuccess) {
			this.tryInitDBInfoFrom();
			try {
				c = (Connection) DriverManager.getConnection( using_db, db_account, db_password);
			} catch (SQLException e) {
				error_code = StaticValues.SQLTips.StatementError5 + e.getMessage();
				connectSuccess = false;
			}
		}
		if( connectSuccess == false) {
			this.closeConnection();
			throw new MyAllException( error_code);
		} else {
			connections_created_amount++;
		}
	}
	private void closeConnection() throws MyAllException {
		if( c != null) {
			try {
				c.close();
				c = null;
				connections_closed_amount++;
			} catch (SQLException e) {
				throw new MyAllException( StaticValues.SQLTips.StatementError4 + e.getMessage());
			}
		}
	}
	private void tryInitDBInfoFrom() throws MyAllException {
		tryInitDBInfoFrom( property_file);
	}
	String error_code = null;
	public DBConnector tryInitDBInfoFrom( String property_file) throws MyAllException {
		File properties = new File( property_file);
		boolean initSuccess = true;
		if( properties.exists() == false || properties.isDirectory()) {
			return this;
		}
		BufferedReader properties_reader = null;
		try {
			properties_reader = new BufferedReader( new FileReader( properties));
		} catch (FileNotFoundException e) {
			error_code =  StaticValues.FileTips.FileError1 + e.getMessage();
			initSuccess = false;
		}
		Map<String, String> mapper = new HashMap<String, String>();
		while( initSuccess) {
			try {
				String single_line = properties_reader.readLine();
				if( null == single_line) {
					break;
				}
				single_line = single_line.replaceAll(" ", "");
				int indexOfEq = single_line.indexOf("=");
				if( indexOfEq == -1) {
					error_code = StaticValues.FileTips.FileError3;
					initSuccess = false;
				}
				String key = single_line.substring( 0, indexOfEq).toLowerCase();
				if( key.equals("")) {
					error_code = StaticValues.FileTips.FileError4;
					initSuccess = false;
					break;
				}
				String value = single_line.substring( indexOfEq + 1);
				mapper.put(key, value);
			} catch (IOException e) {
				error_code = StaticValues.FileTips.FileError2+ e.getMessage();
				initSuccess = false;
			}
		}
		try {
			properties_reader.close();
			properties_reader = null;
		} catch (IOException e) {
			error_code = StaticValues.FileTips.FileError5 + e.getMessage();
			initSuccess = false;
		}
		if( initSuccess) {
			if( mapper.containsKey( "url")) {
				url = mapper.get( "url");	
			}
			if( mapper.containsKey( "db_account")) {
				db_account = mapper.get( "db_account");	
			}
			if( mapper.containsKey( "db_password")) {
				db_password = mapper.get( "db_password");	
			}
		} 
		mapper.clear();
		mapper = null;
		if( initSuccess == false) {
			throw new MyAllException( error_code);
		}
		return this;
	}
}
