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
import willie.core.Interpolator;
import willie.core.ReportWriter;
import willie.core.RequiresPostStepProcessing;
import willie.core.RequiresPreSimulationProcessing;
import willie.core.RequiresPreStepProcessing;
import willie.core.RequiresTimeManager;
import willie.core.TimeManager;
import willie.core.Timer;
import willie.core.WillieObject;
import willie.output.Report;

public class Schedule implements WillieObject, RequiresTimeManager, ReportWriter, RequiresPreStepProcessing,
		RequiresPostStepProcessing, RequiresPreSimulationProcessing {

	private String name;
	private TimeManager timeManager;
	private ArrayList<WeekSchedule> weekSchedules;
	private ArrayList<Integer> endMonth;
	private ArrayList<Integer> endDay;
	private boolean interpolate;
	private double previousValue;
	private Timer timer;
	private double value;

	public Schedule(String name) {
		this.name = name;
		weekSchedules = new ArrayList<WeekSchedule>();
		endMonth = new ArrayList<Integer>();
		endDay = new ArrayList<Integer>();
		timer = new Timer();
	}

	@Override
	public void linkToTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;

	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		weekSchedules = new ArrayList<WeekSchedule>();
		endMonth = new ArrayList<Integer>();
		endDay = new ArrayList<Integer>();

		for (int i = 0; i < objectData.size("Week Schedules"); i++) {
			weekSchedules.add((WeekSchedule) objectReferences.get(objectData.getAlpha("Week Schedules", i)));
		}

		endMonth = new ArrayList<Integer>();
		for (int i = 0; i < objectData.size("End Month"); i++) {
			endMonth.add(objectData.getInteger("End Month", i));
		}

		endDay = new ArrayList<Integer>();
		for (int i = 0; i < objectData.size("End Day"); i++) {
			endDay.add(objectData.getInteger("End Day", i));
		}

		String interpolateOption = objectData.getAlpha("Interpolate");
		if (interpolateOption.equals("Yes")) {
			interpolate = true;
		} else {
			interpolate = false;
		}

	}

	private double valueUninterpolated() {
		value = -999.0;
		for (int i = 0; i < weekSchedules.size(); i++) {
			if (timeManager.month() < endMonth.get(i)) {
				value = weekSchedules.get(i).getValue();
			} else if ((timeManager.month() == endMonth.get(i)) && (timeManager.day() <= endDay.get(i))) {
				value = weekSchedules.get(i).getValue();
			}
		}
		return value;
	}

	public double getValue() {
		value = -999.0;
		for (int i = 0; i < weekSchedules.size(); i++) {
			if (timeManager.month() < endMonth.get(i)) {
				value = weekSchedules.get(i).getValue();
			} else if ((timeManager.month() == endMonth.get(i)) && (timeManager.day() <= endDay.get(i))) {
				value = weekSchedules.get(i).getValue();
			}
		}
		if (interpolate) {
			return Interpolator.interpolate(timer.getTime(), 0, 3600, previousValue, value, -999, 999);
		} else {
			return value;
		}
	}

	@Override
	public void addHeader(Report report) {
		report.addTitle(name, 2);
		report.addDataHeader("Value", "");
		report.addDataHeader("Timer", "");
	}

	@Override
	public void addData(Report report) {
		report.putReal(getValue());
		report.putReal(timer.getTime());
	}

	@Override
	public void processPostStep() {
		timer.step(timeManager.timeStep());
	}

	@Override
	public void processPreSimulation() {
		timer.setTime(timeManager.second());
		previousValue = valueUninterpolated();
	}

	@Override
	public void processPreStep() {
		if (timeManager.isNewHour()) {
			previousValue = value;
			timer.reset();
		}
	}

}
