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

import java.util.ArrayList;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.core.ReportWriter;
import willie.core.WillieObject;
import willie.output.Report;

public class CompositeLoad implements WillieObject, Load, ReportWriter{
	
	private String name;
	private ArrayList<Load> loads;
	
	public CompositeLoad(String name){
		this.name = name;
		loads = new ArrayList<Load>();
	}

	@Override
	public double sensibleLoad() {
		double totalLoad = 0;
		for(Load load: loads){
			totalLoad += load.sensibleLoad();
		}
		return totalLoad;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		loads = new ArrayList<Load>();
		for(int i=0;i<objectData.size("Loads");i++){
			loads.add((Load)objectReferences.get(objectData.getAlpha("Loads",i)));
		}
		
	}

	@Override
	public double latentLoad() {
		double totalLoad = 0;
		for(Load load: loads){
			totalLoad += load.latentLoad();
		}
		return Math.max(0,totalLoad);
	}
	
	@Override
	public void addHeader(Report report) {
		report.addTitle(name,2);
		report.addDataHeader("Sensible Load", "[Btu/Hr]");
		report.addDataHeader("Latent Load", "[Btu/Hr]");
	}

	@Override
	public void addData(Report report) {
		report.putReal(sensibleLoad());
		report.putReal(latentLoad());
	}

}
