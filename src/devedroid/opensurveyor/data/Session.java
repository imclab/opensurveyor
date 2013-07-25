package devedroid.opensurveyor.data;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import devedroid.opensurveyor.Utils;

public class Session implements Serializable {

	private long startTime, endTime=-1;
	private boolean hasExternals;
	
	private List<Marker> markers = new ArrayList<Marker>();
	
	public Session() {
		startTime = System.currentTimeMillis();
	}
	
	public void addMarker(Marker poi) {
		markers.add(poi);
		if(poi.containsExternals()) hasExternals = true;
	}
	
	public void finish() {
		endTime = System.currentTimeMillis();
	}
	
	public boolean isRunning() {
		return endTime==-1;		
	}
	
	public static final String FILE_EXT = ".svx";
	public static final String FILE_EXT_ARCHIVE = ".svp";
	
	public void exportArchive(File file) throws IOException {
		FileOutputStream fo = new FileOutputStream(file);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(fo));
		ZipEntry ee = new ZipEntry("survey"+FILE_EXT);
		out.putNextEntry(ee);
		writeTo(new OutputStreamWriter(out));
		out.closeEntry();
		for(Marker p: markers) {
			if(p.containsExternals()) {
				p.getExternals().saveExternals(out);
			}
		}
		out.close();
	}
	
	public boolean hasExternals() {
		return hasExternals;
	}
	
	public void writeTo(Writer os) throws IOException {
		os.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
		os.write("<survey " +
				"start=\""+Utils.formatISOTime(new Date(startTime))+"\" " +
				"end=\""+Utils.formatISOTime(new Date(endTime))+"\">\n");
		for(Marker p: markers) {
			os.write("  ");
			p.writeXML(os);
		}
		os.write("</survey>\n");
		os.flush();
	}
	
	public Iterable<Marker> getMarkers() {
		return markers;
	}


}
