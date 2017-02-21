package br.ufpe.cin.dsoa.qos.simulator.parser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "QosAttributeType")
@XmlAccessorType(XmlAccessType.FIELD)
public class QosAttribute {

	@XmlAttribute
	private String name;

	@XmlAttribute
	private double value;

	@XmlAttribute(required = false)
	private String operation;

	@XmlAttribute(required = false)
	private String expression;
	
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	@XmlElement(name = "simulation")
	private Simulation simulation;

	public Simulation getSimulation() {
		return simulation;
	}

	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	@Override
	public String toString() {
		return "QosAttribute [name=" + name + ", value=" + value
				+ ", operation=" + operation + ", simulation=" + simulation
				+ "]";
	}

}
