package br.ufpe.cin.dsoa.qos.simulator.listener;

import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.BundleTracker;

import br.ufpe.cin.dsoa.qos.simulator.interceptors.DsoaInterceptorChain;
import br.ufpe.cin.dsoa.qos.simulator.parser.QosAttribute;
import br.ufpe.cin.dsoa.qos.simulator.parser.Service;
import br.ufpe.cin.dsoa.qos.simulator.parser.ServiceList;

public class DsoaInterfaceListener extends BundleTracker {

	private static final String CONFIGURATION_FILE = "/OSGI-INF/services.xml";
	private static Logger logger = Logger.getLogger(DsoaInterfaceListener.class
			.getSimpleName());

	private BundleContext context;
	private Unmarshaller unmarshaller;

	public DsoaInterfaceListener(BundleContext context) {
		super(context, Bundle.ACTIVE, null);
		this.context = context;
		try {
			JAXBContext jc = JAXBContext.newInstance(
					br.ufpe.cin.dsoa.qos.simulator.parser.ServiceList.class,
					br.ufpe.cin.dsoa.qos.simulator.parser.Service.class,
					br.ufpe.cin.dsoa.qos.simulator.parser.QosAttribute.class,
					br.ufpe.cin.dsoa.qos.simulator.parser.Simulation.class,
					br.ufpe.cin.dsoa.qos.simulator.parser.Interval.class,
					br.ufpe.cin.dsoa.qos.simulator.parser.Distribution.class,
					br.ufpe.cin.dsoa.qos.simulator.parser.Parameter.class);
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
				logger.info("===>>> Service list: " + list);
				regs = new ArrayList<ServiceRegistration>(list.getServices()
						.size());
				long initTime = System.currentTimeMillis();
				for (Service service : list.getServices()) {
					logger.info("==>> Service: " + service);
					regs.add(createService(bundle, service, initTime));
				}
				System.out.println("==>> Simulation started at " + initTime);
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
	
	
	@SuppressWarnings("unchecked")
	public void unregisterServices() {
		Bundle[] bundles = this.getBundles();
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

	/**
	 * Steps required to create a simulated service:
	 * 	1. 	Load the service class (which is dynamically imported)
	 * 	2. 	Build registration properties, including AvgAvailability and ResponseTime as described on the service.xml descriptor.
	 * 	3. 	Build an interceptor chain that is responsible for simulating quality metrics (nowadays, two metric can be simulated
	 * 		AvgResponseTime and AvgAvailability (they shall have this name).
	 *  4. 	Creating and registering a Service Proxy that will forward requests through the built chain.
	 *  
	 * @param bundle
	 * @param service
	 * @param startTime
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "rawtypes" })
	private ServiceRegistration createService(Bundle bundle, Service service, long startTime)
			throws ClassNotFoundException {
		Class clazz = this.context.getBundle().loadClass(service.getInterfaceName());
		Dictionary properties = this.buildRegistryMetadata(service);
		DsoaInterceptorChain chain = this.buildInterceptorChain(bundle, service, startTime);
		Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class[] { clazz }, chain);
		return this.context.registerService(service.getInterfaceName(), proxy,
				properties);
	}

	/**
	 * Create service's metadata which will be included in the OSGi registry.
	 * 
	 * @param service
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Dictionary buildRegistryMetadata(Service service) {
		Dictionary properties = new Properties();
		properties.put("provider.pid", service.getPid());
		properties.put("service.managed", true);
		// adding_distributed_properties
		// FIXME: adicionar propriedades de forma mais parametrizada no xml
		// (services)
		if (null != service.getAddress()) {
			properties.put("service.exported.interfaces", "*");
			properties.put("service.exported.configs", "org.apache.cxf.ws");
			properties.put("org.apache.cxf.ws.address", service.getAddress());
		}

		logger.info("Registering service: " + service);
		for (QosAttribute qos : service.getQosAttributes()) {
			logger.info("QoSAttribute: " + qos);
			StringBuffer propertyNameBuf = new StringBuffer("constraint");
			
			String operation = qos.getOperation();
			if (operation != null) {
				propertyNameBuf.append(".").append("operation").append("."); 
			} else {
				propertyNameBuf.append(".").append("service").append("."); 
			}
			propertyNameBuf.append(qos.getName());
			if (operation != null) {
				propertyNameBuf.append(".").append(operation).append(".");
			}
			propertyNameBuf.append(qos.getExpression());
			properties.put(propertyNameBuf.toString(), qos.getValue());
		}
		return properties;
	}

	private DsoaInterceptorChain buildInterceptorChain(Bundle bundle,
			Service service, long startTime) throws ClassNotFoundException {
		return new DsoaInterceptorChain(bundle, service, startTime);
	}
}