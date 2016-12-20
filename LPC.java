
public class LPC {
	public final static int p = 12; // precision, in number of samples
	
	public static double[] calcCoefficientVector(double[] samples, int start, int N) { //start - pocetak frejma
		double[] alpha = new double[p];													//N - duzina frejma
		double[] R = new double[p+1];
	
		for (int i=0; i<=p; i++) {
			R[i] = autocorelationFunc(samples, start, N, i);
			//System.out.printf("%.6f ", R[i]);
		}
		//System.out.println();
		double[][] mat = new double[p+1][p+2]; //indexing from 1
		for (int i=1; i<=p; i++) {
			for (int k=1; k<=p; k++) {
				mat[i][k] = R[Math.abs(k-i)];
			}
			mat[i][p+1] = R[i];
		}
		
		gaus(mat, p);
		
		for (int i=0; i<p; i++) alpha[i] = mat[i+1][p+1];
//		double sum = 0;
//		for (int i=1; i<=N; i++) {
//			double ss = 0;
//			for (int k=1; k<=p; k++) ss += samples[offset + i - k] * alpha[k-1];
//			sum += (ss-samples[i])*(ss-samples[i]);
//		}
//		System.out.println(sum);
		//for (int i=0; i<p; i++) System.out.println(alpha[i]);
		double c[] = new double[p];
		c[0] = R[0];
		for (int n=1; n<p; n++) {
			c[n] = alpha[n];
			for (int k=1; k<n; k++) {
				c[n] += k / n * c[k] * alpha[n-k];
			}
		}
		
		return alpha;
	}
	
	public static double autocorelationFunc(double[] samples, int start, int N, int k) {
		double sum = 0;
		for (int n=start; n<=start+N-1-k; n++) {
			sum += samples[n] * samples[n+k];
		}
		
		return sum;
	}
	
//	public static double autocorelationFunc(double[] samples, int offset, int N, int k) {
//		double sum = 0;
//		for (int n=offset+0; n<offset+N; n++) {
//			sum += samples[n] * samples[n-k];
//		}
//		
//		return sum;
//	}
	
	public static void gaus(double[][] mat, int n) {
		for (int k=1; k<=n; k++) {
			double tmp = mat[k][k];
			for (int j=1; j<=n+1; j++) mat[k][j] /= tmp;	//set mat[k][k] = 1, and scale rest of the row
			for (int i=1; i<=n; i++) {
				if (i == k) continue;
				double d = mat[i][k] / mat[k][k];
				for (int j=1; j<=n+1; j++) mat[i][j] = mat[i][j] - d * mat[k][j];
			}
		}
	}
}
