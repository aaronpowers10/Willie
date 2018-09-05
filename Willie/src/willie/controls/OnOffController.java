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

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.core.WillieObject;

public class OnOffController implements WillieObject, Controller {
	
	private String name;
	private Setpoint setpoint;
	private Sensor sensor;
	private double deadband;
	private String action;
	private double output;

	public OnOffController(String name){
		this.name = name;
	}
	
	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		setpoint = (Setpoint)objectReferences.get(objectData.getAlpha("Setpoint"));
		sensor = (Sensor)objectReferences.get(objectData.getAlpha("Sensor"));
		deadband = objectData.getReal("Deadband");	
		action = objectData.getAlpha("Action");
	}
	
	@Override
	public String name() {
		return name;
	}
	
	private double error(){
		return sensor.sensorOutput() - setpoint.getSetpoint();
	}

	@Override
	public double output() {
		if(error() > setpoint.getSetpoint() + deadband){
			output = 1;
		} else if(error() < setpoint.getSetpoint() - deadband){
			output = 0;
		}
		if(action.equals("Direct")){
			return output;
		} else {
			return 1-output;
		}
	}
}
