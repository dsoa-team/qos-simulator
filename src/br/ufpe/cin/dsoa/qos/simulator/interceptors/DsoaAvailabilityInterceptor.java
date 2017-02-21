package br.ufpe.cin.dsoa.qos.simulator.interceptors;

import java.lang.reflect.Method;
import java.net.ConnectException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.qos.simulator.availability.AvailabilitySimulator;
import br.ufpe.cin.dsoa.qos.simulator.availability.BernoulliAvailabilitySimulator;
import br.ufpe.cin.dsoa.qos.simulator.parser.Distribution;
import br.ufpe.cin.dsoa.qos.simulator.parser.Interval;
import br.ufpe.cin.dsoa.qos.simulator.parser.QosAttribute;
import br.ufpe.cin.dsoa.qos.simulator.parser.Simulation;

public class DsoaAvailabilityInterceptor extends DsoaInterceptor {
	public static final String NAME = "AvgAvailability";

	private Map<Interval, AvailabilitySimulator> simulationMap = new HashMap<Interval, AvailabilitySimulator>();
	private double timeout;

	public DsoaAvailabilityInterceptor(long initTime, QosAttribute availability) {
		super(initTime);
		Simulation simulation = availability.getSimulation();
		long startTime = 0;
		if(simulation != null){
			AvailabilitySimulator simulator = null;
			for (Interval interval : simulation.getIntervals()) {
				interval.setStartTime(startTime);
				long duration = interval.getTime();
				startTime += duration;
				interval.setStopTime(startTime);
				simulator = getSimulator(interval);
				this.simulationMap.put(interval, simulator);
			}
		}
		this.cycle = startTime;

		/*
		 * this.available = true; this.timeout = timeout; this.expGenMttf = new
		 * ExponentialDistributionImpl(mttf); this.expGenMttr = new
		 * ExponentialDistributionImpl(mttr);
		 */
	}

	private AvailabilitySimulator getSimulator(Interval interval) {
		Distribution distribution = interval.getDistribution();
		AvailabilitySimulator simulator = null;
		if (distribution != null) {
			if (BernoulliAvailabilitySimulator.NAME
					.equalsIgnoreCase(distribution.getName())) {
				simulator = new BernoulliAvailabilitySimulator(
						distribution.getParameters());
			} else {
				throw new InvalidParameterException(
						"Distribution not supported: " + distribution.getName());
			}
		} else {
			throw new InvalidParameterException("Distribution cannot be null");
		}
		return simulator;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		long moment = System.currentTimeMillis();
		if (initTime == 0) {
			initTime = moment;
		}
		AvailabilitySimulator simulator = null;
		
		if(!simulationMap.isEmpty()) {
			for (Interval interval : simulationMap.keySet()) {
				if (interval.contains((moment - initTime) % cycle)) {
					simulator = simulationMap.get(interval);
				}
			}
		}

		if (simulator == null || simulator.isAvailable()) {
			return super.invoke(proxy, method, args);
		} else {
			if (timeout > 0) {
				Thread.sleep(Math.round(timeout));
			}
			throw new RuntimeException("Simulated availability failure...");
		}
	}

}
