package br.ufpe.cin.dsoa.qos.simulator.interceptors;



public class TestAvailability {

/*	static Logger log = Logger.getLogger("Availability test");
	@Override
	public Object invoke(Object... params) {
		//log.info(" Testing...");
		return "Ok";
	}*/

	public static void main(String[] args) {
		/*DsoaQoSInterceptor interceptor = new DsoaAvailabilityInterceptor(
				new TestAvailability(), 20000, 5000);
		Object monit = new Object();
		synchronized (monit) {
			while (true) {
				try {
					interceptor.invoke("Test");
					monit.wait(500);
					log.info("Available: "
							+ DsoaAvailabilityInterceptor.counterAv);
					log.info("Unavailable: "
							+ DsoaAvailabilityInterceptor.counterUnv);
					log.info("Total: "
							+ (DsoaAvailabilityInterceptor.counterAv + DsoaAvailabilityInterceptor.counterUnv));
					// interceptor.wait(500);
				} catch (Exception e) {
				//	System.out.println("OOOOOOOOPPPPPPPPPPPPSSSSSSSSSSSSSS");
					e.printStackTrace();
					try {
						monit.wait(500);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		}
*/
	}
}
