package es.fic.udc.riws.airport.twitter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
@Transactional(readOnly = true)
public class TweetRepository {

	private static final String RUTA_INDEX = "/Users/Brais/Downloads/tmp/tweetindex";

	public List<Tweet> findByKeywords(String words) throws IOException, ParseException {

		List<Tweet> result = new ArrayList<>();
		
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
		Directory directory = FSDirectory.open(new File(RUTA_INDEX));

		// Now search the index:
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		
		// Parse a simple query that searches for "text":
		QueryParser parser = new QueryParser(Version.LUCENE_48, "texto", analyzer);
		Query query = parser.parse(words);
		ScoreDoc[] hits = isearcher.search(query, null, 3000).scoreDocs;
		
		// Iterate through the results:
		for (ScoreDoc hit : hits) {
			Document hitDoc = isearcher.doc(hit.doc);
			String text = hitDoc.get("texto");
			Date date = new Date();//new Date(hitDoc.get("date"));
			result.add(new Tweet(text, date));
		}
		ireader.close();
		directory.close();
		
		return result;
	}
	
	public List<Tweet> findByKeywordsMatchingAll(String words) throws IOException, ParseException {

		List<Tweet> result = new ArrayList<>();
		
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
		Directory directory = FSDirectory.open(new File(RUTA_INDEX));

		// Now search the index:
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		
		// Parse a simple query that searches for "text":
		QueryParser parser = new QueryParser(Version.LUCENE_48, "texto", analyzer);
		Query query = parser.parse("\"" + words + "\"");
		ScoreDoc[] hits = isearcher.search(query, null, 3000).scoreDocs;
		
		// Iterate through the results:
		for (ScoreDoc hit : hits) {
			Document hitDoc = isearcher.doc(hit.doc);
			String text = hitDoc.get("texto");
			Date date = new Date();//new Date(hitDoc.get("date"));
			result.add(new Tweet(text, date));
		}
		ireader.close();
		directory.close();
		
		return result;
	}
}
