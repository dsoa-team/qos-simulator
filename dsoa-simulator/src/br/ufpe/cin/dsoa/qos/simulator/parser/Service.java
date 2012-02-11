package br.ufpe.cin.dsoa.qos.simulator.parser;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ServiceType", propOrder = { "interfaceName", "className",
		"pid", "address", "qosAttributes" })
@XmlAccessorType(XmlAccessType.FIELD)
public class Service {

	@XmlAttribute(required = true)
	private String interfaceName;

	@XmlAttribute(required = false)
	private String className;

	@XmlAttribute(required = true)
	private String pid;

	@XmlAttribute(required = false)
	private String address;

	@XmlElementWrapper(name = "qos")
	@XmlElement(name = "attribute")
	private List<QosAttribute> qosAttributes;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public List<QosAttribute> getQosAttributes() {
		return qosAttributes;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setQosAttributes(List<QosAttribute> qosAttributes) {
		this.qosAttributes = qosAttributes;
	}

	@Override
	public String toString() {
		return "Service [pid=" + pid
				+ (null == address ? "" : ", address=" + address)
				+ ", interfaceName=" + interfaceName
				+ (null == className ? "" : ", className=" + className)
				+ ", qosAttributes=" + qosAttributes + "]";
	}

}
