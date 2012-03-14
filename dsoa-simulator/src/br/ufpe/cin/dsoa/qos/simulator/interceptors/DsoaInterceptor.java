package br.ufpe.cin.dsoa.qos.simulator.interceptors;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public abstract class DsoaInterceptor implements InvocationHandler {

	protected DsoaInterceptor next;
	protected long initTime;
	protected long cycle;

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
