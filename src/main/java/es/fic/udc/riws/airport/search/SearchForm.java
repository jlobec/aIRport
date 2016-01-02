package es.fic.udc.riws.airport.search;

public class SearchForm {

	private String text;

	public SearchForm() {
	}

	public SearchForm(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
