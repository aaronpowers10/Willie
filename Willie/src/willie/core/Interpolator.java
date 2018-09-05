package willie.core;

public class Interpolator {

	public static double interpolate(double x, double x1, double x2, double f1, double f2, double minf, double maxf){
		return Math.max(minf, Math.min(maxf,f1 + (f2-f1)*(x-x1) / (x2 - x1)));
	}
	
	public static double interpolate(double x, double x1, double x2, double f1, double f2){
		return f1 + (f2-f1)*(x-x1) / (x2 - x1);
	}

}
