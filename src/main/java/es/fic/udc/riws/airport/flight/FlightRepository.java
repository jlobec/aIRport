package es.fic.udc.riws.airport.flight;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class FlightRepository {

	private static final String RUTA_INDEX = "/home/jesus/tmp/flightindexwithdate";
//	private static final String RUTA_INDEX = "/Users/Brais/Downloads/tmp/flightindexwithdate";
	private static final String AIRPORT_NAME_CRITERIA = "airport";
	private static final String AIRPORT_CODE_CRITERIA = "airportcode";
	private static final String FLIGHTCODE_CRITERIA = "flightcode";
	private static final String COMPANY_CRITERIA = "company";

	public List<FlightResultAirportDto> findMostDelayedAirports() throws IOException, ParseException {
		Map<String, FlightResultAirportDto> resultMap = new HashMap<String, FlightResultAirportDto>();
		Map<String, Integer> arrivalsMap = new HashMap<String, Integer>();
		Map<String, Integer> departuresMap = new HashMap<String, Integer>();

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
		
//		long baseTime = System.currentTimeMillis();
//		String lowerDate = buildDate(baseTime - 604800000);
//        String upperDate = buildDate(baseTime);
//        boolean includeLower = true;
//        boolean includeUpper = true;
//        Query dateQuery = TermRangeQuery.newStringRange("fecha", lowerDate, upperDate, includeLower, includeUpper);
		
		BooleanQuery departuresDelayedQuery = new BooleanQuery();
		departuresDelayedQuery.add(delayQuery, Occur.MUST); 
		departuresDelayedQuery.add(departuresQuery, Occur.MUST);
//		departuresDelayedQuery.add(dateQuery, Occur.MUST);
		
		ScoreDoc[] hitsDepartures = isearcher.search(departuresDelayedQuery, null, 3000).scoreDocs;
		
		/** Buscar vuelos con retraso y que sean llegadas */
		QueryParser arrivalsParser = new QueryParser(Version.LUCENE_48, "tipoVuelo", analyzer);
		Query arrivalsQuery = arrivalsParser.parse("arrivals");
		
		BooleanQuery arrivalsDelayedQuery = new BooleanQuery();
		arrivalsDelayedQuery.add(delayQuery, Occur.MUST); 
		arrivalsDelayedQuery.add(arrivalsQuery, Occur.MUST);

		ScoreDoc[] hitsArrivals = isearcher.search(arrivalsDelayedQuery, null, 3000).scoreDocs;
		
		/** Buscar todas las llegadas */
		BooleanQuery allArrivalsQuery = new BooleanQuery();
		allArrivalsQuery.add(arrivalsQuery, Occur.MUST);
		ScoreDoc[] hitsAllArrivals = isearcher.search(allArrivalsQuery, null, 100000).scoreDocs;
		
		/** Buscar todas las salidas */
		BooleanQuery allDeparturesQuery = new BooleanQuery();
		allDeparturesQuery.add(departuresQuery, Occur.MUST);
		ScoreDoc[] hitsAllDepartures = isearcher.search(allDeparturesQuery, null, 100000).scoreDocs;
		
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
		
		// Todas las salidas y llegadas
		for (ScoreDoc hit : hitsAllArrivals) {
			Document hitDoc = isearcher.doc(hit.doc);
			String key = hitDoc.get("aeropuerto_nombre");
			if (arrivalsMap.containsKey(key)) {
				Integer actual = arrivalsMap.get(key);
				arrivalsMap.put(key, ++actual);
			} else {
				arrivalsMap.put(key, 1);
			}
		}
		
		for (ScoreDoc hit : hitsAllDepartures) {
			Document hitDoc = isearcher.doc(hit.doc);
			String key = hitDoc.get("aeropuerto_nombre");
			if (departuresMap.containsKey(key)) {
				Integer actual = departuresMap.get(key);
				departuresMap.put(key, ++actual);
			} else {
				departuresMap.put(key, 1);
			}
		}

		ireader.close();
		directory.close();
		
		// Componer resultado
		List<FlightResultAirportDto> res = new ArrayList<FlightResultAirportDto>();
		try{
		for (Entry<String, FlightResultAirportDto> entry : resultMap.entrySet()) {
			Float totalArrivals = (arrivalsMap.get(entry.getKey()) != null) ? (float) arrivalsMap.get(entry.getKey()) : null;
			Float delayedArrivals = (float) entry.getValue().getValorArrivals();
			Float totalDepartures = (departuresMap.get(entry.getKey()) != null) ? (float) departuresMap.get(entry.getKey()) : null;
			Float delayedDepartures = (float) entry.getValue().getValorDepartures();
			
			if (totalArrivals!= null && totalDepartures != null){
				Float porcentArrivals = (delayedArrivals/totalArrivals) * 100;
				Float porcentDepartures =  (delayedDepartures/totalDepartures) * 100;
				FlightResultAirportDto f = new FlightResultAirportDto();
				f.setNombre(entry.getKey());
				f.setPorcentArrivals(porcentArrivals);
				f.setPorcentDepartures(porcentDepartures);
				f.setValorArrivals(entry.getValue().getValorArrivals());
				f.setValorDepartures(entry.getValue().getValorDepartures());
				res.add(f);
			}
//			res.add(new FlightResultAirportDto(entry.getKey(), entry.getValue().getValorDepartures(), 
//					entry.getValue().getValorArrivals()));
		}
		
		
		res.sort(FlightResultAirportDto.Comparators.VALORES);
		
		} catch (Exception e){
			e.printStackTrace();
		}
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
	
	
	public List<FlightResultAirportDto> findByCriteria(String criteria) throws IOException, ParseException, java.text.ParseException {
		Map<String, FlightResultAirportDto> resultMap = new HashMap<String, FlightResultAirportDto>();
		List<Query> userQueries = new ArrayList<Query>();
		List<FlightResultAirportDto> res = new ArrayList<FlightResultAirportDto>();
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Map<String, String> claveValorQuery = new HashMap<String, String>();
		Map<String, Integer> arrivalsMap = new HashMap<String, Integer>();
		Map<String, Integer> departuresMap = new HashMap<String, Integer>();
		
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
		Directory directory = FSDirectory.open(new File(RUTA_INDEX));
		
		/** Parseo de los criterios */
		String claveAnterior = null;
		List<String> keywords = getValues(criteria);
		for (String word : keywords){
			
			word = word.trim().replaceAll("\"", "");
			// Tres opciones: 
			// (1) clave:valor
			// (2) clave:
			// (3) valor (de la anterior clave)
			String[] trozos = word.split(":");
			//Si queda clave valor
			if (trozos.length > 1){
				claveValorQuery.put(trozos[0], trozos[1]);
				claveAnterior = null;
			} else {
				if (claveAnterior != null){
					claveValorQuery.put(claveAnterior, trozos[0]);
					claveAnterior = null;
				} else {
					claveAnterior = trozos[0];
				}
			}
		}
		
		for (Entry<String, String> entry : claveValorQuery.entrySet()) {
			String clave = entry.getKey();
			String valor = entry.getValue();
			QueryParser userQParser;
			//Ver el tipo de criterio
			if (AIRPORT_NAME_CRITERIA.equalsIgnoreCase(clave)){
				userQParser = new QueryParser(Version.LUCENE_48, "aeropuerto_nombre", analyzer);
				Query userQuery = userQParser.parse(valor);
				userQueries.add(userQuery);
			}
			if (AIRPORT_CODE_CRITERIA.equalsIgnoreCase(clave)){
				userQParser = new QueryParser(Version.LUCENE_48, "aeropuerto_codigo", analyzer);
				Query userQuery = userQParser.parse(valor);
				userQueries.add(userQuery);
			}
			if (FLIGHTCODE_CRITERIA.equalsIgnoreCase(clave)){
				userQParser = new QueryParser(Version.LUCENE_48, "codigovuelo", analyzer);
				String codigoVuelo = valor.substring(0, 2)+" "+valor.substring(2, valor.length());
				Query userQuery = userQParser.parse(codigoVuelo);
				userQueries.add(userQuery);
			}
			if (COMPANY_CRITERIA.equalsIgnoreCase(clave)){
				userQParser = new QueryParser(Version.LUCENE_48, "compania", analyzer);
				Query userQuery = userQParser.parse(valor);
				userQueries.add(userQuery);
			}
		}
				
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
		for (Query q: userQueries){
			departuresDelayedQuery.add(q, Occur.MUST);
		}
		ScoreDoc[] hitsDepartures = isearcher.search(departuresDelayedQuery, null, 3000).scoreDocs;
		
		/** Buscar vuelos con retraso y que sean llegadas */
		QueryParser arrivalsParser = new QueryParser(Version.LUCENE_48, "tipoVuelo", analyzer);
		Query arrivalsQuery = arrivalsParser.parse("arrivals");
		
		BooleanQuery arrivalsDelayedQuery = new BooleanQuery();
		arrivalsDelayedQuery.add(delayQuery, Occur.MUST); 
		arrivalsDelayedQuery.add(arrivalsQuery, Occur.MUST);
		for (Query q: userQueries){
			arrivalsDelayedQuery.add(q, Occur.MUST);
		}
		ScoreDoc[] hitsArrivals = isearcher.search(arrivalsDelayedQuery, null, 3000).scoreDocs;
		
		/** Buscar todas las llegadas */
		BooleanQuery allArrivalsQuery = new BooleanQuery();
		allArrivalsQuery.add(arrivalsQuery, Occur.MUST);
		for (Query q: userQueries){
			allArrivalsQuery.add(q, Occur.MUST);
		}
		ScoreDoc[] hitsAllArrivals = isearcher.search(allArrivalsQuery, null, 100000).scoreDocs;
		
		/** Buscar todas las salidas */
		BooleanQuery allDeparturesQuery = new BooleanQuery();
		allDeparturesQuery.add(departuresQuery, Occur.MUST);
		for (Query q: userQueries){
			allDeparturesQuery.add(q, Occur.MUST);
		}
		ScoreDoc[] hitsAllDepartures = isearcher.search(allDeparturesQuery, null, 100000).scoreDocs;

		
		
		
		/** Iterar resultados */
		for (ScoreDoc hit : hitsDepartures){
			Document hitDoc = isearcher.doc(hit.doc);
			String key = hitDoc.get("fecha");
			Date date = DateTools.stringToDate(key);
			String dateFormat = df.format(date);
			String keyMap = key.substring(0,8);
			if (resultMap.containsKey(keyMap)) {
				FlightResultAirportDto actual = resultMap.get(keyMap);
				actual.setValorDepartures(actual.getValorDepartures() + 1);
				resultMap.put(keyMap, actual);
			} else {
				FlightResultAirportDto nuevo = new FlightResultAirportDto(dateFormat, 1, 0);
				resultMap.put(keyMap, nuevo);
			}
		}
		
		for (ScoreDoc hit : hitsArrivals) {
			Document hitDoc = isearcher.doc(hit.doc);
			String key = hitDoc.get("fecha");
			Date date = DateTools.stringToDate(key);
			String dateFormat = df.format(date);
			String keyMap = key.substring(0,8);
			if (resultMap.containsKey(keyMap)) {
				FlightResultAirportDto actual = resultMap.get(keyMap);
				actual.setValorArrivals(actual.getValorArrivals() + 1);
				resultMap.put(keyMap, actual);
			} else {
				FlightResultAirportDto nuevo = new FlightResultAirportDto(dateFormat, 0, 1);
				resultMap.put(keyMap, nuevo);
			}
		}
		
		// Todas las salidas y llegadas
		for (ScoreDoc hit : hitsAllArrivals) {
			Document hitDoc = isearcher.doc(hit.doc);
			String key = hitDoc.get("fecha");
			Date date = DateTools.stringToDate(key);
			String dateFormat = df.format(date);
			String keyMap = key.substring(0,8);
			if (arrivalsMap.containsKey(keyMap)) {
				Integer actual = arrivalsMap.get(keyMap);
				arrivalsMap.put(keyMap, ++actual);
			} else {
				arrivalsMap.put(keyMap, 1);
			}
		}
		
		for (ScoreDoc hit : hitsAllDepartures) {
			Document hitDoc = isearcher.doc(hit.doc);
			String key = hitDoc.get("fecha");
			Date date = DateTools.stringToDate(key);
			String dateFormat = df.format(date);
			String keyMap = key.substring(0,8);
			if (departuresMap.containsKey(keyMap)) {
				Integer actual = departuresMap.get(keyMap);
				departuresMap.put(keyMap, ++actual);
			} else {
				departuresMap.put(keyMap, 1);
			}
		}

		ireader.close();
		directory.close();
		
		// Componer resultado
		

		for (Entry<String, FlightResultAirportDto> entry : resultMap.entrySet()) {
//			res.add(new FlightResultAirportDto(entry.getValue().getNombre(), entry.getValue().getValorDepartures(), 
//					entry.getValue().getValorArrivals()));
			
			
			Float totalArrivals = (arrivalsMap.get(entry.getKey()) != null) ? (float) arrivalsMap.get(entry.getKey()) : null;
			Float delayedArrivals = (float) entry.getValue().getValorArrivals();
			Float totalDepartures = (departuresMap.get(entry.getKey()) != null) ? (float) departuresMap.get(entry.getKey()) : null;
			Float delayedDepartures = (float) entry.getValue().getValorDepartures();
			
			if (totalArrivals!= null && totalDepartures != null){
				Float porcentArrivals = (delayedArrivals/totalArrivals) * 100;
				Float porcentDepartures =  (delayedDepartures/totalDepartures) * 100;
				FlightResultAirportDto f = new FlightResultAirportDto();
				f.setNombre(entry.getValue().getNombre());
				f.setPorcentArrivals(porcentArrivals);
				f.setPorcentDepartures(porcentDepartures);
				f.setValorArrivals(entry.getValue().getValorArrivals());
				f.setValorDepartures(entry.getValue().getValorDepartures());
				res.add(f);
			}
			
		}
		
		res.sort(FlightResultAirportDto.Comparators.VALORES);
		
		return res;
	}

	
	private List<String> getValues(String input) {
        List<String> matchList = new ArrayList<>();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
        Matcher regexMatcher = regex.matcher(input);
        while (regexMatcher.find()) {
            matchList.add(regexMatcher.group());
        }
        return matchList;
    }
	
	
}
