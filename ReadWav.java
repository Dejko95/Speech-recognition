import java.io.File;
import java.io.IOException;

public class ReadWav {
	public static void readFromFolder(String folderName, boolean train) {
		File folder = new File(folderName);
		for (final File file : folder.listFiles()) {
			try {
				SpeechRecognition.getInstance().processWav(WavFile.openWavFile(file), file.getName(), train);
			} catch (IOException | WavFileException e) {
				e.printStackTrace();
			}
	    }
	}
	
	public static void readFile(File file, boolean train) {
		try {
			SpeechRecognition.getInstance().processWav(WavFile.openWavFile(file), file.getName(), train);
		} catch (IOException | WavFileException e) {
			e.printStackTrace();
		}
	}
	
	public static void recordWav() {
		
	}
}
