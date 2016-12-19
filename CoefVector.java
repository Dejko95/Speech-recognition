
public class CoefVector {
	double[] coefs;

	public CoefVector(double[] coefs) {
		super();
		this.coefs = coefs;
	}
	
	public double distanceFrom(CoefVector vec) {
		double sum = 0;
		
		for (int i=0; i<coefs.length; i++) {
			sum += Math.pow(coefs[i] - vec.coefs[i], 2);
		}
		//System.out.printf("%.6f\n", sum);
		return sum;
	}
}
