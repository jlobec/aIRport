package es.fic.udc.riws.airport.flight;

import java.util.Comparator;

public class FlightResultAirportDto implements Comparable<FlightResultAirportDto> {

	private String nombre;
	private Integer valorDepartures;
	private Integer valorArrivals;

	public FlightResultAirportDto(String nombre, Integer valorDepartures, Integer valorArrivals) {
		this.nombre = nombre;
		this.valorDepartures = valorDepartures;
		this.valorArrivals = valorArrivals;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Integer getValorDepartures() {
		return valorDepartures;
	}

	public void setValorDepartures(Integer valorDepartures) {
		this.valorDepartures = valorDepartures;
	}

	public Integer getValorArrivals() {
		return valorArrivals;
	}

	public void setValorArrivals(Integer valorArrivals) {
		this.valorArrivals = valorArrivals;
	}

	@Override
	public int compareTo(FlightResultAirportDto o) {
		return Comparators.VALORES.compare(this, o);
	}

	public static class Comparators {
		public static Comparator<FlightResultAirportDto> VALORES = new Comparator<FlightResultAirportDto>() {
			@Override
			public int compare(FlightResultAirportDto o1, FlightResultAirportDto o2) {
				Integer b = o2.getValorDepartures()+o2.getValorArrivals();
				Integer a = o1.getValorDepartures()+o1.getValorArrivals();
				return b.compareTo(a);
//				int primeraComp = o2.getValorDepartures().compareTo(o1.getValorDepartures());
//				if  ( primeraComp != 0){
//					return primeraComp;
//				} else {
//					return o2.getValorArrivals().compareTo(o1.getValorArrivals());
//				}
			}
		};
	}

}
