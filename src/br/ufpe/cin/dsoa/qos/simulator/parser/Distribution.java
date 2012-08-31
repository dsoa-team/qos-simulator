package br.ufpe.cin.dsoa.qos.simulator.parser;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "DistributionType")
@XmlAccessorType(XmlAccessType.FIELD)
public class Distribution {
	@XmlAttribute
	private String name;

	@XmlElement(name = "parameter")
	private List<Parameter> parameters = new LinkedList<Parameter>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
}
