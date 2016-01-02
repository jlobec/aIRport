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
import es.fic.udc.riws.airport.flight.FlightResultAirportDto;
import es.fic.udc.riws.airport.flight.FlightResultCompanyDto;


@Controller
public class HomeController {
	
	@Autowired
	FlightRepository flightRepository;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Principal principal, Model model) throws IOException, ParseException {
		
		if (principal == null){
			return "home/homeNotSignedIn";
		}
		
		//Grafica barras (retrasos por aeropuerto)
		List<FlightResultAirportDto> datosVuelos = flightRepository.findMostDelayedAirports();
		List<FlightResultAirportDto> subset = datosVuelos.subList(0, 10);
		model.addAttribute("datosvuelos", subset);
		
		//Grafica companias
		List<FlightResultCompanyDto> datosCompanias = flightRepository.findMostDelayedCompanies();
		List<FlightResultCompanyDto> subsetC = datosCompanias.subList(0, 10);
		model.addAttribute("datoscomps", subsetC);
		
		return "home/homeSignedIn";
	}
}
