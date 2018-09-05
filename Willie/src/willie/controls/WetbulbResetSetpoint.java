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
import willie.core.RequiresWeather;
import willie.core.Weather;
import willie.core.WillieObject;

public class WetbulbResetSetpoint implements WillieObject, Sensor, RequiresWeather{
	
	private String name;
	private double maxWetbulb;
	private double maxWetbulbSetpoint;
	private double minWetbulb;
	private double minWetbulbSetpoint;
	private Weather weather;
	
	public WetbulbResetSetpoint(String name){
		this.name = name;
	}
	
	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		maxWetbulb = objectData.getReal("Max Wetbulb");
		minWetbulb = objectData.getReal("Min Wetbulb");
		maxWetbulbSetpoint = objectData.getReal("Max Wetbulb Setpoint");
		minWetbulbSetpoint = objectData.getReal("Min Wetbulb Setpoint");		
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public double sensorOutput() {
		return max(minWetbulbSetpoint,min(maxWetbulbSetpoint,(maxWetbulbSetpoint - minWetbulbSetpoint)/
				(maxWetbulb - minWetbulb)*(weather.wetbulb()-minWetbulb)+ minWetbulbSetpoint));
	}

	@Override
	public void linkToWeather(Weather weather) {
		this.weather = weather;		
	}

}
