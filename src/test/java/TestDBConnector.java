import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import gsb.toolskit.frameworks.MyExcepts.MyAllException;
import gsb.toolskit.frameworks.db.DBConnector;
import gsb.toolskit.frameworks.db.Jobs;
import gsb.toolskit.frameworks.db.MyTimer;
import gsb.toolskit.frameworks.db.SQLExec;

public class TestDBConnector {
	@Test
	@DisplayName("测试终端输出")
	public void testPrint() {
		System.out.println( "Hello Tester");
	}
	
	@Test
	public void testCreateConnection() {
		final DBConnector db = new DBConnector();
		int t = 10;
		long cost = 0;
		MyTimer tmr = new MyTimer();
		for( int i=0;i<1;i++) {

			dateFromInts(db, t, tmr, 1901, 1, 1);

			dateFromString(db, t, tmr, 2018, 12, 30);
	
		}

		dateFromInts(db, t, tmr, 1901, 1, 1);

		dateFromInts(db, t, tmr, 1999, 6, 26);
		
		dateFromInts(db, t, tmr, 2018, 12, 31);

		dateFromString(db, t, tmr, 2018, 12, 30);
		
		dateFromString(db, t, tmr, 1999, 6, 26);

		dateFromString(db, t, tmr, 1901, 1, 1);


	}

	private void dateFromInts(final DBConnector db, int t, MyTimer tmr, int year, int month, int day) {
		long cost;
		db.setDB("CalendarDB").setTable("CalendarSplited").setParallel( false);
		tmr.setJob( new Jobs() {
			public void doJob() {
				try {
					db.execSql();
				} catch (MyAllException e) {
					e.printStackTrace();
				}
			}});
		selectTraditionCalendarDateByInts(db, year, month, day);
		cost = tmr.countJobCostedTimeFor( t);
		System.out.println( "dateFromInts timer cost:" + cost + "ms for " + t + " times.");
	}

	private void dateFromString(final DBConnector db, int t, MyTimer tmr, int year, int month, int day) {
		long cost;
		db.setDB("CalendarDB").setTable("Calendars").setParallel( false);
		tmr.setJob( new Jobs() {
			public void doJob() {
				try {
					db.execSql();
				} catch (MyAllException e) {
					e.printStackTrace();
				}
			}});
		selectTraditionCalendarDateByString(db, year, month, day);
		cost = tmr.countJobCostedTimeFor( t);
		System.out.println( "dateFromString timer cost:" + cost + "ms for " + t + " times.");
	}
	private void selectTraditionCalendarDateByInts(DBConnector db, int year, int month, int day) {
		final String cause = " where year=" + year
							+ " and month=" + month
							+ " and day=" + day;
		db.setSql( new SQLExec(){
			public String sqlGenerate(String using_table) {
				return "select td from " + using_table + cause;
			}

			public Object complexResultSetHandle(Object source) {
				ResultSet rs = null;
				if( source != null && source instanceof ResultSet) {
					rs = (ResultSet) source;
				} else {
					return rs;
				}
				String td = "无结果";
				try {
					if( rs.next()) {
						td = rs.getString("td");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				System.out.println( td);
				return null;
			}});
		
	}
	private void selectTraditionCalendarDateByString( DBConnector db, int year, int month, int day) {
		final String d = "" + year + (month>9?month:"0"+month) + (day>9?day:"0"+day);
		db.setSql( new SQLExec(){
			public String sqlGenerate(String using_table) {
				return "select td from " + using_table + " where d='" + d + "'";
			}

			public Object complexResultSetHandle(Object source) {
				ResultSet rs = null;
				if( source != null && source instanceof ResultSet) {
					rs = (ResultSet) source;
				} else {
					return rs;
				}
				String td = "无结果";
				try {
					if( rs.next()) {
						td = rs.getString("td");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				System.out.println( td);
				return null;
			}});
	}
}
