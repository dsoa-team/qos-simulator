package br.ufpe.cin.dsoa.qos.simulator.interceptors;

public interface DsoaAvailabilityInterceptorMBean {

	public void setTimeout(double timeout);

	public void setAvailability(double availability);

	public void setInterval(long interval);

	public double getTimeout();

	public double getAvailability();

	public long getInterval();

}
