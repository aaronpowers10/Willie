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

import willie.core.ObjectFactory;
import willie.core.WillieObject;

public class LoadsFactory implements ObjectFactory{

	@Override
	public WillieObject create(String type, String name) {
		if (type.equals("Composite Load")) {
			return new CompositeLoad(name);
		} else if (type.equals("Drybulb Based Load")) {
			return new DrybulbBasedLoad(name);
		} else if (type.equals("Load From File")) {
			return new LoadFromFile(name);
		} else if (type.equals("Scheduled Load")) {
			return new ScheduledLoad(name);
		} else {
			return null;
		}
	}
}