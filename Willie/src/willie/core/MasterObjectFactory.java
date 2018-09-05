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

import java.util.ArrayList;

public class MasterObjectFactory {
	
	private ArrayList<ObjectFactory> factories;
	
	public MasterObjectFactory(){
		factories = new ArrayList<ObjectFactory>();
	}
	
	public void addFactory(ObjectFactory factory){
		factories.add(factory);
	}

	public WillieObject create(String type, String name) {
		
		for(ObjectFactory factory : factories){
			WillieObject object = factory.create(type, name);
			if(object != null){
				return object;
			}
		}
		
		if (type.equals("Biquadratic Curve")) {
			return new BiquadraticCurve(name);
		} else if (type.equals("Electric Meter")) {
			return new ElectricMeter(name);
		} else if (type.equals("Global")) {
			return createGlobal(name);
		} else if (type.equals("Output Report")) {
			return new OutputReport(name);
		} else if (type.equals("Quadratic Curve")) {
			return new QuadraticCurve(name);
		} else {
			throw new WillieFileReadException("The type " + type + " is not valid for " + name + ".");
		}
	}

	private WillieObject createGlobal(String name) {
		if (name.equals("Time Manager")) {
			return new TimeManager();
		} else if (name.equals("TMY3 Weather")) {
			return new TMY3Weather();
		} else {
			throw new WillieFileReadException(name + " is not a valid Global type.");
		}
	}

}
