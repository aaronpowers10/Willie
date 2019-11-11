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
package willie.core;

import java.util.ArrayList;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.controls.Measureable;
import willie.output.Report;

public class ElectricMeter implements WillieObject, ReportWriter, RequiresTimeManager, Simulator, Measureable{
	
	private String name;
	private TimeManager timeManager;
	private ArrayList<ElectricConsumer> loads;
	private double consumption;
	
	public ElectricMeter(String name){
		this.name = name;
		loads = new ArrayList<ElectricConsumer>();
		reset();
	}

	@Override
	public void linkToTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;
	}

	@Override
	public String name() {
		return name;
	}
	
	public void reset(){
		consumption = 0;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		loads = new ArrayList<ElectricConsumer>();
		for(int i=0;i<objectData.size("Attached Loads");i++){
			loads.add((ElectricConsumer)(objectReferences.get(objectData.getAlpha("Attached Loads",i))));
		}
		
	}

	@Override
	public void addHeader(Report report) {
		report.addTitle(name,2);
		report.addDataHeader("Power", "[kW]");
		report.addDataHeader("Consumption", "[kWh]");
	}

	@Override
	public void addData(Report report) {
		report.putReal(power());
		report.putReal(consumption);
	}
	
	public double power(){
		double power = 0;
		for(ElectricConsumer load: loads){
			power += load.electricPower();
		}
		return power;
	}
	
	public double consumption(){
		return consumption;
	}

	@Override
	public void simulateStep2() {
		consumption += power()*timeManager.dtHours();
	}

	@Override
	public double getMeasurement() {
		return power();
	}

}
