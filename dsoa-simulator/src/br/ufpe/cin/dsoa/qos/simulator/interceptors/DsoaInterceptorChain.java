package br.ufpe.cin.dsoa.qos.simulator.interceptors;

import java.lang.reflect.Method;

public class DsoaInterceptorChain extends DsoaInterceptor {
	private DsoaInterceptor current;
	
	public void add(DsoaInterceptor next) {
		if (current != null) {
			next.setNext(current);
		}
		current = next;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		return current.invoke(proxy, method, args);
	}
}
