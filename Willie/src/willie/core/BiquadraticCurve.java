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

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;

public class BiquadraticCurve implements TwoVariableFunction, WillieObject {
	
	private String name;
	private double c1;
	private double c2;
	private double c3;
	private double c4;
	private double c5;
	private double c6;
	
	public BiquadraticCurve(String name){
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		c1 = objectData.getReal("C1");
		c2 = objectData.getReal("C2");
		c3 = objectData.getReal("C3");
		c4 = objectData.getReal("C4");
		c5 = objectData.getReal("C5");
		c6 = objectData.getReal("C6");
	}

	@Override
	public double evaluate(double x1, double x2) {
		return c1 + c2*x1 + c3*x1*x1 + c4*x2 + c5*x2*x2 + c6*x1*x2;
	}

}
