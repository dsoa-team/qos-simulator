package br.ufpe.cin.dsoa.qos.simulator.interceptors;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.random.RandomGenerator;

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

	private Logger logger;

	private FileHandler adaptationLogFile;

	// private Service service;
	// private double minimum;
	// private double maximum;

	public DsoaResponseTimeInterceptor(long initTime, QosAttribute responseTime) {
		super(initTime);
		RandomGenerator gen = new JDKRandomGenerator();
		gen.setSeed(1000);
		RandomData randomData = new RandomDataImpl(gen);
		Simulation simulation = responseTime.getSimulation();
		long startTime = 0;
		ResponseTimeSimulator simulator = null;
		for (Interval interval : simulation.getIntervals()) {
			interval.setStartTime(startTime);
			long duration = interval.getTime();
			startTime += duration;
			interval.setStopTime(startTime);
			simulator = getSimulator(randomData, interval);
			this.simulationMap.put(interval, simulator);
			//System.out.println("Interval[" + i++ + "]: " + interval);
		}
		this.cycle = startTime;
		
		java.util.logging.Formatter f = new java.util.logging.Formatter() {

			public String format(LogRecord record) {
				StringBuilder builder = new StringBuilder(1000);
				builder.append(formatMessage(record));
				builder.append("\n");
				return builder.toString();
			}
		};
		logger = Logger.getLogger("SimulatorLog");
		try {
			adaptationLogFile = new FileHandler("simulator.log");
			adaptationLogFile.setFormatter(f);
			logger.addHandler(adaptationLogFile);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// this.service = service;
	}

	private ResponseTimeSimulator getSimulator(RandomData randomData, Interval interval) {
		Distribution distribution = interval.getDistribution();
		ResponseTimeSimulator simulator = null;
		if (distribution != null) {
			if (ExponentialResponseTimeSimulator.NAME
					.equalsIgnoreCase(distribution.getName())) {
				simulator = new ExponentialResponseTimeSimulator(randomData,
						distribution.getParameters());
			} else if (UniformResponseTimeSimulator.NAME.equalsIgnoreCase(distribution.getName())) {
				simulator = new UniformResponseTimeSimulator(randomData, distribution.getParameters());
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
			logger.info("Start time: " + initTime);
		}
		//System.out.println("===>> Moment: " + moment + "[" + (moment - initTime) % cycle + "]");
		ResponseTimeSimulator simulator = null;
		for (Interval interval : simulationMap.keySet()) {
			if (interval.contains((moment - initTime) % cycle)) {
				simulator = simulationMap.get(interval);
				break;
			}
		}
		long simulatedTime = Math.round(simulator.getSimulatedTime());
		while (simulatedTime == 0) {
			simulatedTime = Math.round(simulator.getSimulatedTime());
		}
		logger.info("Moment: " + (moment - initTime) % cycle + ", " + simulatedTime);
		Thread.sleep(simulatedTime);
		retorno = super.invoke(proxy, method, args);
		return retorno;
	}
}
