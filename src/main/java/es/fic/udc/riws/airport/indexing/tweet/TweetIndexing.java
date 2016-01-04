package es.fic.udc.riws.airport.indexing.tweet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class TweetIndexing {

	private static final String RUTA_ARCHIVOS = "/Users/Brais/Desktop/aIRport/tweets/";
	private static final String RUTA_INDEX = "/Users/Brais/Downloads/tmp/tweetindex";

	public static void doIndex() throws IOException, ParseException {

		List<String> l = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader((RUTA_ARCHIVOS + "file.csv")));
		try {
			String line = br.readLine();

			while (line != null) {
				l.add(line);
				line = br.readLine();
			}
		} finally {
			br.close();
		}

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);

		// Store the index in memory:
		//Directory directory = new RAMDirectory();

		// To store an index on disk, use this instead:
		Directory directory = FSDirectory.open(new File(RUTA_INDEX));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_48, analyzer);
		IndexWriter iwriter = new IndexWriter(directory, config);
		
		// Tratar archivos
		for (String line : l) {
			// Procesar cada linea.
			// (1) Dividir trozos
			if (line.isEmpty()) {
				continue;
			} else {
				String[] trozos = line.split(";,;");
				String usuario = trozos[0];
				String texto = trozos[1];
				//String localizacion = trozos[2];
				String fecha = trozos[2];
				
				// (2) Crear documentos y almacenarlos
				Document doc = new Document();
				doc.add(new TextField("usuario", usuario, Field.Store.YES));
				doc.add(new TextField("texto", texto, Field.Store.YES));
				//doc.add(new StringField("localizacion", localizacion, Field.Store.YES));
				doc.add(new Field("fecha", fecha, Field.Store.YES, Index.NOT_ANALYZED));
				iwriter.addDocument(doc);
			}
		}
		iwriter.close();
		
		/** Para probar : 
		// Now search the index:
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);

		// Parse a simple query that searches for "text":
		QueryParser parser = new QueryParser(Version.LUCENE_48, "texto", analyzer);
		Query query = parser.parse("american*");
		ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;

		System.out.println(hits.length);

		// Iterate through the results:
		for (ScoreDoc hit : hits) {
			Document hitDoc = isearcher.doc(hit.doc);
			System.out.println("This is the text of the indexed document that was a result for the "
					+ "query ’delay’:\n" + hitDoc.get("texto"));
		}
		ireader.close();
		directory.close();*/

	}

}
