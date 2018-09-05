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

public class CubicCurve implements OneVariableFunction,WillieObject{
	
	private String name;
	private double c1;
	private double c2;
	private double c3;
	private double c4;

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
		
	}

	@Override
	public double evaluate(double x) {
		return c1 + c2*x + c3*x*x + c4*x*x*x;
	}

}
