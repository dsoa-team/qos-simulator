package br.ufpe.cin.dsoa.qos.simulator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.qos.simulator.listener.DsoaInterfaceListener;

public class Activator implements BundleActivator {

	private BundleContext ctx;
	private DsoaInterfaceListener listener;
	
	@Override
	public void start(BundleContext context) throws Exception {
		this.ctx = context;
		listener = new DsoaInterfaceListener(ctx);
		listener.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		listener.close();
		listener.unregisterServices();
	}

}
