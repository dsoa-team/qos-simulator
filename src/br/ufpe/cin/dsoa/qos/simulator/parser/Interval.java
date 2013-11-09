package br.ufpe.cin.dsoa.qos.simulator.parser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "IntervalType")
@XmlAccessorType(XmlAccessType.FIELD)
public class Interval {
	@XmlAttribute
	private long time;

	@XmlAttribute(required = false)
	private String unit;

	@XmlAttribute(required = false)
	private double value;
	
	@XmlAttribute(required = false)
	private String exceptionClass;

	@XmlElement(name = "distribution")
	private Distribution distribution;

	private long startTime;
	private long stopTime;

	public boolean contains(long moment) {
		if (moment >= startTime && moment < stopTime) {
			return true;
		} else {
			return false;
		}
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getExceptionClass() {
		return exceptionClass;
	}

	public void setExceptionClass(String exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	public Distribution getDistribution() {
		return distribution;
	}

	public void setDistribution(Distribution distribution) {
		this.distribution = distribution;
	}

	public void setStartTime(long startTime) {
		// TODO Auto-generated method stub
		this.startTime = startTime;
	}

	public void setStopTime(long stopTime) {
		this.stopTime = stopTime;
	}

	@Override
	public String toString() {
		return "Interval [time=" + time + ", unit=" + unit + ", value=" + value
				+ ", distribution=" + distribution + ", startTime=" + startTime
				+ ", stopTime=" + stopTime + "]";
	}

}