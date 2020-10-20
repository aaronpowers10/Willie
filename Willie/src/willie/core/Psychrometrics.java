package willie.core;

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;


public class Psychrometrics {
	private static double C1 = -10214.165;
	private static double C2 = -4.8932428;
	private static double C3 = -0.0053765794;
	private static double C4 = 0.00000019202377;
	private static double C5 = 0.00000000035575832;
	private static double C6 = -0.000000000000090344688;
	private static double C7 = 4.1635019;
	private static double C8 = -10440.397;
	private static double C9 = -11.29465;
	private static double C10 = -.027022355;
	private static double C11 = 0.00001289036;
	private static double C12 = -0.0000000024780681;
	private static double C13 = 6.5459673;

	private static double pws(double temperature){
        double tempRankine = temperature +459.67;
        if (temperature<32){
            return exp(C1/tempRankine+C2+C3*tempRankine+C4*pow(tempRankine,2)+C5*pow(tempRankine,3)+C6*pow(tempRankine,4)+C7*log(tempRankine));
        } else{
            return exp(C8/tempRankine+C9+C10*tempRankine+C11*pow(tempRankine,2)+C12*pow(tempRankine,3)+C13*log(tempRankine));
        }
    }


	public static double saturationHumidityRatio(double temperature, double pressure){
        return 0.621945*(pws(temperature)/(pressure-pws(temperature)));
    }

	public static double humidityRatioFWetbulb(double drybulb, double wetbulb, double pressure){
		double hSat;
		hSat = saturationHumidityRatio(wetbulb,pressure);
		if(drybulb > 32) {
			return ((1093-0.556*wetbulb)*hSat - 0.24*(drybulb - wetbulb)) / (1093 + 0.444*drybulb -wetbulb);
		} else {
			return ((1220 - 0.04*wetbulb)*hSat - 0.24*(drybulb - wetbulb)) / (1220 + 0.444*drybulb - 0.48*wetbulb);
		}
	}

	public static double humidityRatioFDewpoint(double dewpoint, double pressure){
		return saturationHumidityRatio(dewpoint,pressure);
	}
	
	public static double humidityRatioFRealativeHumidity(double drybulb,double relativeHumidity, double pressure){
		double pw = relativeHumidity*pws(drybulb);
		return 0.621945 * (pw / (pressure - pw));
	}

	public static double wetbulbFDewpoint(double drybulb, double dewpoint, double pressure){
		double humidityRatio;
		double residual;
		double residualInc;
		double slope;
		double dx;
		double wetbulb;
		int iteration = 0;

		residual = 1000;
		dx = 0.0001;
		wetbulb = drybulb - 10;
		humidityRatio = humidityRatioFDewpoint(dewpoint,pressure);
		while(Math.abs(residual)>0.000005 && iteration < 500){
			residual = humidityRatioFWetbulb(drybulb,wetbulb,pressure) - humidityRatio;
			residualInc = humidityRatioFWetbulb(drybulb,wetbulb + dx, pressure) - humidityRatio;
			slope = (residualInc - residual) / dx;
			wetbulb = wetbulb - residual / slope;
			iteration ++;
		}
		return wetbulb;
	}

	public static double enthalpyFDewpoint(double drybulb, double dewpoint, double pressure){
		return 0.24*drybulb + humidityRatioFDewpoint(drybulb,dewpoint)*(1061 + 0.444*drybulb);
	}
	
	public static double enthalpyFWetbulb(double drybulb, double wetbulb, double pressure){
		return 0.24*drybulb + humidityRatioFWetbulb(drybulb,wetbulb,pressure)*(1061 + 0.444*drybulb);
	}
	
	public static double enthalpyFHumidityRatio(double drybulb, double humidityRatio, double pressure){
		return 0.24*drybulb + humidityRatio*(1061 + 0.444*drybulb);
	}
	
	public static double volumeFHumidityRatio(double drybulb, double humidityRatio, double pressure){
		return 0.370486 * (drybulb + 459.67) * (1 + 1.607858 * humidityRatio) / pressure;
	}
	
	public static double densityFHumidityRatio(double drybulb, double humidityRatio, double pressure){
		return 1.0 / volumeFHumidityRatio(drybulb,humidityRatio,pressure);
	}
	
	public static double drybulbFEnthalpyHumidityRatio(double enthalpy, double humidityRatio){
		return (enthalpy - 1061*humidityRatio)/(0.24 + 0.444*humidityRatio);
	}	
	
	public static double saturationHumidityRatioFEnthalpy(double enthalpy, double pressure){
		double residual;
		double residualInc;
		double slope;
		double dx;
		double drybulb;
		int iteration = 0;

		residual = 1000;
		dx = 0.0001;
		drybulb = 60;
		while(Math.abs(residual)>0.00005 && iteration < 500){
			residual = enthalpyFHumidityRatio(drybulb,saturationHumidityRatio(drybulb,pressure),pressure) - enthalpy;
			residualInc = enthalpyFHumidityRatio(drybulb + dx,saturationHumidityRatio(drybulb + dx,pressure),pressure) - enthalpy;
			slope = (residualInc - residual) / dx;
			drybulb = drybulb - residual / slope;
			iteration ++;
		}
		return saturationHumidityRatio(drybulb,pressure);
	}
	
	public static double dewpointFHumidityRatio(double drybulb, double humidityRatio, double pressure){
		double c14 = 100.45;
		double c15 = 33.193;
		double c16 = 2.319;
		double c17 = 0.17074;
		double c18 = 1.2063;
		double pw = (pressure * humidityRatio) / (0.621945 + humidityRatio);
		double alpha = Math.log(pw);
		
		if (drybulb > 32){
		    return c14 + c15 * alpha + c16 * alpha *alpha + c17 * alpha*alpha*alpha + c18 * Math.pow(pw , 0.1984);
		} else {
		    return 90.12 + 26.142 * alpha + 0.8927 * alpha*alpha;
		}
	}
	
	public static double dewpointFWetbulb(double drybulb, double wetbulb,double pressure) {
		return dewpointFHumidityRatio(drybulb,humidityRatioFWetbulb(drybulb,wetbulb,pressure),pressure);
	}
	public static void main(String[] args){
		System.out.println(saturationHumidityRatioFEnthalpy(50,14.7));
	}
}
