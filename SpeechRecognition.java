import java.io.*;

public class SpeechRecognition {

	WavFile wavFile;
	double noiseLevel;
	int samplesNumber;
	double samples[];
	double samplesAbsolute[];
	boolean speaking[];
	int samplesPerWindow;
	int windowNum;
	int startWordWindow;
	int endWordWindow;
	
	private static SpeechRecognition instance = null;

	final static int endpointWindowSize = 15; 		// Milliseconds
	final static int slidingWindowNum = 18;
	final static int maxZeroNum = 8;
	final static int maxOnesNum = 8;
	final static int maxZCT = 25; 		// Maximum Zero-Crossing Threshold
	
	public static SpeechRecognition getInstance() {
		if (instance == null) {
			instance = new SpeechRecognition();
		}
		return instance;
	}
	
	private SpeechRecognition() {
	}
	
	private double findNoiseLevel() {
		int silenceSamplesNumber = (int)wavFile.getSampleRate()/10; //num of samples in 100ms
		double sum = 0;
		
		for (int i=0; i<silenceSamplesNumber; i++) {
			sum += samplesAbsolute[i];
		}
		
		double meanValue = sum / silenceSamplesNumber;
		double standardDeviation = 0;
		
		for(int i=0; i<silenceSamplesNumber; i++) {
	        standardDeviation += Math.pow(samplesAbsolute[i] - meanValue, 2);
		}
	        
	    standardDeviation = Math.sqrt(standardDeviation / silenceSamplesNumber);
		
		return meanValue + 2 * standardDeviation;
	}
	
	private boolean[] endpoint() {
		samplesPerWindow = (int)wavFile.getSampleRate() * endpointWindowSize / 1000;	//1000ms : sampleRate = endpointWindowSize : x 
		windowNum = samplesNumber / samplesPerWindow;
		boolean talk[] = new boolean[windowNum];
		
		for (int win=0; win<windowNum; win++) {
			double sum = 0;
			
			for (int sam = win * samplesPerWindow; sam < (win + 1) * samplesPerWindow; sam++) {
				sum += samplesAbsolute[sam];
			}
			
			double avg = sum / samplesPerWindow;
			if (avg > noiseLevel) {
				talk[win] = true;
				//System.out.println("..." + win);
			} else {
				talk[win] = false;
			}
		}
		
		smoothUp(talk);
		smoothDown(talk);
		
		startWordWindow = -1;

	    for (int win=0; win<windowNum; win++) {
	        if (talk[win]) {
	        	//System.out.println(win + ": " + 1);
	            if (startWordWindow == -1) {
	                startWordWindow = win;
	            }
	            endWordWindow = win;
	        }
	        //else 
	        //	System.out.println(win + ": " + 0);
	    }
	    
	    System.out.println(startWordWindow + "__" + endWordWindow);
	    
	    zcr(talk);

	    //System.out.println(startWordWindow + "__" + endWordWindow);
	    
		return talk;
	}
	
	private void smoothUp(boolean talk[]) {
		//System.out.println(slidingWindowNum + "/" + maxOnesNum);
		
		for (int swin=0; swin < windowNum - slidingWindowNum + 1; swin++) {
			if (!talk[swin] || !talk[swin + slidingWindowNum - 1]) continue;

			int zeroNum = 0;
			
			for (int win=swin; win < swin + slidingWindowNum; win++) {
				if (!talk[win]) {
					zeroNum++;
				}
			}
			
			if (zeroNum <= maxZeroNum) {
				for (int win=swin; win < swin + slidingWindowNum; win++) {
					talk[win] = true;
				}
				//System.out.println(swin + "~~~~");
		    }	

			//System.out.println(swin + ": " + zeroNum);
		}

		for (int i=0; i<windowNum; i++) {
			if (talk[i]) {
				//System.out.println("++ " + i);
			}
		}
	}	
		
	private void smoothDown(boolean talk[]) {
		for (int swin=0; swin < windowNum - slidingWindowNum + 1; swin++) {
			if (talk[swin] || talk[swin + slidingWindowNum - 1]) continue;

			int onesNum = 0;
			
			for (int win=swin; win < swin + slidingWindowNum; win++) {
				if (talk[win]) {
					onesNum++;
				}
			}
			
			if (onesNum <= maxOnesNum) {
				for (int win=swin; win < swin + slidingWindowNum; win++) {
					talk[win] = false;
				}
		    }	
		}
	}
	
