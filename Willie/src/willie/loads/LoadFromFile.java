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
package willie.loads;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.core.ReportWriter;
import willie.core.RequiresPostSimulationProcessing;
import willie.core.RequiresPostStepProcessing;
import willie.core.RequiresTimeManager;
import willie.core.TimeManager;
import willie.core.Timer;
import willie.core.WillieObject;
import willie.output.Report;

public class LoadFromFile implements WillieObject, Load, ReportWriter, RequiresTimeManager,RequiresPostStepProcessing,RequiresPostSimulationProcessing{
	
	/*
	 * TODO: Need to be able to read sensible load, latent load (time stamp?)
	 */
	private String name;
	private Scanner in;
	private double pullFrequency;
	private double sensibleLoad;
	private double latentLoad;
	private Timer timer;
	private TimeManager timeManager;
	
	public LoadFromFile(String name){
		this.name = name;
		timer = new Timer();
		
	}
	
	@Override
	public String name() {
		return name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		try {
			in = new Scanner(new File(objectData.getAlpha("File")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		pullFrequency = objectData.getReal("Pull Frequency");
		readLoad();
	}
	
	@Override
	public void processPostStep() {
		timer.step(timeManager.timeStep());
		if(timer.getTime()>=pullFrequency){
			readLoad();
			timer.reset();
		}
	}
	
	private void readLoad(){
		sensibleLoad = Double.parseDouble(in.nextLine());
	}

	@Override
	public void processPostSimulation() {
		in.close();
	}

	@Override
	public void linkToTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;
		
	}

	@Override
	public void addHeader(Report report) {
		report.addTitle(name, 2);
		report.addDataHeader("Sensible Load", "[Btu/Hr]");
		report.addDataHeader("Latent Load", "[Btu/Hr]");
		
	}

	@Override
	public void addData(Report report) {
		report.putReal(sensibleLoad);
		report.putReal(latentLoad);
		
	}

	@Override
	public double sensibleLoad() {
		return sensibleLoad;
	}

	@Override
	public double latentLoad() {
		return latentLoad;
	}
}
