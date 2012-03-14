package br.ufpe.cin.dsoa.qos.simulator.responseTime;

import java.util.List;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.ExponentialDistributionImpl;

import br.ufpe.cin.dsoa.qos.simulator.parser.Parameter;

public class ExponentialResponseTimeSimulator implements ResponseTimeSimulator {
	public static final String NAME = "exponential";
	public static final long DEFAULT_TIME = 1000;
	private ExponentialDistributionImpl expGenRespTime;

	public ExponentialResponseTimeSimulator(List<Parameter> parameters) {
		// Simulated default time = 1 sec.
		long simulatedTime = DEFAULT_TIME;
		for (Parameter parameter : parameters) {
			if (parameter.getName().equalsIgnoreCase("mean")) {
				simulatedTime = Math.round(parameter.getValue());
				break;
			}
		}
		this.expGenRespTime = new ExponentialDistributionImpl(simulatedTime);
	}

	@Override
	public long getSimulatedTime() {
		// TODO Auto-generated method stub
		try {
			return Math.round(this.expGenRespTime.sample());
		} catch (MathException e) {
			e.printStackTrace();
			return DEFAULT_TIME;
		}
	}

	@Override
	public String toString() {
		return "ExponentialResponseTimeSimulator [expGenRespTime="
				+ expGenRespTime + "]";
	}
	
}