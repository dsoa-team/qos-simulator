package br.ufpe.cin.dsoa.qos.simulator.interceptors;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public abstract class DsoaInterceptor implements InvocationHandler {

	protected DsoaInterceptor next;

	public DsoaInterceptor getNext() {
		return this.next;
	}
	
	public void setNext(DsoaInterceptor next) {
		this.next = next;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		return (next == null) ? null : next.invoke(proxy, method, args);
	}
}
