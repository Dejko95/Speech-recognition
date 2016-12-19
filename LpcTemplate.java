import java.util.ArrayList;

public class LpcTemplate {
	String word;
	int framesNumber;
	CoefVector[] vectors;
	
	static ArrayList<LpcTemplate> templateList = new ArrayList<>();
	static ArrayList<LpcTemplate> sampleList = new ArrayList<>();
	
	public LpcTemplate(String word, int framesNumber, CoefVector[] vectors) {
		this.word = word;
		this.framesNumber = framesNumber;
		this.vectors = vectors;
	}
	
}
