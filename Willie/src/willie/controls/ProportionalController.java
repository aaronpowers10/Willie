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

public class ProportionalController implements WillieObject,Controller, ReportWriter {
	
	private String name;
	private String action;
	private Sensor sensor;
	private Setpoint setpoint;
	private double throttleRange;
	private double minOutput;
	private double maxOutput;
	
	public ProportionalController(String name){
		this.name = name;
	}
	
	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		throttleRange = objectData.getReal("Throttle Range");
		minOutput = objectData.getReal("Min Output");
		maxOutput = objectData.getReal("Max Output");
		action = objectData.getAlpha("Action");
		sensor = (Sensor)objectReferences.get(objectData.getAlpha("Sensor"));
		setpoint = (Setpoint)objectReferences.get(objectData.getAlpha("Setpoint"));
		
	}

	@Override
	public String name() {
		return name;
	}
	
	private double outputSignal(){
		if (action.equals("Direct")){			
			return 1/throttleRange * (sensor.sensorOutput() - (setpoint.getSetpoint() - 0.5*throttleRange));
		} else {
			return -1/throttleRange * (sensor.sensorOutput() - (setpoint.getSetpoint() - 0.5*throttleRange)) + 1;
		}
	}
	
	private double error(){
		return sensor.sensorOutput() - setpoint.getSetpoint();
	}

	@Override
	public double output() {
		//if(outputSignal() <= 0)
		//	return 0;
		return min(max(outputSignal(),minOutput),maxOutput);
	}
	
	@Override
	public void addHeader(Report report) {
		report.addTitle(name,2);
		report.addDataHeader("Error", "");
		report.addDataHeader("Output", "");
	}

	@Override
	public void addData(Report report) {
		report.putReal(error());
		report.putReal(output());	
	}
}
