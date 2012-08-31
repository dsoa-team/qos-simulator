package br.ufpe.cin.dsoa.qos.simulator.availability;

import java.util.List;

import br.ufpe.cin.dsoa.qos.simulator.parser.Parameter;

public class BernoulliAvailabilitySimulator implements AvailabilitySimulator {
	public static final String NAME = "bernoulli";
	private static final double DEFAULT_AVAILABILITY = 0.90;
	private double availability;

	public BernoulliAvailabilitySimulator(List<Parameter> parameters) {
		availability = DEFAULT_AVAILABILITY;
		for (Parameter parameter : parameters) {
			if (parameter.getName().equalsIgnoreCase("mean")) {
				availability = parameter.getValue() / 100;
				break;
			}
		}
	}

	@Override
	public boolean isAvailable() {
		double random = Math.random();
		if (random <= availability) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "BernoulliAvailabilitySimulator [availability=" + availability
				+ "]";
	}
	
}
