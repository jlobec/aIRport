package es.fic.udc.riws.airport.search;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.fic.udc.riws.airport.flight.FlightRepository;
import es.fic.udc.riws.airport.twitter.Tweet;
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
				String queryFiltered = query.replace("company:", "").replace("airport:", "").replace("flightcode:", "");
				String result = formatQuery(queryFiltered.split(" "));
				
				List<Tweet> tweetList = tweetRepository.findByKeywords(result);

				long seed = System.nanoTime();
				Collections.shuffle(tweetList, new Random(seed));
				
				model.addAttribute("tweets", tweetList.subList(0, Math.min(10, tweetList.size())));
				model.addAttribute("query", query);
				model.addAttribute("datosvuelos", flightRepository.findByCriteria(query));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	return"search/search";

	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(Principal principal, @ModelAttribute SearchForm searchForm)
			throws IOException, ParseException {

		return "redirect:/search?query=" + URLEncoder.encode(searchForm.getText(), "UTF-8");
	}
	
	private String formatQuery(String[] split) {
		String result = "";
		for (String string : split) {
			if (string.startsWith("\"")) {
				if (result.isEmpty()) {
					result = result + string + " ";
				} else {
					result = result + " AND " + string + " ";
				}
			} else {
				if (string.endsWith("\"")) {
					result = result + string;
				} else {
					if (result.isEmpty()) {
						result = result + string;
					} else {
						result = result + " AND " + string;
					}
				}
			}
		}
		return result;
	}

}
