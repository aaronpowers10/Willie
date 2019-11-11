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
import booker.building_data.BookerProject;
import booker.building_data.NamespaceList;
import booker.io.ProjectReadCompleteListener;
import otis.lexical.ConsoleUpdateListener;
import sam.input.ProjectReader;
import willie.controls.ControlsFactory;
import willie.loads.LoadsFactory;
import willie.schedules.ScheduleFactory;

public class Project implements ProjectReadCompleteListener {

	private NamespaceList<WillieObject> objects;
	private ArrayList<OutputReport> outputFiles;
	private ArrayList<RequiresPreSimulationProcessing> requiresPreSimulationProcessing;
	private ArrayList<RequiresPreStepProcessing> requiresPreStepProcessing;
	private ArrayList<RequiresPostStepProcessing> requiresPostStepProcessing;
	private ArrayList<RequiresPostSimulationProcessing> requiresPostSimulationProcessing;
	private ArrayList<RequiresWeather> requiresWeather;
	private ArrayList<Simulator> simulators;
	private TimeManager timeManager;
	private Weather weather;
	private MasterObjectFactory masterObjectFactory;
	private ArrayList<SimulationUpdateListener> simulationUpdateListeners;

	public Project(ArrayList<ObjectFactory> objectFactories) {
		initialize();
		for (ObjectFactory factory : objectFactories) {
			masterObjectFactory.addFactory(factory);
		}
	}

	public Project(ObjectFactory objectFactory) {
		initialize();
		masterObjectFactory.addFactory(objectFactory);
	}

	public Project() {
		initialize();
	}

	public void addSimulationUpdateListener(SimulationUpdateListener simulationUpdateListener) {
		simulationUpdateListeners.add(simulationUpdateListener);
	}

	private void initialize() {
		masterObjectFactory = new MasterObjectFactory();
		masterObjectFactory.addFactory(new ScheduleFactory());
		masterObjectFactory.addFactory(new LoadsFactory());
		masterObjectFactory.addFactory(new ControlsFactory());
		objects = new NamespaceList<WillieObject>();
		outputFiles = new ArrayList<OutputReport>();
		requiresPreSimulationProcessing = new ArrayList<RequiresPreSimulationProcessing>();
		requiresPreStepProcessing = new ArrayList<RequiresPreStepProcessing>();
		requiresPostStepProcessing = new ArrayList<RequiresPostStepProcessing>();
		requiresPostSimulationProcessing = new ArrayList<RequiresPostSimulationProcessing>();
		requiresWeather = new ArrayList<RequiresWeather>();
		simulators = new ArrayList<Simulator>();
		simulationUpdateListeners = new ArrayList<SimulationUpdateListener>();
	}

	public void addObjectFactory(ObjectFactory factory) {
		masterObjectFactory.addFactory(factory);
	}

	private void read(String fileName) {
		ProjectReader reader = new ProjectReader();
		reader.addProjectReadCompleteListener(this);
		reader.addUpdateListener(new ConsoleUpdateListener());
		reader.loadInThread(fileName);
	}
	
	public void simulate(String fileName) {
		read(fileName);
	}

	public void runSimulation() throws IOException {
		long startTime = System.nanoTime();

		for (SimulationUpdateListener updateListener : simulationUpdateListeners) {
			updateListener.simulationUpdate("Beginning Simulation");
		}

		for (OutputReport outputFile : outputFiles) {
			outputFile.writeHeader();
		}

		timeManager.initialize();

		for (RequiresPreSimulationProcessing preSimulationProcessor : requiresPreSimulationProcessing) {
			preSimulationProcessor.processPreSimulation();
		}

		while (timeManager.continueSimulation()) {

			if (timeManager.isNewHour()) {
				for (SimulationUpdateListener updateListener : simulationUpdateListeners) {
					updateListener.simulationUpdate(
							"Simulating " + timeManager.monthString() + " " + timeManager.timeString() + ".");
				}
			}

			for (RequiresPreStepProcessing preStepProcessor : requiresPreStepProcessing) {
				preStepProcessor.processPreStep();
			}

			for (Simulator simulator : simulators) {
				simulator.simulateStep1();
			}

			for (Simulator simulator : simulators) {
				simulator.simulateStep2();
			}

			for (OutputReport outputFile : outputFiles) {
				outputFile.step();
			}

			for (RequiresPostStepProcessing postStepProcessor : requiresPostStepProcessing) {
				postStepProcessor.processPostStep();
			}

			timeManager.incrementTimeStep();
		}

		for (OutputReport outputFile : outputFiles) {
			outputFile.close();
		}

		for (RequiresPostSimulationProcessing postSimulationProcessor : requiresPostSimulationProcessing) {
			postSimulationProcessor.processPostSimulation();
		}

		long endTime = System.nanoTime();

		double duration = (endTime - startTime) / 1000000000.0;
		for (SimulationUpdateListener updateListener : simulationUpdateListeners) {
			updateListener.simulationUpdate("Simulation completed in " + duration + " seconds.");
		}
	}

	@Override
	public void projectReadComplete(BookerProject project) {

		for (int i = 0; i < project.size(); i++) {
			BookerObject object = project.get(i);
			objects.add(masterObjectFactory.create(object.type(), object.name()));
			if (objects.get(i) instanceof TimeManager) {
				timeManager = (TimeManager) objects.get(i);
			} else if (objects.get(i) instanceof Weather) {
				weather = (Weather) objects.get(i);
			}
		}

		for (int i = 0; i < objects.size(); i++) {
			objects.get(i).read(project.get(i), objects);
		}

		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i) instanceof OutputReport) {
				outputFiles.add((OutputReport) objects.get(i));
			}
		}

		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i) instanceof RequiresPreStepProcessing) {
				requiresPreStepProcessing.add((RequiresPreStepProcessing) (objects.get(i)));
			}
		}

		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i) instanceof RequiresPostStepProcessing) {
				requiresPostStepProcessing.add((RequiresPostStepProcessing) (objects.get(i)));
			}
		}

		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i) instanceof RequiresPreSimulationProcessing) {
				requiresPreSimulationProcessing.add((RequiresPreSimulationProcessing) (objects.get(i)));
			}
		}

		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i) instanceof RequiresPostSimulationProcessing) {
				requiresPostSimulationProcessing.add((RequiresPostSimulationProcessing) (objects.get(i)));
			}
		}

		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i) instanceof RequiresTimeManager) {
				((RequiresTimeManager) (objects.get(i))).linkToTimeManager(timeManager);
			}
		}

		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i) instanceof RequiresWeather) {
				((RequiresWeather) (objects.get(i))).linkToWeather(weather);
			}
		}

		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i) instanceof Simulator) {
				simulators.add((Simulator) objects.get(i));
			}
		}
		
		try {
			runSimulation();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
