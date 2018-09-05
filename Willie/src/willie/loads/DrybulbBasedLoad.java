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

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.core.Interpolator;
import willie.core.ReportWriter;
import willie.core.RequiresWeather;
import willie.core.Weather;
import willie.core.WillieObject;
import willie.output.Report;

public class DrybulbBasedLoad implements WillieObject, Load, RequiresWeather, ReportWriter{
	
	private String name;
	private Weather weather;
	private double peakLoad;
	private double peakLoadDrybulb;
	private double balanceTemperature;
	
	public DrybulbBasedLoad(String name){
		this.name = name;
	}

	@Override
	public void linkToWeather(Weather weather) {
		this.weather = weather;		
	}

	@Override
	public double sensibleLoad() {
		return Interpolator.interpolate(weather.drybulb(), balanceTemperature, peakLoadDrybulb, 0.0, peakLoad);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		peakLoad = objectData.getReal("Peak Load");
		peakLoadDrybulb = objectData.getReal("Peak Load Drybulb");
		balanceTemperature = objectData.getReal("Balance Temperature");		
	}

	@Override
	public double latentLoad() {
		return 0;
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
