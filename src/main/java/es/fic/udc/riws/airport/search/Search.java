package es.fic.udc.riws.airport.search;

import java.io.IOException;
import java.security.Principal;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.fic.udc.riws.airport.flight.FlightRepository;
import es.fic.udc.riws.airport.twitter.TweetRepository;

@Controller
public class Search {

	@Autowired
	TweetRepository tweetRepository;
	
	@Autowired
	FlightRepository flightRepository;

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String index(Principal principal, Model model,
			@RequestParam(value = "query", required = false) String query) {

		model.addAttribute(new SearchForm());
		if (query != null) {
			try {
				model.addAttribute("tweets", tweetRepository.findByKeywords(query));
				model.addAttribute("query", query);
				model.addAttribute("datosvuelos", flightRepository.findByCriteria(query));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "search/search";
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(Principal principal, @ModelAttribute SearchForm searchForm)
			throws IOException, ParseException {

		return "redirect:/search?query=" + searchForm.getText();
	}
}
