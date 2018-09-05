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

import java.util.ArrayList;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.core.ReportWriter;
import willie.core.WillieObject;
import willie.output.Report;

public class SignalMultiplierController implements WillieObject, Controller,ReportWriter{
	
	private String name;
	private ArrayList<Controller> controllers;
	
	public SignalMultiplierController(String name){
		this.name = name;
		controllers = new ArrayList<Controller>();
	}

	@Override
	public double output() {
		double output = 1.0;
		for(Controller controller: controllers){
			output = output*controller.output();
		}
		return output;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		controllers = new ArrayList<Controller>();
		for (int i = 0; i < objectData.size("Controllers"); i++) {
			controllers.add((Controller)objectReferences.get(objectData.getAlpha("Controllers", i)));
		}
		
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
