
public class DTW {
	private static double d[][];
	public static double difference(CoefVector[] temp, CoefVector[] samp) {
//		if (d != null) {
//			for (int i=0; i<temp.length; i++) {
//				for (int j=0; j<samp.length; j++) {
//					System.out.printf("%.6f\t", d[i][j]);
//				}
//				System.out.println();
//			}
//		}
		d = new double[temp.length][samp.length];
		for (int i=0; i<temp.length; i++) {
			for (int j=0; j<samp.length; j++) {
				d[i][j] = -1;
			}
		}
		d[0][0] = temp[0].distanceFrom(samp[0]);
		for (int j=1; j<samp.length; j++) {
			d[0][j] = temp[0].distanceFrom(samp[j]) + d[0][j-1];
		}
		d[1][1] = temp[1].distanceFrom(samp[1]) + d[0][0];
		for (int j=2; j<samp.length; j++) {
			d[1][j] = temp[1].distanceFrom(samp[j]) + Math.min(d[1][j-1], d[0][j-1]);
		}
		
		for (int i=2; i<temp.length; i++) {
			int start = (i+1) / 2;
			if (i % 2 == 0) {
				d[i][start] = temp[i].distanceFrom(samp[start]) + d[i-2][start-1];
			}
			else {
				try {
					d[i][start] = temp[i].distanceFrom(samp[start]) + Math.min(d[i-2][start-1], d[i-1][start-1]);
					
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(i + " " + start);
					System.out.println(d[i-2][start-1]);
					System.out.println(d[i-1][start-1]);
				}
			}
			for (int j=start+1; j<samp.length; j++) {
				d[i][j] = temp[i].distanceFrom(samp[j]) + Math.min(Math.min(d[i][j-1], d[i-1][j-1]), d[i-2][j-1]); 
			}
		}
		
		return d[temp.length-1][samp.length-1];
		//return diff(temp, temp.length-1, samp, samp.length-1);
	}
	
	private static double diff(CoefVector[] temp, int i, CoefVector[] samp, int j) {
		if (i < 0 || j < 0 || i > 2*j) return 9999;
		if (d[i][j] != -1) return d[i][j];
		d[i][j] = temp[i].distanceFrom(samp[j]) + Math.min(Math.min(diff(temp, i, samp, j-1), 
																diff(temp, i-1, samp, j-1)), 
																diff(temp, i-2, samp, j-1)); 
		if (temp[i].distanceFrom(samp[j]) > 9999) {
			System.out.println(i + " . " + j);
		}
		return d[i][j];
	}

}
