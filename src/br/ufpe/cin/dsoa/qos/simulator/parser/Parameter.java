package br.ufpe.cin.dsoa.qos.simulator.parser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ParameterType")
@XmlAccessorType(XmlAccessType.FIELD)
public class Parameter {

	@XmlAttribute
	private String name;

	@XmlAttribute
	private double value;

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

}
