package es.fic.udc.riws.airport.indexing.tweet;

import java.io.IOException;
import java.security.Principal;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TweetIndexingController {

	@RequestMapping(value = "/api/indexartweets", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String index(Principal principal) throws IOException, ParseException {
		try{
			TweetIndexing.doIndex();
		} catch (Exception e){
			e.printStackTrace();
			return "{'indexing': 'ko'}";
		}
		return "{'indexing': 'ok'}";
	}
}
