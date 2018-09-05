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

import java.io.IOException;
import java.util.ArrayList;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.output.Report;

public class OutputReport implements WillieObject, RequiresTimeManager{
	
	private String name;
	private ArrayList<ReportWriter> writers;
	private Report report;
	private double frequency; 
	private TimeManager timeManager;
	private Timer timer;
	
	public OutputReport(String name){
		this.name = name;
		writers = new ArrayList<ReportWriter>();
		timer = new Timer();
	}
	
	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		frequency = objectData.getReal("Report Frequency");
		writers = new ArrayList<ReportWriter>();
		for(int i=0;i<objectData.size("Objects");i++){
			writers.add((ReportWriter)(objectReferences.get(objectData.getAlpha("Objects",i))));
		}
		String fileName = objectData.getAlpha("File Name");
		report = new Report(fileName);
		timer.setTime(frequency);
	}

	@Override
	public String name() {
		return name;
	}
	
	public void writeHeader()throws IOException{
		for(ReportWriter writer: writers){
			writer.addHeader(report);
		}
		report.writeHeader();
	}
	
	public void writeData() throws IOException{
		for(ReportWriter writer: writers){
			writer.addData(report);
		}
		report.writeRow();
	}

	public void close() throws IOException{
		report.close();
	}

	public void step() throws IOException{
		timer.step(timeManager.timeStep());
		if(timer.getTime()>=frequency){
			writeData();
			timer.reset();
		}
	}

	@Override
	public void linkToTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;
	}

}
