package gsb.toolskit.frameworks.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Stack;

public class MyFileHandler {
	static Stack<Reader> reader_stack = new Stack<Reader>();
	static Stack<Writer> writer_stack = new Stack<Writer>();
	public static void clearReaderStack() throws IOException {
		if( reader_stack != null && reader_stack.isEmpty() == false) {
			MyFileHandler.closeReadersCreatedAfter( reader_stack.peek());	
		}
	}
	public static void clearWriterStack() throws IOException {
		if( writer_stack != null && writer_stack.isEmpty() == false) {
			MyFileHandler.closeWritersCreatedAfter( writer_stack.peek());	
		}
	}
	public static void closeReadersCreatedAfter( Reader target_reader) throws IOException {
		if( reader_stack == null || reader_stack.contains( target_reader) == false) {
			return;
		}
		boolean found = false;
		while(found != true) {
			Reader r = reader_stack.pop();
			if( r.equals( target_reader)) {
				found = true;
			}
			r.close();
		}
	}
	public static void closeWritersCreatedAfter( Writer target_writer) throws IOException {
		if( writer_stack == null || writer_stack.contains( target_writer) == false) {
			return;
		}
		boolean found = false;
		while(found != true) {
			Writer w = writer_stack.pop();
			if( w.equals( target_writer)) {
				found = true;
			}
			w.close();
		}
	}
	public static BufferedReader getBufferedFileReader( String file_name) throws FileNotFoundException {
		BufferedReader br = new BufferedReader( new FileReader( file_name));
		reader_stack.push( br);
		return br;
	}
	public static BufferedReader getBufferedFileReader( String file_name, String encoding) throws UnsupportedEncodingException, FileNotFoundException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream( file_name), encoding));
		reader_stack.push( br);
		return br;
	}
	public static BufferedWriter getBufferedFileWriter( String file_name) throws IOException {
		BufferedWriter bw = new BufferedWriter( new FileWriter( file_name));
		writer_stack.push( bw);
		return bw;
	}
	public static BufferedWriter getBufferedFileWriter( String file_name, String encoding) throws UnsupportedEncodingException, FileNotFoundException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream( file_name), encoding));
		writer_stack.push( bw);
		return bw;
	}
	private TransForm formater;
	public void transferAFile2BFileByLine( String file_name, String A_ENCODING, String B_ENCODING) throws IOException {
		String target = null;
		int indexOfDot = file_name.indexOf(".");
		if( indexOfDot >= 0) {
			target = file_name.substring(0, indexOfDot) + "_" + B_ENCODING + file_name.substring(indexOfDot);
		} else {
			target = file_name + "_" + B_ENCODING;
		}
		BufferedReader br = MyFileHandler.getBufferedFileReader(file_name, A_ENCODING);
		BufferedWriter bw = MyFileHandler.getBufferedFileWriter(target, B_ENCODING);
		
		while( true) {
			String single_line = br.readLine();
			if( single_line == null) {
				break;
			}
			if( formater == null) {
				formater = new TransForm(){};
			}
			bw.write( formater.lineStringFormat( single_line));
			bw.flush();
		}
		
		MyFileHandler.closeReadersCreatedAfter( br);
		MyFileHandler.closeWritersCreatedAfter( bw);
	}
	public void transferBig5File2Utf8File( String big5_file) throws IOException {
		this.transferAFile2BFileByLine(big5_file, "Big5", "UTF-8");
	}
}
