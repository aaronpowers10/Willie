/*
 *
1 *  Copyright (C) 2017 Aaron Powers
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
import willie.core.Interpolator;
import willie.core.RequiresWeather;
import willie.core.Weather;
import willie.core.WillieObject;

public class DrybulbResetSetpoint implements WillieObject, Setpoint, RequiresWeather{
	
	private String name;
	private double maxDrybulb;
	private double maxDrybulbSetpoint;
	private double minDrybulb;
	private double minDrybulbSetpoint;
	private Weather weather;
	
	public DrybulbResetSetpoint(String name){
		this.name = name;
	}
	
	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		maxDrybulb = objectData.getReal("Max Drybulb");
		minDrybulb = objectData.getReal("Min Drybulb");
		maxDrybulbSetpoint = objectData.getReal("Max Drybulb Setpoint");
		minDrybulbSetpoint = objectData.getReal("Min Drybulb Setpoint");
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void linkToWeather(Weather weather) {
		this.weather = weather;		
	}

	@Override
	public double getSetpoint() {
		return Interpolator.interpolate(weather.drybulb(), minDrybulb, maxDrybulb, 
				minDrybulbSetpoint, maxDrybulbSetpoint, Math.min(minDrybulbSetpoint,maxDrybulbSetpoint), Math.max(minDrybulbSetpoint,maxDrybulbSetpoint));
		//return max(minDrybulbSetpoint,min(maxDrybulbSetpoint,(maxDrybulbSetpoint - minDrybulbSetpoint)/
		//		(maxDrybulb - minDrybulb)*(weather.drybulb()-minDrybulb)+ minDrybulbSetpoint));
	}
}
