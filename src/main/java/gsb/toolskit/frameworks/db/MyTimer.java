package gsb.toolskit.frameworks.db;

public class MyTimer{
	static long start;
	static long end;
	Jobs j;
	public void setJob( Jobs jobs) {
		this.j = jobs;
	}
	public long countJobCostedTimeFor( int cycleTimes) {
		start = System.currentTimeMillis();
		for( int i=0; i < cycleTimes; i++) {
			j.doJob();
		}
		end = System.currentTimeMillis();
		return end - start;
	}
}

