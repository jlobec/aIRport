package es.fic.udc.riws.airport.home;

import java.io.IOException;
import java.security.Principal;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import es.fic.udc.riws.airport.simpledemo.SimpleDemo;

@Controller
public class HomeController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Principal principal) throws IOException, ParseException {
		SimpleDemo demo = new SimpleDemo();
		demo.index();
		return principal != null ? "home/homeSignedIn" : "home/homeNotSignedIn";
	}
}
