package br.ufpe.cin.dsoa.qos.simulator.parser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "QosAttributeType")
@XmlAccessorType(XmlAccessType.FIELD)
public class QosAttribute {

	@XmlAttribute
	private String name;
	@XmlAttribute(required = false)
	private String operation;
	@XmlAttribute(required = false)
	private String distribution;
	@XmlAttribute
	private double registredQos;
	@XmlAttribute
	private double simulatedQos;
	@XmlAttribute(required = false)
	private double timeout;

	public double getTimeout() {
		return timeout;
	}

	public void setTimeout(double timeout) {
		this.timeout = timeout;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getRegistredQos() {
		return registredQos;
	}

	public void setRegistredQos(double providedQos) {
		this.registredQos = providedQos;
	}

	public double getSimulatedQos() {
		return simulatedQos;
	}

	public void setSimulatedQos(double simulatedQos) {
		this.simulatedQos = simulatedQos;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}

	public String getDistribution() {
		return distribution;
	}

	@Override
	public String toString() {
		return "QosAttribute [name="
				+ name
				+ (operation == null ? "" : ", operation=" + operation)
				+ (distribution == null ? "" : ", distribution=" + distribution)
				+ (timeout <= 0 ? "" : ", timeout=" + timeout)
				+ ", providedQos=" + registredQos + ", simulatedQos="
				+ simulatedQos + "]";
	}

}
