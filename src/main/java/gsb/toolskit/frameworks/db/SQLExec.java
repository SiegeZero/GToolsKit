package gsb.toolskit.frameworks.db;

public interface SQLExec {
	public String sqlGenerate( String using_table);
	public Object complexResultSetHandle( Object source);
}
