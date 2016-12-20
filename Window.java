import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Window extends JFrame {
	public Window() {
		setSize(800, 600);
		setTitle("Speech recognition");
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel pnl = new JPanel();
		setContentPane(pnl);
		pnl.setLayout(null);
		
		JButton addWav = new JButton("Add new wav");
		addWav.setBounds(50, 50, 100, 30);
		pnl.add(addWav);
		
		addWav.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//JFileChooser chooser = new JFileChooser("src/dataset/train44kHz");
				//chooser.showOpenDialog(null);
				//File file = chooser.getSelectedFile();
				//ReadWav.readFile(file, true);
				ReadWav.readFromFolder("src/dataset/train44kHz", true);
				for (LpcTemplate template : LpcTemplate.templateList) {
					System.out.println(template.word + ": " + template.vectors.length + " " + template.framesNumber);
				}
				System.out.println();
			}
		});
		
		JButton guessWav = new JButton("Guess");
		guessWav.setBounds(50, 150, 100, 30);
		pnl.add(guessWav);
		
		guessWav.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser chooser = new JFileChooser("src/dataset/test44kHz");
				chooser.showOpenDialog(null);
				File file = chooser.getSelectedFile();
				ReadWav.readFile(file, false);
				
				//System.out.println("templates: " + LpcTemplate.templateList.size());
				//System.out.println("samples: " + LpcTemplate.sampleList.size());
				int correct = 0;
				//for (LpcTemplate sample : LpcTemplate.sampleList) {
				LpcTemplate sample = LpcTemplate.sampleList.get(LpcTemplate.sampleList.size()-1);
					System.out.println("-------------- " + sample.word + " ---------------");
					System.out.println("length: " + sample.vectors.length);
					double minDiff = Double.MAX_VALUE;
					String guess = "?";
					for (LpcTemplate template : LpcTemplate.templateList) {
						double diff = DTW.difference(template.vectors, sample.vectors);
						System.out.printf("%s %.6f\n", template.word, diff);
						if (diff < minDiff) {
							minDiff = diff;
							guess = template.word;
						}
					}
					System.out.println(sample.word + ": " + guess);
				//}
				
			}
		});
		
		
		
		
		JButton guessAll = new JButton("Guess all");
		guessAll.setBounds(50, 250, 100, 30);
		pnl.add(guessAll);
		
		guessAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				ReadWav.readFromFolder("src/dataset/test44kHz", false);
				
				//System.out.println("templates: " + LpcTemplate.templateList.size());
				//System.out.println("samples: " + LpcTemplate.sampleList.size());
				int correct = 0;
				for (LpcTemplate sample : LpcTemplate.sampleList) {
				//LpcTemplate sample = LpcTemplate.sampleList.get(LpcTemplate.sampleList.size()-1);
					System.out.println("-------------- " + sample.word + " ---------------");
					System.out.println("length: " + sample.vectors.length);
					double minDiff = Double.MAX_VALUE;
					String guess = "?";
					for (LpcTemplate template : LpcTemplate.templateList) {
						double diff = DTW.difference(template.vectors, sample.vectors);
						System.out.printf("%s %.6f\n", template.word, diff);
						if (diff < minDiff) {
							minDiff = diff;
							guess = template.word;
						}
					}
					System.out.println(sample.word + ": " + guess);
					if (sample.word.startsWith(guess)) {
						correct++;
					}
				}
				System.out.println("Score: " + correct + "/" + LpcTemplate.templateList.size() + "  Acc: " + (double)correct/LpcTemplate.templateList.size() + "%");
			}
		});
		
		
		
	}
}
