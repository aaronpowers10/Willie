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
import willie.core.RequiresPostStepProcessing;
import willie.core.RequiresTimeManager;
import willie.core.TimeManager;
import willie.core.WillieObject;
import willie.output.Report;

public class PIDController
		implements WillieObject, Controller, RequiresTimeManager, ReportWriter, RequiresPostStepProcessing{

	private String name;
	private String action;
	private Sensor sensor;
	private Setpoint setpoint;
	private double minOutput;
	private double maxOutput;
	private TimeManager timeManager;
	private double pConstant;
	private double iConstant;
	private double dConstant;
	private double error;
	private double previousError;
	private double zeroErrorOutput;

	public PIDController(String name) {
		this.name = name;
		previousError = 0;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		minOutput = objectData.getReal("Min Output");
		maxOutput = objectData.getReal("Max Output");
		action = objectData.getAlpha("Action");
		sensor = (Sensor) objectReferences.get(objectData.getAlpha("Sensor"));
		setpoint = (Setpoint) objectReferences.get(objectData.getAlpha("Setpoint"));
		pConstant = objectData.getReal("Kp");
		iConstant = objectData.getReal("Ki");
		dConstant = objectData.getReal("Kd");
		zeroErrorOutput = objectData.getReal("Zero Error Output");
	}

	@Override
	public String name() {
		return name;
	}

	private double error() {
		if (action.equals("Direct")) {
			error = setpoint.getSetpoint() - sensor.sensorOutput();
		} else {
			error = sensor.sensorOutput() - setpoint.getSetpoint();
		}
		return error;
	}

	private double integralError() {
		return 0.5 * (error() + previousError);// * timeManager.dt();
	}

	private double derivativeError() {
		return (error() - previousError);// / timeManager.dt();
	}	

	private double pOutput() {
		return pConstant * error();
	}

	private double iOutput() {
		return iConstant * integralError();
	}

	private double dOutput() {
		return dConstant * derivativeError();
	}

	private double outputSignal() {
//		if (action.equals("Direct")) {
			return pOutput() + iOutput() + dOutput() + zeroErrorOutput;
//		} else {
//			return -pOutput() - iOutput() - dOutput();
//		}
	}

	@Override
	public double output() {
		return min(max(outputSignal(), minOutput), maxOutput);
	}

	@Override
	public void linkToTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;
	}

	@Override
	public void addHeader(Report report) {
		report.addTitle(name, 9);
		report.addDataHeader("Error", "");
		report.addDataHeader("Previous Error", "");
		report.addDataHeader("DT", "");
		report.addDataHeader("I Error", "");
		report.addDataHeader("D Error", "");
		report.addDataHeader("P Output", "");
		report.addDataHeader("I Output", "");
		report.addDataHeader("D Output", "");
		report.addDataHeader("Output", "");
	}

	@Override
	public void addData(Report report) {
		report.putReal(error());
		report.putReal(previousError);
		report.putReal(timeManager.dt());
		report.putReal(integralError());
		report.putReal(derivativeError());
		report.putReal(pOutput());
		report.putReal(iOutput());
		report.putReal(dOutput());
		report.putReal(output());
	}
	
	@Override
	public void processPostStep() {
		
		previousError = error;//	error();
	}
	

}
