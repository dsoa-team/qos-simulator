package br.ufpe.cin.dsoa.qos.simulator.interceptors;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public abstract class DsoaInterceptor implements InvocationHandler {

	protected DsoaInterceptor next;
	protected long initTime;
	protected long cycle;
	
	private static int instanceCounter = 0;
	private int id;
	
	public DsoaInterceptor(long initTime) {
		this.initTime = initTime;
		this.id = instanceCounter++;
	}
	
	public int getId() {
		return id;
	}
	
	public DsoaInterceptor getNext() {
		return this.next;
	}

	public void setNext(DsoaInterceptor next) {
		this.next = next;
	}

	public void start(long time) {
/*		initTime = time;
		if (next != null) {
			next.start(time);
		}*/
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		return (next == null) ? null : next.invoke(proxy, method, args);
	}
}
