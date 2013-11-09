package br.ufpe.cin.dsoa.qos.simulator.interceptors;

import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.qos.simulator.parser.Distribution;
import br.ufpe.cin.dsoa.qos.simulator.parser.Interval;
import br.ufpe.cin.dsoa.qos.simulator.parser.QosAttribute;
import br.ufpe.cin.dsoa.qos.simulator.parser.Simulation;
import br.ufpe.cin.dsoa.qos.simulator.responseTime.ConstantResponseTimeSimulator;
import br.ufpe.cin.dsoa.qos.simulator.responseTime.ExponentialResponseTimeSimulator;
import br.ufpe.cin.dsoa.qos.simulator.responseTime.ResponseTimeSimulator;
import br.ufpe.cin.dsoa.qos.simulator.responseTime.UniformResponseTimeSimulator;

public class DsoaResponseTimeInterceptor extends DsoaInterceptor {

	public static final String NAME = "AvgResponseTime";

	private Map<Interval, ResponseTimeSimulator> simulationMap = new HashMap<Interval, ResponseTimeSimulator>();

	// private Service service;
	// private double minimum;
	// private double maximum;

	public DsoaResponseTimeInterceptor(QosAttribute responseTime) {
		Simulation simulation = responseTime.getSimulation();
		long startTime = 0;
		ResponseTimeSimulator simulator = null;
		int i = 1;
		//System.out.println("===============================================");
		//System.out.println("==>> Time: " + System.currentTimeMillis());
		for (Interval interval : simulation.getIntervals()) {
			interval.setStartTime(startTime);
			long duration = interval.getTime();
			startTime += duration;
			interval.setStopTime(startTime);
			simulator = getSimulator(interval);
			this.simulationMap.put(interval, simulator);
			//System.out.println("Interval[" + i++ + "]: " + interval);
		}
		this.cycle = startTime;
		// this.service = service;
	}

	private ResponseTimeSimulator getSimulator(Interval interval) {
		Distribution distribution = interval.getDistribution();
		ResponseTimeSimulator simulator = null;
		if (distribution != null) {
			if (ExponentialResponseTimeSimulator.NAME
					.equalsIgnoreCase(distribution.getName())) {
				simulator = new ExponentialResponseTimeSimulator(
						distribution.getParameters());
			} else if (UniformResponseTimeSimulator.NAME.equalsIgnoreCase(distribution.getName())) {
				simulator = new UniformResponseTimeSimulator(distribution.getParameters());
			}else {
				throw new InvalidParameterException(
						"Distribution not supported: " + distribution.getName());
			}
		} else {
			simulator = new ConstantResponseTimeSimulator(interval.getValue());
		}

		return simulator;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object retorno = null;
		long moment = System.currentTimeMillis();
		if (initTime == 0) {
			initTime = moment;
		}
		//System.out.println("===>> Moment: " + moment + "["
		//		+ (moment - initTime) % cycle + "]");
		ResponseTimeSimulator simulator = null;
		for (Interval interval : simulationMap.keySet()) {
			if (interval.contains((moment - initTime) % cycle)) {
				simulator = simulationMap.get(interval);
				//System.out.println("Interval: " + interval);
				System.out.println("Simulator: " + simulator);
			}
		}
		long simulatedTime = Math.round(simulator.getSimulatedTime());
		while (simulatedTime == 0) {
			simulatedTime = Math.round(simulator.getSimulatedTime());
		}
		Thread.sleep(simulatedTime);
		retorno = super.invoke(proxy, method, args);
		return retorno;
	}
}
