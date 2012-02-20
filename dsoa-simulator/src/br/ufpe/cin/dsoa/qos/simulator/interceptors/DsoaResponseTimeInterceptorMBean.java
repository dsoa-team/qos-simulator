package br.ufpe.cin.dsoa.qos.simulator.interceptors;

public interface DsoaResponseTimeInterceptorMBean {

	public void setResponseTime(double responseTime);
	public double getResponseTime();
	
}
