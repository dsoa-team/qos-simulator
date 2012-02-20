package br.ufpe.cin.dsoa.qos.simulator.interceptors;

import java.lang.reflect.Method;

import org.apache.commons.math.distribution.ExponentialDistributionImpl;

import br.ufpe.cin.dsoa.qos.simulator.parser.Service;

public class DsoaResponseTimeInterceptor extends DsoaInterceptor implements
		DsoaResponseTimeInterceptorMBean {

	public static final String NAME = "ResponseTime";
	private double responseTime;

	private ExponentialDistributionImpl expGenRespTime;

	// private Service service;
	// private double minimum;
	// private double maximum;

	public DsoaResponseTimeInterceptor(Service service, double responseTime) {
		this.responseTime = responseTime;
		this.expGenRespTime = new ExponentialDistributionImpl(responseTime);
		// this.service = service;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object retorno = null;
		synchronized (this) {
			try {
				this.verifyMean();
				long waitTime = Math.round(expGenRespTime.sample());
				while (waitTime == 0) {
					this.verifyMean();
					waitTime = Math.round(expGenRespTime.sample());
				}
				wait(waitTime);
				retorno = super.invoke(proxy, method, args);
			} catch (Exception e) {
				System.out.println("EXCE��O NO RESPONSE TIME INTERCEPTOR!!!");
				e.printStackTrace();
			}
			return retorno;
		}
	}

	/**
	 * Verifica se houve mudança na propriedade tempo de resposta via JMX e faz
	 * modificação no gerador de variável aleatória)
	 * 
	 */
	private synchronized void verifyMean() {
		if (this.responseTime != this.expGenRespTime.getMean()) {
			this.expGenRespTime = new ExponentialDistributionImpl(
					this.responseTime);
		}
	}

	@Override
	public void setResponseTime(double responseTime) {
		this.responseTime = responseTime;

	}

	@Override
	public double getResponseTime() {
		return this.responseTime;
	}
}
