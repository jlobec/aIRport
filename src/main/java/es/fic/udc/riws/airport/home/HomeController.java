package es.fic.udc.riws.airport.home;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.fic.udc.riws.airport.flight.FlightRepository;
import es.fic.udc.riws.airport.flight.FlightResult;


@Controller
public class HomeController {
	
	@Autowired
	FlightRepository flightRepository;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Principal principal, Model model) throws IOException, ParseException {
		
		if (principal == null){
			return "home/homeNotSignedIn";
		}
		List<FlightResult> datosVuelos = flightRepository.findMostDelayed("aeropuerto_nombre");
		List<FlightResult> subset = datosVuelos.subList(0, 10);
		model.addAttribute("datosvuelos", subset);
		
		return "home/homeSignedIn";
	}
}
