package br.ufpe.cin.dsoa.qos.simulator.interceptors;

import java.lang.reflect.Method;

import org.apache.commons.math.distribution.ExponentialDistributionImpl;

import br.ufpe.cin.dsoa.qos.simulator.parser.Service;

public class DsoaResponseTimeInterceptor extends DsoaInterceptor {
	
	public static final String NAME = "ResponseTime";
	
	private ExponentialDistributionImpl expGenRespTime;

	//private Service service;
	//private double minimum;
	//private double maximum;

	public DsoaResponseTimeInterceptor(Service service, double responseTime) {
		this.expGenRespTime = new ExponentialDistributionImpl(responseTime);
		//this.service = service;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object retorno = null;
		synchronized (this) {
			try {
				long waitTime = Math.round(expGenRespTime.sample());
				while (waitTime == 0) {
					waitTime = Math.round(expGenRespTime.sample());
				}
				wait(waitTime);
				retorno = super.invoke(proxy, method, args);
			} catch (Exception e) {
				System.out.println("EXCEÇÃO NO RESPONSE TIME INTERCEPTOR!!!");
				e.printStackTrace();
			}
			return retorno;
		}
	}
}
