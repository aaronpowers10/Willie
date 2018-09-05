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

import java.util.Calendar;
import java.util.GregorianCalendar;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.output.Report;

public class TimeManager implements WillieObject, ReportWriter {
	
	private GregorianCalendar calendar;
	private double timeStep;
	private int startYear;
	private int startMonth;
	private int startDay;
	private int startHour;
	private int startMinute;
	private int endYear;
	private int endMonth;
	private int endDay;
	private int endHour;
	private int endMinute;
	private int stepNumber;
	
	public TimeManager(){
		//Defaults here
		stepNumber = 1;
	}
	
	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		timeStep = objectData.getReal("Time Step");
		startYear = objectData.getInteger("Start Year");
		startMonth = objectData.getInteger("Start Month");
		startDay = objectData.getInteger("Start Day of Month");
		startHour = objectData.getInteger("Start Hour of Day");
		startMinute = objectData.getInteger("Start Minute of Hour");
		endYear = objectData.getInteger("End Year");
		endMonth = objectData.getInteger("End Month");
		endDay = objectData.getInteger("End Day of Month");
		endHour = objectData.getInteger("End Hour of Day");
		endMinute = objectData.getInteger("End Minute of Hour");
		
	}
	
	@Override
	public String name() {
		return "Time Manager";
	}

	public void initialize(){
		calendar = new GregorianCalendar(startYear,startMonth-1,startDay,startHour,startMinute);
	}
	
	public boolean isNewHour(){
		if(calendar.get(Calendar.MINUTE)==0 && calendar.get(Calendar.SECOND)==0 && calendar.get(Calendar.MILLISECOND)==0){
			return true;
		}else {
			return false;
		}
	}
	
	public double timeStep(){
		return timeStep;
	}

	public double dt(){
		/*
		 * Units of hours
		 */
		return (double)timeStep/60.0/60.0;
	}

	public int hourOfYear(){
		return (calendar.get(Calendar.DAY_OF_YEAR)-1)*24 + calendar.get(Calendar.HOUR_OF_DAY);
	}

	public void incrementTimeStep(){
		calendar.add(Calendar.MILLISECOND, (int)(timeStep*1000.0));
		stepNumber++;
	}
	
	public String monthString(){
		double month = month();
		if(month==1){
			return "January";
		} else if (month == 2){
			return "February";
		}else if (month == 3){
			return "March";
		}else if (month == 4){
			return "April";
		}else if (month == 5){
			return "May";
		}else if (month == 6){
			return "June";
		}else if (month == 7){
			return "July";
		}else if (month == 8){
			return "August";
		}else if (month == 9){
			return "September";
		}else if (month == 10){
			return "October";
		}else if (month == 11){
			return "November";
		} else {
			return "December";
		}
	}
	
	public String timeString(){
		return calendar.getTime().toString();
	}

	public int month(){
		return calendar.get(Calendar.MONTH)+1;
	}

	public int day(){
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public int dayOfWeek(){
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	public int hour(){
		return calendar.get(Calendar.HOUR_OF_DAY);
	}
	
	public int minute(){
		return calendar.get(Calendar.MINUTE);
	}
	
	public int second(){
		return calendar.get(Calendar.SECOND);
	}

	public boolean isWeekday(){
		if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
			return false;
		} else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
			return false;
		} else {
			return true;
		}
	}

	public boolean continueSimulation(){
		GregorianCalendar endCalendar = new GregorianCalendar(endYear,endMonth-1,endDay,endHour,endMinute);
		if(calendar.before(endCalendar)){
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void addHeader(Report report) {
		report.addTitle("Time",7);
		report.addDataHeader("Time Step", "");
		report.addDataHeader("Month", "");
		report.addDataHeader("Day", "");
		report.addDataHeader("Day of Week", "");
		report.addDataHeader("Hour", "");
		report.addDataHeader("Minute", "");
		report.addDataHeader("Second", "");
	}

	@Override
	public void addData(Report report) {
		report.putInteger(stepNumber);
		report.putInteger(month());
		report.putInteger(day());
		report.putInteger(dayOfWeek());
		report.putInteger(hour());
		report.putInteger(minute());
		report.putInteger(second());
	}
}
