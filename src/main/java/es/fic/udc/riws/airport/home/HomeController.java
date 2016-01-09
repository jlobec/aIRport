package es.fic.udc.riws.airport.home;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.fic.udc.riws.airport.flight.FlightRepository;
import es.fic.udc.riws.airport.flight.FlightResultAirportDto;
import es.fic.udc.riws.airport.flight.FlightResultCompanyDto;
import es.fic.udc.riws.airport.twitter.Tweet;
import es.fic.udc.riws.airport.twitter.TweetRepository;

@Controller
public class HomeController {

	@Autowired
	FlightRepository flightRepository;

	@Autowired
	TweetRepository tweetRepository;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Principal principal, Model model) throws IOException, ParseException {

		if (principal == null) {
			return "home/homeNotSignedIn";
		}

		// Grafica barras (retrasos por aeropuerto)
		List<FlightResultAirportDto> datosVuelos = flightRepository.findMostDelayedAirports();
		List<FlightResultAirportDto> subset = datosVuelos.subList(0, 10);
		model.addAttribute("datosvuelos", subset);

		// Grafica companias
		List<FlightResultCompanyDto> datosCompanias = flightRepository.findMostDelayedCompanies();
		List<FlightResultCompanyDto> subsetC = datosCompanias.subList(0, 10);
		model.addAttribute("datoscomps", subsetC);

		List<Tweet> tweets = getTweetsByCompanyAndAirport(subsetC, subset);

		// Obtenemos un tweet de cada una de los 3 aeropuertos con mas retrasos
		model.addAttribute("tweetAero1", tweets.get(0).getText());
		model.addAttribute("tweetAero2", tweets.get(1).getText());
		model.addAttribute("tweetAero3", tweets.get(2).getText());
		model.addAttribute("tweetAero4", tweets.get(3).getText());
		model.addAttribute("tweetAero5", tweets.get(4).getText());
		model.addAttribute("tweetAero6", tweets.get(5).getText());
		model.addAttribute("tweetAero7", tweets.get(6).getText());
		model.addAttribute("tweetAero8", tweets.get(7).getText());
		model.addAttribute("tweetAero9", tweets.get(8).getText());
		model.addAttribute("tweetAero10", tweets.get(9).getText());

		// Obtenemos un tweet por cada uno de las 3 companias con mas retrasos
		model.addAttribute("tweetComp1", tweets.get(10).getText());
		model.addAttribute("tweetComp2", tweets.get(11).getText());
		model.addAttribute("tweetComp3", tweets.get(12).getText());
		model.addAttribute("tweetComp4", tweets.get(13).getText());
		model.addAttribute("tweetComp5", tweets.get(14).getText());
		model.addAttribute("tweetComp6", tweets.get(15).getText());
		model.addAttribute("tweetComp7", tweets.get(16).getText());
		model.addAttribute("tweetComp8", tweets.get(17).getText());
		model.addAttribute("tweetComp9", tweets.get(18).getText());
		model.addAttribute("tweetComp10", tweets.get(19).getText());

		return "home/homeSignedIn";
	}

	private List<Tweet> getTweetsByCompanyAndAirport(List<FlightResultCompanyDto> subsetC,
			List<FlightResultAirportDto> subset) {

		List<Tweet> tweets = new ArrayList<>();
		List<Tweet> tweetList;
		Random rand = new Random();
		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subset.get(0).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}

		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subset.get(1).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}

		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subset.get(2).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}
		
		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subset.get(3).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}

		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subset.get(4).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}
		
		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subset.get(5).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}
		
		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subset.get(6).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}
		
		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subset.get(7).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}
		
		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subset.get(8).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}
		
		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subset.get(9).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}
		
		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subsetC.get(0).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}

		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subsetC.get(1).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}

		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subsetC.get(2).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}
		
		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subsetC.get(3).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}

		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subsetC.get(4).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}
		
		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subsetC.get(5).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}
		
		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subsetC.get(6).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}
		
		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subsetC.get(7).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}
		
		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subsetC.get(8).getNombre().replace(" ", ""));
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}
		
		try {
			tweetList = tweetRepository.findByKeywordsMatchingAll(subsetC.get(9).getNombre());
			int random = rand.nextInt(tweetList.size() - 1);
			tweets.add(tweetList.get(random));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}
		return tweets;
	}
}
