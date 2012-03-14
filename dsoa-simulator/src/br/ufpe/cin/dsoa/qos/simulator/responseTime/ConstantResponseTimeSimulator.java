package br.ufpe.cin.dsoa.qos.simulator.responseTime;

public class ConstantResponseTimeSimulator implements ResponseTimeSimulator {

	private long constantTime;
	private static final long DEFAULT = 1000;

	public ConstantResponseTimeSimulator(double d) {
		super();
		this.constantTime = Math.round((d == 0) ? DEFAULT : d);
	}

	@Override
	public long getSimulatedTime() {
		// TODO Auto-generated method stub
		return this.constantTime;
	}

	@Override
	public String toString() {
		return "ConstantResponseTimeSimulator [constantTime=" + constantTime
				+ "]";
	}

}
