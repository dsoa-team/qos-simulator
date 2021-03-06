package br.ufpe.cin.dsoa.qos.simulator.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "services")
@XmlType(name = "ServicesType", propOrder = { "services" })
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceList {

	@XmlElement(name = "service")
	private List<Service> services;

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	@Override
	public String toString() {
		return "ServiceList [services=" + services + "]";
	}

	public static void main(String[] args) {
		/*
		 * List<Parameter> parameterList = new LinkedList<Parameter>();
		 * 
		 * Parameter parameter = new Parameter(); parameter.setName("mean");
		 * parameter.setValue(35);
		 * 
		 * parameterList.add(parameter);
		 * 
		 * Distribution distribution = new Distribution();
		 * distribution.setName("exponential");
		 * distribution.setParameters(parameterList);
		 * 
		 * Interval interval = new Interval(); interval.setTime(2000);
		 * interval.setUnit("ms"); interval.setDistribution(distribution);
		 * 
		 * Simulation simulation = new Simulation();
		 * simulation.addInterval(interval);
		 * 
		 * QosAttribute qosAvailability = new QosAttribute();
		 * qosAvailability.setName("availability");
		 * qosAvailability.setOperation("soma"); qosAvailability.setValue(95);
		 * qosAvailability.setSimulation(simulation);
		 * 
		 * List<QosAttribute> qosIn = new ArrayList<QosAttribute>();
		 * qosIn.add(qosAvailability);
		 * 
		 * Service calculadora = new Service();
		 * calculadora.setInterfaceName("br.ufpe.cin.exemplos.Calculadora");
		 * calculadora.setPid("calc-01"); calculadora.setQosAttributes(qosIn);
		 * 
		 * List<Service> serviceList = new ArrayList<Service>();
		 * serviceList.add(calculadora);
		 * 
		 * ServiceList services = new ServiceList();
		 * services.setServices(serviceList);
		 * 
		 * try { // specify where the generated XML schema will be created final
		 * File dir = new File("generated" + File.separator + "schema"); //
		 * specify a name for the generated XML instance document OutputStream
		 * os = new FileOutputStream("services.xml");
		 * 
		 * // create a JAXBContext for the PrintOrder class JAXBContext ctx =
		 * JAXBContext.newInstance(
		 * br.ufpe.cin.dsoa.qos.simulator.parser.ServiceList.class,
		 * br.ufpe.cin.dsoa.qos.simulator.parser.Service.class,
		 * br.ufpe.cin.dsoa.qos.simulator.parser.QosAttribute.class,
		 * br.ufpe.cin.dsoa.qos.simulator.parser.Simulation.class,
		 * br.ufpe.cin.dsoa.qos.simulator.parser.Interval.class,
		 * br.ufpe.cin.dsoa.qos.simulator.parser.Distribution.class,
		 * br.ufpe.cin.dsoa.qos.simulator.parser.Parameter.class);
		 * 
		 * // generate an XML schema from the annotated object model; create it
		 * // in the dir specified earlier under the default name, schema1.xsd
		 * ctx.generateSchema(new SchemaOutputResolver() {
		 * 
		 * @Override public Result createOutput(String namespaceUri, String
		 * schemaName) throws IOException { return new StreamResult(new
		 * File(dir, schemaName)); } });
		 * 
		 * // create a marshaller Marshaller marshaller =
		 * ctx.createMarshaller(); // the property JAXB_FORMATTED_OUTPUT
		 * specifies whether or not the // marshalled XML data is formatted with
		 * linefeeds and indentation
		 * marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); //
		 * marshall the data in the Java content tree // to the XML instance
		 * document niceVet.xml marshaller.marshal(services, os); } catch
		 * (FileNotFoundException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (PropertyException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch
		 * (JAXBException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 */

		try {
			JAXBContext jc = JAXBContext.newInstance(
					br.ufpe.cin.dsoa.qos.simulator.parser.ServiceList.class,
					br.ufpe.cin.dsoa.qos.simulator.parser.Service.class,
					br.ufpe.cin.dsoa.qos.simulator.parser.QosAttribute.class,
					br.ufpe.cin.dsoa.qos.simulator.parser.Simulation.class,
					br.ufpe.cin.dsoa.qos.simulator.parser.Interval.class,
					br.ufpe.cin.dsoa.qos.simulator.parser.Distribution.class,
					br.ufpe.cin.dsoa.qos.simulator.parser.Parameter.class);
			Unmarshaller u = jc.createUnmarshaller();
			ServiceList list = (ServiceList) u.unmarshal(new FileInputStream(
					"services.xml"));
			System.out.println(list);
			System.out.println(list.getServices());
			for (Service service : list.getServices()) {
				System.out.println("Pid: " + service.getPid());
				System.out.println("Interface: " + service.getInterfaceName());
				System.out.println("Atributes:");
				for (QosAttribute qos : service.getQosAttributes()) {
					System.out.println("\tName: " + qos.getName());
					System.out.println("\tValue: " + qos.getValue());
				}
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
