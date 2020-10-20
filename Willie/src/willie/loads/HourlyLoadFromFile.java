package willie.loads;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.GregorianCalendar;
import java.util.Scanner;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.core.Interpolator;
import willie.core.ReportWriter;
import willie.core.RequiresPostSimulationProcessing;
import willie.core.RequiresPostStepProcessing;
import willie.core.RequiresPreSimulationProcessing;
import willie.core.RequiresPreStepProcessing;
import willie.core.RequiresTimeManager;
import willie.core.TimeManager;
import willie.core.Timer;
import willie.core.WillieObject;
import willie.output.Report;

public class HourlyLoadFromFile
		implements WillieObject, Load, ReportWriter, RequiresTimeManager, RequiresPreStepProcessing,
		RequiresPostStepProcessing, RequiresPreSimulationProcessing, RequiresPostSimulationProcessing {

	private String name;
	private String fileName;
	private double sensibleLoad;
	private double latentLoad;
	private double nextSensibleLoad;
	private double nextLatentLoad;
	GregorianCalendar calendar;
	private TimeManager timeManager;
	private Timer timer;
	private Scanner in;

	public HourlyLoadFromFile(String name) {
		this.name = name;
		timer = new Timer();
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		fileName = objectData.getAlpha("File");
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void processPostStep() {
		timer.step(timeManager.timeStep());
	}

	private void getData() {
		sensibleLoad = nextSensibleLoad;
		latentLoad = nextLatentLoad;
		String[] data = in.nextLine().split(",");
		int year = Integer.parseInt(data[0]);
		int month = Integer.parseInt(data[1]) - 1;
		int day = Integer.parseInt(data[2]);
		int hour = Integer.parseInt(data[3]);
		calendar = new GregorianCalendar(year, month, day, hour, 0);
		nextSensibleLoad = Double.parseDouble(data[4]);
		nextLatentLoad = Double.parseDouble(data[5]);
	}

	@Override
	public void addHeader(Report report) {
		report.addTitle(name, 2);
		report.addDataHeader("Sensible Load", "[Btu/Hr]");
		report.addDataHeader("Latent Load", "[Btu/Hr]");
		
	}

	@Override
	public void addData(Report report) {
		report.putReal(sensibleLoad());
		report.putReal(latentLoad());
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
		while (continueSearch) {
			getData();
			if (timeManager.isCalendarAfter(calendar)) {
				continueSearch = false;
			}
		}
		sensibleLoad = nextSensibleLoad;
		latentLoad = nextLatentLoad;

	}

	@Override
	public void processPreStep() {
		if (timeManager.isNewHour()) {
			getData();
			timer.reset();
		}

	}

	@Override
	public double sensibleLoad() {
		return Interpolator.interpolate(timer.getTime(), 0, 3600, sensibleLoad, nextSensibleLoad, -999, 999);
	}

	@Override
	public double latentLoad() {
		return Interpolator.interpolate(timer.getTime(), 0, 3600, latentLoad, nextLatentLoad, -999, 999);
	}
}
