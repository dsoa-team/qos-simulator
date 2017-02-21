package br.ufpe.cin.dsoa.qos.simulator.interceptors;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;

import br.ufpe.cin.dsoa.qos.simulator.parser.QosAttribute;
import br.ufpe.cin.dsoa.qos.simulator.parser.Service;

public class DsoaInterceptorChain extends DsoaInterceptor {

	private final static Map<Class<?>, Object> defaultValues = new HashMap<Class<?>, Object>();
	private DsoaInterceptor currentInterceptor;
	private Bundle bundle;

	static {
		defaultValues.put(String.class, "");
		defaultValues.put(Integer.class, 0);
		defaultValues.put(int.class, 0);
		defaultValues.put(Long.class, 0L);
		defaultValues.put(long.class, 0L);
		defaultValues.put(Character.class, '\0');
		defaultValues.put(char.class, '\0');
		defaultValues.put(float.class, 0F);
		defaultValues.put(Float.class, 0F);
		defaultValues.put(double.class, 0d);
		defaultValues.put(Double.class, 0d);
	}

	public DsoaInterceptorChain(Bundle bundle, Service service, long initTime) throws ClassNotFoundException {
		super(initTime);
		this.bundle = bundle;
		this.addService(service);
/*	TODO JUST FOR EVALUATION
		Map<String, QosAttribute> qosMap = new HashMap<String, QosAttribute>();
		for (QosAttribute qos : service.getQosAttributes()) {
			qosMap.put(qos.getName().toLowerCase(), qos);
		}
		
		QosAttribute responseTime = qosMap.get(DsoaResponseTimeInterceptor.NAME
				.toLowerCase());
		if (responseTime != null) {
			this.add(new DsoaResponseTimeInterceptor(initTime, responseTime));
		}
		
		QosAttribute businessException = qosMap.get(DsoaBusinessExceptionInterceptor.NAME.toLowerCase());
		if(businessException != null) {
			this.add(new DsoaBusinessExceptionInterceptor(this.initTime,businessException, bundle));
		}

		QosAttribute availability = qosMap.get(DsoaAvailabilityInterceptor.NAME
				.toLowerCase());
		if (availability != null) {
			this.add(new DsoaAvailabilityInterceptor(this.initTime,availability));
		}
	*/	
	}

	public void addService(Service service) {
		// Se service.getClassName()==null => Deve-se criar um proxy para fazer
		// um mock do servico.
		if (null == service.getClassName()) {
			this.add(new InvocationHandler(this.initTime));
		} else {
			try {
				// A classe do servi�o deve sempre possuir um contrutor default
				Class c = bundle.loadClass(service.getClassName());
				Object instance = c.newInstance();
				this.add(new InvocationHandler(this.initTime, instance));
			} catch (Exception e) {
				this.add(new InvocationHandler(this.initTime));
				e.printStackTrace();
			}
		}
	}

	public void add(DsoaInterceptor next) {
		if (currentInterceptor != null) {
			next.setNext(currentInterceptor);
		}
		currentInterceptor = next;
	}

	public void start() {
		currentInterceptor.start(System.currentTimeMillis());
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return currentInterceptor.invoke(proxy, method, args);
	}

	class InvocationHandler extends DsoaInterceptor {

		private Object instance;

		public InvocationHandler(long initTime) {
			super(initTime);
		}

		public InvocationHandler(long initTime, Object instance) {
			super(initTime);
			this.instance = instance;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

			if (instance != null) {
				return method.invoke(instance, args);
			} else {
				return defaultValues.get(method.getReturnType());
			}
		}

	}
}
