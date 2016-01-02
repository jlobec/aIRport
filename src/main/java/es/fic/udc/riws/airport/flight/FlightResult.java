package es.fic.udc.riws.airport.flight;

import java.util.Comparator;

public class FlightResult implements Comparable<FlightResult>{

	private String campoClave;
	private Integer valor;

	public FlightResult(String campoClave, Integer valor) {
		this.campoClave = campoClave;
		this.valor = valor;
	}

	public String getCampoClave() {
		return campoClave;
	}

	public void setCampoClave(String campoClave) {
		this.campoClave = campoClave;
	}

	public Integer getValor() {
		return valor;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}

	@Override
	public int compareTo(FlightResult o) {
		return Comparators.VALOR.compare(this, o);
	}
	
	public static class Comparators {
		 public static Comparator<FlightResult> VALOR = new Comparator<FlightResult>() {
	            @Override
	            public int compare(FlightResult o1, FlightResult o2) {
	                return o2.getValor().compareTo(o1.getValor());
	            }
	        };
	}
	
	

}
