package willie.controls;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.core.Weather;
import willie.core.WillieObject;

public class WetbulbBasedController implements WillieObject, Controller {

	private String name;
	private double wetbulb;
	private Weather weather;
	private String action;

	public WetbulbBasedController(String name) {
		this.name = name;
	}

	@Override
	public double output() {
		if (weather.wetbulb() < wetbulb) {
			if(action.equals("Direct")) {
				return 1;
			} else {
				return 0;
			}
		} else {
			if(action.equals("Direct")) {
				return 0;
			} else {
				return 1;
			}
		}
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		wetbulb = objectData.getReal("Wetbulb");
		action = objectData.getAlpha("Action");

	}

}
