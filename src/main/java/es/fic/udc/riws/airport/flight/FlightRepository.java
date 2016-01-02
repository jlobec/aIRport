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
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
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
public class FlightRepository {

	private static final String RUTA_INDEX = "tmp/flightindexall";

	public List<FlightResultAirportDto> findMostDelayedAirports() throws IOException, ParseException {
		Map<String, FlightResultAirportDto> resultMap = new HashMap<String, FlightResultAirportDto>();

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
		Directory directory = FSDirectory.open(new File(RUTA_INDEX));

		// Now search the index:
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);

		/** Buscar vuelos con retraso y que sean salidas */
		QueryParser delayParser = new QueryParser(Version.LUCENE_48, "delayed", analyzer);
		Query delayQuery = delayParser.parse("true");
		
		QueryParser departuresParser = new QueryParser(Version.LUCENE_48, "tipoVuelo", analyzer);
		Query departuresQuery = departuresParser.parse("departures");
		
		BooleanQuery departuresDelayedQuery = new BooleanQuery();
		departuresDelayedQuery.add(delayQuery, Occur.MUST); 
		departuresDelayedQuery.add(departuresQuery, Occur.MUST);
		
		ScoreDoc[] hitsDepartures = isearcher.search(departuresDelayedQuery, null, 3000).scoreDocs;
		
		/** Buscar vuelos con retraso y que sean llegadas */
		QueryParser arrivalsParser = new QueryParser(Version.LUCENE_48, "tipoVuelo", analyzer);
		Query arrivalsQuery = arrivalsParser.parse("arrivals");
		
		BooleanQuery arrivalsDelayedQuery = new BooleanQuery();
		arrivalsDelayedQuery.add(delayQuery, Occur.MUST); 
		arrivalsDelayedQuery.add(arrivalsQuery, Occur.MUST);

		ScoreDoc[] hitsArrivals = isearcher.search(arrivalsDelayedQuery, null, 3000).scoreDocs;
		
		
		/** Iterar resultados */
		for (ScoreDoc hit : hitsDepartures){
			Document hitDoc = isearcher.doc(hit.doc);
			String key = hitDoc.get("aeropuerto_nombre");
			if (resultMap.containsKey(key)) {
				FlightResultAirportDto actual = resultMap.get(key);
				actual.setValorDepartures(actual.getValorDepartures() + 1);
				resultMap.put(key, actual);
			} else {
				FlightResultAirportDto nuevo = new FlightResultAirportDto(key, 1, 0);
				resultMap.put(key, nuevo);
			}
		}
		
		for (ScoreDoc hit : hitsArrivals) {
			Document hitDoc = isearcher.doc(hit.doc);
			String key = hitDoc.get("aeropuerto_nombre");
			if (resultMap.containsKey(key)) {
				FlightResultAirportDto actual = resultMap.get(key);
				actual.setValorArrivals(actual.getValorArrivals() + 1);
				resultMap.put(key, actual);
			} else {
				FlightResultAirportDto nuevo = new FlightResultAirportDto(key, 0, 1);
				resultMap.put(key, nuevo);
			}
		}

		ireader.close();
		directory.close();
		
		// Componer resultado
		List<FlightResultAirportDto> res = new ArrayList<FlightResultAirportDto>();

		for (Entry<String, FlightResultAirportDto> entry : resultMap.entrySet()) {
			res.add(new FlightResultAirportDto(entry.getKey(), entry.getValue().getValorDepartures(), 
					entry.getValue().getValorArrivals()));
		}
		
		res.sort(FlightResultAirportDto.Comparators.VALORES);
		
		return res;
	}

	public List<FlightResultCompanyDto> findMostDelayedCompanies() throws IOException, ParseException {

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
			String key = hitDoc.get("compania");
			if (resultMap.containsKey(key)) {
				Integer actual = resultMap.get(key);
				resultMap.put(key, ++actual);
			} else {
				resultMap.put(key, 1);
			}
		}
		ireader.close();
		directory.close();

		// Componer resultado
		List<FlightResultCompanyDto> res = new ArrayList<FlightResultCompanyDto>();
		Integer total = 0; 
		for (Entry<String, Integer> entry : resultMap.entrySet()) {
			total+=entry.getValue();
			res.add(new FlightResultCompanyDto(entry.getKey(), (float) entry.getValue()));
		}
		//Preparar porcentajes
		for (FlightResultCompanyDto f : res){
			Float porcentRetrasos = (f.getPorcentRetrasos() / total) * 100;
			f.setPorcentRetrasos(porcentRetrasos);
		}
		res.sort(FlightResultCompanyDto.Comparators.PORCENTAJES);
		return res;
	}
}
