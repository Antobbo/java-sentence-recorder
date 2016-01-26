/*SentenceRecorderTest.java*/
import javax.swing.JFrame;
public class SentenceRecorderTest{
	public static void main(String[] args){	
		SentenceRecorder sentenceRecorder = new SentenceRecorder();
		sentenceRecorder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		
		sentenceRecorder.pack();
		
		sentenceRecorder.setVisible(true);
	}
}
