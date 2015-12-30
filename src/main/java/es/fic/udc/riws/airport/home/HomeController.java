package es.fic.udc.riws.airport.home;

import java.io.IOException;
import java.security.Principal;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class HomeController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Principal principal) throws IOException, ParseException {
		return principal != null ? "home/homeSignedIn" : "home/homeNotSignedIn";
	}
}
