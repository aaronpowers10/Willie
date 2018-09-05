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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.output.Report;

public class TMY3Weather
		implements WillieObject, Weather, ReportWriter, RequiresTimeManager, RequiresPreStepProcessing,
		RequiresPostStepProcessing, RequiresPreSimulationProcessing, RequiresPostSimulationProcessing {

	/**
	 * TODO: ability to start simulation in middle of year
	 */

	private String fileName;
	private double nextPressure;
	private double nextDrybulb;
	private double nextDewpoint;
	private double pressure;
	private double drybulb;
	private double dewpoint;
	private int pressureIndex;
	private int drybulbIndex;
	private int dewpointIndex;
	private TimeManager timeManager;
	private Timer timer;
	private Scanner in;

	public TMY3Weather() {
		timer = new Timer();
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		fileName = objectData.getAlpha("File");
	}

	@Override
	public String name() {
		return "TMY3 Weather";
	}

	@Override
	public double drybulb() {
		return Interpolator.interpolate(timer.getTime(), 0, 3600, drybulb, nextDrybulb, -999, 999);
	}

	@Override
	public double pressure() {
		return Interpolator.interpolate(timer.getTime(), 0, 3600, pressure, nextPressure, -999, 999);
	}

	@Override
	public double dewpoint() {
		return Interpolator.interpolate(timer.getTime(), 0, 3600, dewpoint, nextDewpoint, -999, 999);
	}

	@Override
	public double wetbulb() {
		return Psychrometrics.wetbulbFDewpoint(drybulb(), dewpoint(), pressure());
	}

	@Override
	public double enthalpy() {
		return Psychrometrics.enthalpyFDewpoint(drybulb(), dewpoint(), pressure());
	}

	@Override
	public double humidityRatio() {
		return Psychrometrics.humidityRatioFDewpoint(dewpoint(), pressure());
	}

	@Override
	public void processPostStep() {
		timer.step(timeManager.timeStep());
	}


	private void setIndices() {
		String[] headers = in.nextLine().split(",");
		for (int i = 0; i < headers.length; i++) {
			if (headers[i].equals("Dry-bulb (C)")) {
				drybulbIndex = i;

			} else if (headers[i].equals("Dew-point (C)")) {
				dewpointIndex = i;
			}
			if (headers[i].equals("Pressure (mbar)")) {
				pressureIndex = i;
			}
		}
	}

	private void getData() {
		drybulb = nextDrybulb;
		pressure = nextPressure;
		dewpoint = nextDewpoint;
		String[] data = in.nextLine().split(",");
		nextDrybulb = Conversions.celsiusToFahrenheit(Double.parseDouble(data[drybulbIndex]));
		nextDewpoint = Conversions.celsiusToFahrenheit(Double.parseDouble(data[dewpointIndex]));
		nextPressure = Conversions.millibarToPsi((Double.parseDouble(data[pressureIndex])));
	}

	@Override
	public void addHeader(Report report) {
		report.addTitle("Weather", 6);
		report.addDataHeader("Dryulb", "[Deg-F]");
		report.addDataHeader("Pressure", "[psi]");
		report.addDataHeader("Dewpoint", "[Deg-F]");
		report.addDataHeader("Wetbulb", "[Deg-F]");
		report.addDataHeader("Enthalpy", "[Btu/Lb]");
		report.addDataHeader("Humidity Ratio", "[Lb-H2O/Lb-DA]");
	}

	@Override
	public void addData(Report report) {
		report.putReal(drybulb());
		report.putReal(pressure());
		report.putReal(dewpoint());
		report.putReal(wetbulb());
		report.putReal(enthalpy());
		report.putReal(humidityRatio());
	}

	@Override
	public void linkToTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;
	}

	@Override
	public void processPostSimulation() {
		in.close();
	}

	@Override
	public void processPreSimulation() {
		timer.setTime(timeManager.second());

		try {
			in = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		in.nextLine();
		setIndices();
		getData();
		drybulb = nextDrybulb;
		dewpoint = nextDewpoint;
		pressure = nextPressure;
	}

	@Override
	public void processPreStep() {
		if (timeManager.isNewHour()) {
			getData();
			timer.reset();
		}

	}
}