	private void zcr(boolean[] talk) {
		double ZCT = calcZCT();
		//System.out.println("zct: " + ZCT);
		
	    int zcrWindowNumber = 250 / endpointWindowSize;
	    int newStartWordWindow = startWordWindow;

	    for (int i = startWordWindow - 1; i >= startWordWindow - zcrWindowNumber; i--) {
	        int count = 0;
	        for (int j = i * samplesPerWindow + 1; j < (i + 1) * samplesPerWindow; j++) {
	            if (samples[j - 1] * samples[j] < 0) {
	                count++;
	            }
	        }

	        if (count < ZCT) {
	            break;
	        } else {
	            newStartWordWindow--;
	            talk[newStartWordWindow] = true;
	        }
	    }

	    startWordWindow = newStartWordWindow;

	    int newEndWordWindow = endWordWindow;

	    for (int i = endWordWindow + 1; i <= endWordWindow + zcrWindowNumber; i++) {
	        int count = 0;
	        for (int j = i * samplesPerWindow + 1; j < (i + 1) * samplesPerWindow; j++) {
	            if (samples[j - 1] * samples[j] < 0) {
	                count++;
	            }
	        }

	        if (count < ZCT) {
	            break;
	        } else {
	        	newEndWordWindow++;
	            talk[newEndWordWindow] = true;
	        }
	    }

	    endWordWindow = newEndWordWindow;
	    //System.out.println(startWordWindow + "___" + endWordWindow);

	}
	
	private double calcZCT() {
		int silenceSamplesNumber = (int)wavFile.getSampleRate() / 10; //num of samples in 100ms	
		
		int silenceWindnowNum = silenceSamplesNumber / samplesPerWindow;
		int crossCounts[] = new int[windowNum];
		int sum = 0;
		
		for (int win=0; win<silenceWindnowNum; win++) {
	        crossCounts[win] = 0;
	        for (int sam = win * samplesPerWindow + 1; sam < (win + 1) * samplesPerWindow; sam++) {
	            if (samples[sam-1] * samples[sam] < 0) {
	            	crossCounts[win]++;
	            }
	        }

	        sum += crossCounts[win];
	    }

		double mean = (double)sum / silenceWindnowNum;
		double standardDeviation = 0;

	    for(int win=0; win<silenceWindnowNum; win++)
	        standardDeviation += Math.pow(crossCounts[win] - mean, 2);

	    standardDeviation = Math.sqrt(standardDeviation / silenceWindnowNum);
	    
	    double ZCT = mean + 2 * standardDeviation;
	    if (ZCT > maxZCT) ZCT = maxZCT;

	    return ZCT;
	}

	public void processWav(WavFile wavFile, String word, boolean train) {
		try {
			this.wavFile = wavFile;
			//wavFile.display();
			
			samplesNumber = (int)wavFile.getFramesRemaining();
			samples = new double[samplesNumber];
			wavFile.readFrames(samples, samplesNumber);
			samplesAbsolute = new double[samplesNumber];
			for (int i=0; i<samplesNumber; i++) {
				samplesAbsolute[i] = Math.abs(samples[i]);
			}
			
			noiseLevel = findNoiseLevel();
			
			word = word.substring(0, word.indexOf('.'));
			
			System.out.println(word + ":");
			speaking = endpoint();
			
			for (int win=0; win<windowNum; win++) {
				for (int s=win*samplesPerWindow; s<(win+1)*samplesPerWindow; s++) {
					samples[s] = hamming(s - win*samplesPerWindow, samplesPerWindow) * samples[s];
				}
			}
			
			CoefVector[] vectors = new CoefVector[endWordWindow - startWordWindow + 1];
			
			for (int win=startWordWindow; win<=endWordWindow; win++) {
				vectors[win - startWordWindow] = new CoefVector(LPC.calcCoefficientVector(samples, win * samplesPerWindow, samplesPerWindow));
			}
		    //System.out.println(startWordWindow + "__" + endWordWindow);
			LpcTemplate template = new LpcTemplate(word, endWordWindow - startWordWindow + 1, vectors);
			if (train) {
				LpcTemplate.templateList.add(template);
			}
			else {
				LpcTemplate.sampleList.add(template);
			}
		} catch (IOException | WavFileException e) {
			e.printStackTrace();
		}
	}
	
	private double hanning(double x, int n) {
	    return 0.5 * (1 - Math.cos((2 * Math.PI * x) / (n - 1)));
	}
	
	private double hamming(double x, int n) {
	    return 0.54 - 0.46 * Math.cos((2 * Math.PI * x) / (n - 1));
	}

}
