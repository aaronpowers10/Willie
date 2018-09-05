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
package willie.controls;

import willie.core.ObjectFactory;
import willie.core.WillieObject;

public class ControlsFactory implements ObjectFactory{

	@Override
	public WillieObject create(String type, String name) {
		if (type.equals("Constant Controller")) {
			return new ConstantController(name);
		}else if (type.equals("Constant Setpoint")) {
			return new ConstantSetpoint(name);
		} else if (type.equals("Drybulb Reset Setpoint")) {
			return new DrybulbResetSetpoint(name);
		}else if (type.equals("PID Controller")) {
			return new PIDController(name);
		} else if (type.equals("Proportional Controller")) {
			return new ProportionalController(name);
		} else if (type.equals("Scaled P Controller")) {
			return new ScaledPController(name);
		}else if (type.equals("Scaled PID Controller")) {
			return new ScaledPIDController(name);
		}else if (type.equals("Scheduled Setpoint")) {
			return new ScheduledSetpoint(name);
		} else if (type.equals("Signal Multiplier Controller")) {
			return new SignalMultiplierController(name);
		}else if (type.equals("Soft Start Controller")) {
			return new SoftStartController(name);
		}else {
			return null;
		}
	}
}