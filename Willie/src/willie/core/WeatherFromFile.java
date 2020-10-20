package willie.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.GregorianCalendar;
import java.util.Scanner;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.output.Report;

public class WeatherFromFile
		implements WillieObject, Weather, ReportWriter, RequiresTimeManager, RequiresPreStepProcessing,
		RequiresPostStepProcessing, RequiresPreSimulationProcessing, RequiresPostSimulationProcessing {

	private String fileName;
	private double nextPressure;
	private double nextDrybulb;
	private double nextWetbulb;
	private double pressure;
	private double drybulb;
	private double wetbulb;
	GregorianCalendar calendar;
	private TimeManager timeManager;
	private Timer timer;
	private Scanner in;

	public WeatherFromFile() {
		timer = new Timer();
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		fileName = objectData.getAlpha("File");
	}

	@Override
	public String name() {
		return "Weather From File";
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
		return Psychrometrics.dewpointFWetbulb(drybulb(), wetbulb(), pressure());
	}

	@Override
	public double wetbulb() {
		return Interpolator.interpolate(timer.getTime(), 0, 3600, wetbulb, nextWetbulb, -999, 999);
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

	private void getData() {
		drybulb = nextDrybulb;
		pressure = nextPressure;
		wetbulb = nextWetbulb;
		String[] data = in.nextLine().split(",");
		int year = Integer.parseInt(data[0]);
		int month = Integer.parseInt(data[1])-1;
		int day = Integer.parseInt(data[2]);
		int hour = Integer.parseInt(data[3]);
		calendar = new GregorianCalendar(year,month,day,hour,0);
		nextDrybulb = Conversions.celsiusToFahrenheit(Double.parseDouble(data[4]));
		nextWetbulb = Conversions.celsiusToFahrenheit(Double.parseDouble(data[5]));
		nextPressure = Conversions.millibarToPsi((Double.parseDouble(data[6])));
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
		in.nextLine(); // header
		boolean continueSearch = true;
		while(continueSearch) {
			getData();
			if(timeManager.isCalendarAfter(calendar)) {
				continueSearch = false;
			}
		}
		drybulb = nextDrybulb;
		wetbulb = nextWetbulb;
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
