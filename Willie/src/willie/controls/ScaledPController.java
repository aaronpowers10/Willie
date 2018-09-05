/*
 *
 *  Copyright (C) 2017 Aaron Powers
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package willie.controls;

import static java.lang.Math.max;
import static java.lang.Math.min;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.core.ReportWriter;
import willie.core.WillieObject;
import willie.output.Report;

public class ScaledPController implements WillieObject, Controller,  ReportWriter{

	private String name;
	private String action;
	private Sensor sensor;
	private Setpoint setpoint;
	private double minOutput;
	private double maxOutput;
	private double fractionOfSetpoint;

	public ScaledPController(String name) {
		this.name = name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		minOutput = objectData.getReal("Min Output");
		maxOutput = objectData.getReal("Max Output");
		action = objectData.getAlpha("Action");
		sensor = (Sensor) objectReferences.get(objectData.getAlpha("Sensor"));
		setpoint = (Setpoint) objectReferences.get(objectData.getAlpha("Setpoint"));
		fractionOfSetpoint = objectData.getReal("Fraction Of Setpoint");
	}

	@Override
	public String name() {
		return name;
	}

	private double error() {
		if (action.equals("Direct")) {
			return setpoint.getSetpoint() - sensor.sensorOutput();
		} else {
			return sensor.sensorOutput() - setpoint.getSetpoint();
		}
	}
	
	private double pConstant(){
		return fractionOfSetpoint * Math.abs(setpoint.getSetpoint());
	}

	private double outputSignal() {
		if (action.equals("Direct")) {
			return pConstant()*error();
		} else {
			return -pConstant()*error();
		}
	}

	@Override
	public double output() {
		return min(max(outputSignal(), minOutput), maxOutput);
	}

	@Override
	public void addHeader(Report report) {
		report.addTitle(name, 3);
		report.addDataHeader("Error", "");
		report.addDataHeader("Output Signal", "");
		report.addDataHeader("Output", "");
	}

	@Override
	public void addData(Report report) {
		report.putReal(error());
		report.putReal(outputSignal());
		report.putReal(output());
	}
}