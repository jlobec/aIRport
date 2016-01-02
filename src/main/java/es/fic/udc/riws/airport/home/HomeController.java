package es.fic.udc.riws.airport.home;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

		// Obtenemos un tweet por cada uno de las 3 companias con mas retrasos
		model.addAttribute("tweetComp1", tweets.get(3).getText());
		model.addAttribute("tweetComp2", tweets.get(4).getText());
		model.addAttribute("tweetComp3", tweets.get(5).getText());

		return "home/homeSignedIn";
	}

	private List<Tweet> getTweetsByCompanyAndAirport(List<FlightResultCompanyDto> subsetC,
			List<FlightResultAirportDto> subset) {

		List<Tweet> tweets = new ArrayList<>();
		try {
			tweets.add(tweetRepository.findByKeywordsMatchingAll(subset.get(0).getNombre()).get(0));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}

		try {
			tweets.add(tweetRepository.findByKeywordsMatchingAll(subset.get(1).getNombre()).get(0));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}

		try {
			tweets.add(tweetRepository.findByKeywordsMatchingAll(subset.get(2).getNombre()).get(0));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}

		try {
			tweets.add(tweetRepository.findByKeywordsMatchingAll(subsetC.get(0).getNombre()).get(0));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}

		try {
			tweets.add(tweetRepository.findByKeywordsMatchingAll(subsetC.get(1).getNombre()).get(0));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}

		try {
			tweets.add(tweetRepository.findByKeywordsMatchingAll(subsetC.get(2).getNombre()).get(0));
		} catch (Exception e) {
			tweets.add(new Tweet("", new Date()));
		}
		return tweets;
	}
}
