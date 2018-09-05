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
import willie.schedules.Schedule;

public class ScheduledSetpoint implements WillieObject, Setpoint {

	private Schedule schedule;
	private String name;
	
	public ScheduledSetpoint(String name){
		this.name = name;
	}
	
	@Override
	public double getSetpoint() {
		return schedule.getValue();
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		schedule = (Schedule)objectReferences.get(objectData.getAlpha("Schedule"));
		
	}

}
