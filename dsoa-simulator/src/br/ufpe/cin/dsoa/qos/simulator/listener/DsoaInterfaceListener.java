package br.ufpe.cin.dsoa.qos.simulator.listener;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.BundleTracker;

import br.ufpe.cin.dsoa.qos.simulator.interceptors.DsoaAvailabilityInterceptor;
import br.ufpe.cin.dsoa.qos.simulator.interceptors.DsoaInterceptor;
import br.ufpe.cin.dsoa.qos.simulator.interceptors.DsoaInterceptorChain;
import br.ufpe.cin.dsoa.qos.simulator.interceptors.DsoaResponseTimeInterceptor;
import br.ufpe.cin.dsoa.qos.simulator.parser.QosAttribute;
import br.ufpe.cin.dsoa.qos.simulator.parser.Service;
import br.ufpe.cin.dsoa.qos.simulator.parser.ServiceList;

public class DsoaInterfaceListener extends BundleTracker {

	private static final String CONFIGURATION_FILE = "/OSGI-INF/services.xml";

	private BundleContext context;
	private Unmarshaller unmarshaller;

	private final static Map<Class<?>, Object> defaultValues = new HashMap<Class<?>, Object>();

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

	public DsoaInterfaceListener(BundleContext context) {
		super(context, Bundle.ACTIVE, null);
		this.context = context;
		try {
			JAXBContext jc = JAXBContext.newInstance(
					br.ufpe.cin.dsoa.qos.simulator.parser.ServiceList.class,
					br.ufpe.cin.dsoa.qos.simulator.parser.Service.class,
					br.ufpe.cin.dsoa.qos.simulator.parser.QosAttribute.class);
			this.unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object addingBundle(Bundle bundle, BundleEvent event) {
		URL url = bundle.getEntry(CONFIGURATION_FILE);
		List<ServiceRegistration> regs = null;
		if (url != null) {
			ServiceList list = null;
			try {
				list = (ServiceList) this.unmarshaller.unmarshal(url);
				regs = new ArrayList<ServiceRegistration>(list.getServices()
						.size());
				for (Service service : list.getServices()) {
					regs.add(createService(bundle, service));
				}
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return regs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void removedBundle(Bundle bundle, BundleEvent event, Object regs) {
		List<ServiceRegistration> list = (List<ServiceRegistration>) regs;
		for (ServiceRegistration reg : list) {
			((ServiceRegistration) reg).unregister();
		}
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ServiceRegistration createService(Bundle bundle, Service service)
			throws ClassNotFoundException {
		Class clazz = bundle.loadClass(service.getInterfaceName());
		Dictionary properties = new Properties();
		properties.put("provider.pid", service.getPid());
		// adding_distributed_properties
		// FIXME: adicionar propriedades de forma mais parametrizada no xml
		// (services)
		if (null != service.getAddress()) {
			properties.put("service.exported.interfaces", "*");
			properties.put("service.exported.configs", "org.apache.cxf.ws");
			properties.put("org.apache.cxf.ws.address", service.getAddress());
		}
		Map<String, QosAttribute> qosMap = new HashMap<String, QosAttribute>();
		System.out.println(service);
		for (QosAttribute qos : service.getQosAttributes()) {
			qosMap.put(qos.getName(), qos);
			// + "." + qos.getDistribution()
			if (qos.getName().equals(DsoaAvailabilityInterceptor.NAME)) {
				properties.put(qos.getName(), qos.getRegistredQos());
			} else {
				properties.put(qos.getOperation() + "." + qos.getName(),
						qos.getRegistredQos());
			}
		}

		DsoaInterceptorChain chain = new DsoaInterceptorChain();
		if (null == service.getClassName()) {
			chain.add(new InvocationHandler());
		} else {
			try {
				Class c = bundle.loadClass(service.getClassName());
				Object instance = c.newInstance();
				chain.add(new InvocationHandler(instance));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				chain.add(new InvocationHandler());
				e.printStackTrace();
			}
		}

		if (qosMap.get(DsoaResponseTimeInterceptor.NAME) != null) {
			QosAttribute attrs = qosMap.get(DsoaResponseTimeInterceptor.NAME);
			DsoaInterceptor interceptor = new DsoaResponseTimeInterceptor(
					service, attrs.getSimulatedQos());
			chain.add(interceptor);
			try {
				this.registerSimulatorMbean(interceptor, 
						new ObjectName("qos.simulator:Type=DsoaResponseTimeInterceptor,Pid="+service.getPid()));
			} catch (MalformedObjectNameException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}

		if (qosMap.get(DsoaAvailabilityInterceptor.NAME) != null) {
			QosAttribute attrs = qosMap.get(DsoaAvailabilityInterceptor.NAME);
			DsoaInterceptor interceptor = new DsoaAvailabilityInterceptor(
					service, attrs.getSimulatedQos(), attrs.getTimeout());
			chain.add(interceptor);
			try {
				this.registerSimulatorMbean(interceptor, 
						new ObjectName("qos.simulator:Type=DsoaAvailabilityInterceptor,Pid="+service.getPid()));
			} catch (MalformedObjectNameException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}

		Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class[] { clazz }, chain);
		return this.context.registerService(service.getInterfaceName(), proxy,
				properties);
	}

	@SuppressWarnings("unchecked")
	public void unregisterServices() {
		Bundle[] bundles = this.getBundles();
		System.out.println(bundles);
		System.out.println(bundles.length);
		if (bundles != null && bundles.length > 0) {
			for (Bundle bundle : bundles) {
				List<ServiceRegistration> list = (List<ServiceRegistration>) this
						.getObject(bundle);
				for (ServiceRegistration reg : list) {
					((ServiceRegistration) reg).unregister();
				}
			}
		}
	}

	// TODO: lógica para desregistrar mbeam
	private void registerSimulatorMbean(DsoaInterceptor interceptor,
			ObjectName name) {
		try {
			ManagementFactory.getPlatformMBeanServer().registerMBean(
					interceptor, name);
		} catch (InstanceAlreadyExistsException e) {
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			e.printStackTrace();
		}
	}

	class InvocationHandler extends DsoaInterceptor {

		private Object instance;

		public InvocationHandler() {
		}

		public InvocationHandler(Object instance) {
			this.instance = instance;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {

			if (instance != null) {
				return method.invoke(instance, args);
			} else {
				return defaultValues.get(method.getReturnType());
			}
		}

	}

}