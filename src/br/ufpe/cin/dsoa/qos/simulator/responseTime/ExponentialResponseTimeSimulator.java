package br.ufpe.cin.dsoa.qos.simulator.responseTime;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.random.RandomGenerator;

import br.ufpe.cin.dsoa.qos.simulator.parser.Parameter;

public class ExponentialResponseTimeSimulator implements ResponseTimeSimulator {
	public static final String NAME = "exponential";
	public static final long DEFAULT_TIME = 1000;
	private RandomData randomData;
	private long simulatedTime;

	public ExponentialResponseTimeSimulator(RandomData randomData, List<Parameter> parameters) {
		// Simulated default time = 1 sec.
		simulatedTime = DEFAULT_TIME;
		for (Parameter parameter : parameters) {
			if (parameter.getName().equalsIgnoreCase("mean")) {
				simulatedTime = Math.round(parameter.getValue());
				break;
			}
		}
		this.randomData = randomData; 
	}

	@Override
	public long getSimulatedTime() {
		// TODO Auto-generated method stub
		return Math.round(randomData.nextExponential(simulatedTime));
	}

	@Override
	public String toString() {
		return "ExponentialResponseTimeSimulator [expGenRespTime="
				+ simulatedTime + "]";
	}
	
	public static void main(String[] args) {
		RandomGenerator gen = new JDKRandomGenerator();
		gen.setSeed(1000);
		RandomData randomData = new RandomDataImpl(gen);
		Parameter meanPar = new Parameter();
		meanPar.setName("mean");
		meanPar.setValue(500);
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(meanPar);
		ExponentialResponseTimeSimulator rtSim = new ExponentialResponseTimeSimulator(randomData, parameters);
		for (int i = 1; i <= 1000; i++) {
			System.out.println(rtSim.getSimulatedTime());
		}
	}
	
}