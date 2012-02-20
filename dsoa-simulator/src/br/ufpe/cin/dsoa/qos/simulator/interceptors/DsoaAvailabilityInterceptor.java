package br.ufpe.cin.dsoa.qos.simulator.interceptors;

import java.lang.reflect.Method;
import java.net.ConnectException;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.ExponentialDistributionImpl;

import br.ufpe.cin.dsoa.qos.simulator.parser.Service;

public class DsoaAvailabilityInterceptor extends DsoaInterceptor implements DsoaAvailabilityInterceptorMBean {
	public static final String NAME = "Availability";
	private Boolean available;
	private double timeout;
	private double availability;
	private long interval = 100;

	private ExponentialDistributionImpl expGenMttf;
	private ExponentialDistributionImpl expGenMttr;

	public DsoaAvailabilityInterceptor(Service service, double availability,
			double timeout) {

		this.available = true;
		this.timeout = timeout;
		this.availability = availability;

		this.expGenMttf = new ExponentialDistributionImpl(generateMttf());
		this.expGenMttr = new ExponentialDistributionImpl(generateMttr());
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (this.isAvailable()) {
			return super.invoke(proxy, method, args);
		} else {
			if (timeout > 0)
				Thread.sleep(Math.round(timeout));

			throw new ConnectException("Simulated availability failure...");
		}

	}

	public boolean isAvailable() {

		long requestTime = System.currentTimeMillis();
		try {
			this.verifyMean();
			this.checkAvailability(requestTime,
					Math.round(expGenMttf.sample()),
					Math.round(expGenMttr.sample()));
		} catch (MathException e) {
			e.printStackTrace();
		}

		return this.available;
	}

	/**
	 * true => available <br/>
	 * false => not available
	 */
	private void checkAvailability(long requestTime, long mttf, long mttr) {

		long cicle = mttf + mttr;
		this.setAvailable((requestTime % cicle > mttf) ? false : true);

		// System.out.println("requestMillis=" + requestTime);
		// System.out.println("cicle=" + cicle);
		// System.out.println("mttf=" + mttf);
		// System.out.println("mttr=" + mttr);
		// System.out.println("point=" + requestTime % cicle);
		// System.out.println("response="
		// + ((requestTime % cicle > mttf) ? false : true));

	}

	private void setAvailable(boolean b) {
		synchronized (available) {
			this.available = b;
		}
	}

	private long generateMttf() {
		return Math.round(availability * interval);
	}

	private long generateMttr() {
		return Math.round((100 - availability) * interval);

	}

	/**
	 * Verifica se houve mudança via JMX e faz modificação no gerador de
	 * variável aleatória).
	 * 
	 * Só é necessário verificar o mttf (mttr é complementar)
	 * 
	 */
	private synchronized void verifyMean() {
		long mttf = generateMttf();

		if (mttf != expGenMttf.getMean()) {
			expGenMttf = new ExponentialDistributionImpl(mttf);
			expGenMttr = new ExponentialDistributionImpl(generateMttr());
		}
	}

	@Override
	public void setTimeout(double timeout) {
		this.timeout = timeout;
	}

	@Override
	public void setAvailability(double availability) {
		this.availability = availability;
	}

	@Override
	public void setInterval(long interval) {
		this.interval = interval;
	}

	@Override
	public double getTimeout() {
		return timeout;
	}

	@Override
	public double getAvailability() {
		return availability;
	}

	@Override
	public long getInterval() {
		return interval;
	}

}
