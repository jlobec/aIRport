package es.fic.udc.riws.airport.indexing.flight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;



public class FlightIndexing {

	//Subconjunto de todos los documentos con 20 documentos aprox. para ir probando
	private static final String RUTA_ARCHIVOS = "/home/jesus/flights_parsed";
	private static final String RUTA_INDEX = "tmp/flightindexwithdate";
	
	public static void doIndex() throws IOException, ParseException {
		
		List<String> files = new ArrayList<String>();
		
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
		
		// Store the index in memory:
		//Directory directory = new RAMDirectory();
		
		// To store an index on disk, use this instead:
		Directory directory = FSDirectory.open(new File(RUTA_INDEX));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_48, analyzer);
		IndexWriter iwriter = new IndexWriter(directory, config);
		
		// Leer los archivos de la carpeta
		Files.walk(Paths.get(RUTA_ARCHIVOS)).forEach(filePath -> {
		    if (Files.isRegularFile(filePath)) {
		        files.add(filePath.toString()); 
		    }
		});
		
		//Tratar archivos
		for (String file : files){
			try(BufferedReader br = new BufferedReader(new FileReader(file))) {
	            for(String line; (line = br.readLine()) != null; ) {
	                // Procesar cada linea.
	            	// (1) Dividir trozos
	            	String[] trozos = line.split(",");
	            	String codigoVuelo = trozos[0].trim();
	            	String compania = trozos[1].trim();
	            	String aeropuerto = trozos[2].trim();
	            	//Tratar fecha
	            	String fecha = trozos[3].trim();
	            	try {
						fecha = buildLuceneDate(fecha);
					} catch (java.text.ParseException e) {
						continue;
					}
	            	String estado = trozos[4].trim();
	            	String tipoVuelo = trozos[5].trim();
	            	
	            	// El aeropuerto se divide en codigo de aeropuerto y nombre
	            	String[] aepTrozos = aeropuerto.split(" ");
	            	String aepCodigo = aepTrozos[0].replaceAll("[()]", "");
	            	String aepNombre = "";
	            	for (int i = 1; i<aepTrozos.length; i++){
	            		aepNombre += aepTrozos[i]+" ";
	            	}
	            	aepNombre = aepNombre.trim();
	            	
	            	// Comprobar si el vuelo ha tenido retraso (Delayed)
	            	String[] estadoTrozos = estado.split(" ");
	            	estado = estadoTrozos[0];
	            	Boolean delayed = false;
	            	if (estadoTrozos.length > 1){
	            		delayed = true;
	            	}
	            	
	            	// (2) Crear documentos y almacenarlos
	            	Document doc = new Document();
	            	doc.add(new TextField("codigovuelo", codigoVuelo, Field.Store.YES));
	            	doc.add(new TextField("compania", compania, Field.Store.YES));
	            	doc.add(new StringField("aeropuerto_codigo", aepCodigo, Field.Store.YES));
	            	doc.add(new TextField("aeropuerto_nombre", aepNombre, Field.Store.YES));
	            	
	            	doc.add(new Field("fecha", fecha, Field.Store.YES, Index.NOT_ANALYZED));
//	            	doc.add(new StringField("fecha", fecha, Field.Store.YES));
	            	
	            	
	            	doc.add(new StringField("estado", estado, Field.Store.YES));
	            	doc.add(new StringField("delayed", delayed.toString(), Field.Store.NO));
	            	doc.add(new StringField("tipoVuelo", tipoVuelo, Field.Store.NO));
	            	iwriter.addDocument(doc);
	            	
	            }
	        }
			
		}
        iwriter.close();
		directory.close();
		
	}
	
	private static String buildLuceneDate(String date) throws java.text.ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
		Date fecha = sdf.parse(date);
		return DateTools.dateToString(fecha, Resolution.SECOND);
	}
	
	
	
}
