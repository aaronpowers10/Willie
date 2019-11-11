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
import willie.core.ReportWriter;
import willie.core.RequiresTimeManager;
import willie.core.Simulator;
import willie.core.TimeManager;
import willie.core.WillieObject;
import willie.output.Report;

public class SoftStartController implements WillieObject,Controller,Simulator,RequiresTimeManager, ReportWriter{

	private String name;
	private Controller controller;
	private double previousOutput;
	private double maxChangePerSecond;
	private TimeManager timeManager;
	
	public SoftStartController(String name){
		this.name = name;
		previousOutput = 0;
	}
	
	@Override
	public double output() {
		double changePerSecond = (controller.output() - previousOutput)/timeManager.dtHours()/3600.0;
		if(changePerSecond > maxChangePerSecond){
			return previousOutput + maxChangePerSecond*timeManager.dtHours()*3600;
		} else if (changePerSecond < -maxChangePerSecond){
			return previousOutput - maxChangePerSecond*timeManager.dtHours()*3600;
		} else {
			return controller.output();
		}
	}

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		maxChangePerSecond = objectData.getReal("Max Change Per Second");
		controller = (Controller)objectReferences.get(objectData.getAlpha("Controller"));		
	}

	@Override
	public void simulateStep2() {
		previousOutput = output();		
	}

	@Override
	public void linkToTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;
	}
	
	@Override
	public void addHeader(Report report) {
		report.addTitle(name,1);
		report.addDataHeader("Output", "");
	}

	@Override
	public void addData(Report report) {
		report.putReal(output());	
	}

}
