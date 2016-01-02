package es.fic.udc.riws.airport.flight;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly=true)
public class FlightRepository {

	private static final String RUTA_INDEX = "tmp/flightindex";
	
	public List<FlightResult> findMostDelayed(String campoClave) throws IOException, ParseException{
		
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
		Directory directory = FSDirectory.open(new File(RUTA_INDEX));

		// Now search the index:
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		
		// Parse a simple query that searches for "text":
		QueryParser parser = new QueryParser(Version.LUCENE_48, "delayed", analyzer);
		Query query = parser.parse("true");
		ScoreDoc[] hits = isearcher.search(query, null, 3000).scoreDocs;
		
		// Iterate through the results:
		for (ScoreDoc hit : hits) {
			Document hitDoc = isearcher.doc(hit.doc);
			String key = hitDoc.get("aeropuerto_nombre");
			if (resultMap.containsKey(key)){
				Integer actual = resultMap.get(key);
				resultMap.put(key, ++actual);
			} else {
				resultMap.put(key, 1);
			}
		}
		ireader.close();
		directory.close();
		
		//Componer resultado
		List<FlightResult> res = new ArrayList<FlightResult>();
		
		for (Entry<String, Integer> entry : resultMap.entrySet()) {
			res.add(new FlightResult(entry.getKey(), entry.getValue()));
		}
		res.sort(FlightResult.Comparators.VALOR);
		return res; 
	}
}
