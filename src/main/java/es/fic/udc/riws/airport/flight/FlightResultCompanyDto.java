package es.fic.udc.riws.airport.flight;

import java.util.Comparator;

public class FlightResultCompanyDto implements Comparable<FlightResultCompanyDto> {

	private String nombre;
	private Float porcentRetrasos;

	public FlightResultCompanyDto(String nombre, Float porcentRetrasos) {
		super();
		this.nombre = nombre;
		this.porcentRetrasos = porcentRetrasos;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Float getPorcentRetrasos() {
		return porcentRetrasos;
	}

	public void setPorcentRetrasos(Float porcentRetrasos) {
		this.porcentRetrasos = porcentRetrasos;
	}

	@Override
	public int compareTo(FlightResultCompanyDto o) {
		return Comparators.PORCENTAJES.compare(this, o);
	}
	
	public static class Comparators {
		public static Comparator<FlightResultCompanyDto> PORCENTAJES = new Comparator<FlightResultCompanyDto>() {
			@Override
			public int compare(FlightResultCompanyDto o1, FlightResultCompanyDto o2) {
				return o2.getPorcentRetrasos().compareTo(o1.getPorcentRetrasos());
			}
		};
	}

}
