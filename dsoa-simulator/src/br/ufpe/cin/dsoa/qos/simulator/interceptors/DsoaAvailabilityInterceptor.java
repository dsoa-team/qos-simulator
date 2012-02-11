package br.ufpe.cin.dsoa.qos.simulator.interceptors;

import java.lang.reflect.Method;
import java.net.ConnectException;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.ExponentialDistributionImpl;

import br.ufpe.cin.dsoa.qos.simulator.parser.Service;

public class DsoaAvailabilityInterceptor extends DsoaInterceptor {
	public static final String NAME = "Availability";
	private Boolean available;
	private double timeout;

	private ExponentialDistributionImpl expGenMttf;
	private ExponentialDistributionImpl expGenMttr;

	public DsoaAvailabilityInterceptor(Service service, double availability,
			double timeout) {

		long interval = 100;
		long mttf = Math.round(availability * interval);
		long mttr = Math.round((100 - availability) * interval);

		this.available = true;
		this.timeout = timeout;
		this.expGenMttf = new ExponentialDistributionImpl(mttf);
		this.expGenMttr = new ExponentialDistributionImpl(mttr);
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

}
