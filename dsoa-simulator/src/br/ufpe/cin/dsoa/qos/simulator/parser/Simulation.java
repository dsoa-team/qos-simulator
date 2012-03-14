package br.ufpe.cin.dsoa.qos.simulator.parser;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SimulationType")
@XmlAccessorType(XmlAccessType.FIELD)
public class Simulation {

	@XmlElement(name = "interval")
	private List<Interval> intervals = new LinkedList<Interval>();

	public void addInterval(Interval interval) {
		this.intervals.add(interval);
	}

	public List<Interval> getIntervals() {
		return intervals;
	}

	@Override
	public String toString() {
		return "Simulation [intervals=" + intervals + "]";
	}

}
