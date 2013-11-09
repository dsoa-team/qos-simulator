package br.ufpe.cin.dsoa.qos.simulator.business;

public class BusinessExceptionSimulator implements BusinessSimulator {

	private Class<? extends Exception> exceptionClass;
	
	public BusinessExceptionSimulator(Class<? extends Exception> exceptionClass) {
		this.exceptionClass = exceptionClass;
	}
	
	
	@Override
	public Class<? extends Exception> getSimulatedBusinessException() {
		return this.exceptionClass;
	}

}
