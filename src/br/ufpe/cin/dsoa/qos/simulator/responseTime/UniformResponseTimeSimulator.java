package br.ufpe.cin.dsoa.qos.simulator.responseTime;

import java.util.List;

import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;

import br.ufpe.cin.dsoa.qos.simulator.parser.Parameter;

public class UniformResponseTimeSimulator implements ResponseTimeSimulator {
	public static final String NAME = "uniform";
	private int minimum = 0;
	private int maximum = 0;
	private RandomDataImpl randomNumberGenerator;

	public UniformResponseTimeSimulator(RandomData randomData, List<Parameter> parameters) {
		for (Parameter parameter : parameters) {
			if (parameter.getName().equalsIgnoreCase("minimum")) {
				minimum = (int) Math.round(parameter.getValue());
			} else if (parameter.getName().equalsIgnoreCase("maximum")) {
				maximum = (int) Math.round(parameter.getValue());
			}
		}
		if (minimum <= 0 || maximum <= 0) {
			throw new IllegalArgumentException(
					"Minimum and maximum must be greater than zero!");
		}
		randomNumberGenerator = new RandomDataImpl();
	}

	@Override
	public long getSimulatedTime() {
		// TODO Auto-generated method stub
		return randomNumberGenerator.nextInt(minimum, maximum);
	}

	@Override
	public String toString() {
		return "UniformResponseTimeSimulator [min=" 
				+ minimum + ", max=" + maximum + "]";
	}

}