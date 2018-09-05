package willie.core;

public class Conversions {

	public static double kWTToEir(double kWT){
		return kWT / 3.516998828;
	}

	public static double eirTokWT(double eir){
		return eir*3.516998828;
	}
	
	public static double btuToTons(double btu){
		return btu/12000.0;
	}
	
	public static double tonsToBtu(double tons){
		return tons*12000;
	}

	public static double btuTokW(double btu){
		return btu / 3412.0;
	}

	public static double tonsTokW(double tons){
		return btuTokW(tons*12000);
	}

	public static double kWToTons(double kW){
		return kW*3412 / 12000;
	}
	
	public static double kWToBtu(double kW){
		return kW*3412;
	}
	
	public static double celsiusToFahrenheit(double celsius){
		return 32 + 9/5.0*celsius;
	}
	
	public static double millibarToPsi(double millibar){
		return 0.0145038*millibar;
	}
	
	public static double inchesWaterToPsi(double inchesWater){
		return 0.0360912*inchesWater;
	}

}
