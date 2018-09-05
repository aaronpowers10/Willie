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
package willie.schedules;

import java.util.ArrayList;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.core.RequiresTimeManager;
import willie.core.TimeManager;
import willie.core.WillieObject;

public class DaySchedule implements WillieObject, RequiresTimeManager{
	
	private String name;
	private ArrayList<Double> values;
	private TimeManager timeManager;
	
	public DaySchedule(String name){
		this.name = name;
		values = new ArrayList<Double>();
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		values = new ArrayList<Double>();
		for(int i=0;i<objectData.size("Values");i++){
			values.add(objectData.getReal("Values",i));
		}
	}
	
	public double getValue(){
		int index = timeManager.hour();
		if(index == 0){
			index = 23;
		} else {
			index = index -1;
		}
		return values.get(index);
	}

	@Override
	public void linkToTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;
		
	}	

}
