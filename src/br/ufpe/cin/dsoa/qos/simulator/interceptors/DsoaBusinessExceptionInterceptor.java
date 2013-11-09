package br.ufpe.cin.dsoa.qos.simulator.interceptors;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;

import br.ufpe.cin.dsoa.qos.simulator.business.BusinessExceptionSimulator;
import br.ufpe.cin.dsoa.qos.simulator.business.BusinessSimulator;
import br.ufpe.cin.dsoa.qos.simulator.parser.Interval;
import br.ufpe.cin.dsoa.qos.simulator.parser.QosAttribute;
import br.ufpe.cin.dsoa.qos.simulator.parser.Simulation;

public class DsoaBusinessExceptionInterceptor extends DsoaInterceptor {

	public static final String NAME = "BusinessException";

	private Map<Interval, BusinessSimulator> simulationMap = new HashMap<Interval, BusinessSimulator>();

	public DsoaBusinessExceptionInterceptor(QosAttribute businessException, Bundle bundle)
			throws ClassNotFoundException {

		BusinessSimulator simulator = null;
		Simulation simulation = businessException.getSimulation();
		List<Interval> intervals = simulation.getIntervals();

		int i = 1;
		long startTime = 0;

		for (Interval interval : intervals) {
			interval.setStartTime(startTime);
			interval.setStopTime(startTime + interval.getTime());
			startTime += interval.getTime();

			String exceptionClassName = interval.getExceptionClass();
			if (null != exceptionClassName) {
				Class<? extends Exception> exceptionClass = bundle.loadClass(exceptionClassName);
				simulator = new BusinessExceptionSimulator(exceptionClass);
				this.simulationMap.put(interval, simulator);
			}
			this.cycle = startTime;

			//System.out.println("Interval[" + i++ + "]: " + interval);

		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		long moment = System.currentTimeMillis();
		if (initTime == 0) {
			initTime = moment;
		}
		//System.out.println("===>> Moment: " + moment + "[" + (moment - initTime) % cycle + "]");

		BusinessSimulator simulator = null;

		if (!simulationMap.isEmpty()) {
			for (Interval interval : simulationMap.keySet()) {
				if (interval.contains((moment - initTime) % cycle)) {
					simulator = simulationMap.get(interval);
					//System.out.println("Interval: " + interval);
					System.out.println("Simulator: " + simulator);
				}
			}
		}

		if (simulator != null) {
			Class<? extends Exception> exceptionClass = simulator.getSimulatedBusinessException();
			Exception ex = exceptionClass.newInstance();
			throw ex;
		} else {
			return super.invoke(proxy, method, args);

		}

	}
}
