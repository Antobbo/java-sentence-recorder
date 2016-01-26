/*SentenceRecorder.java
takes a sentence or word as input and saves it on text file. Every new word/sentence is appended at the end of the file
*/
import javax.swing.event.*;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.*;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.io.File;


import java.nio.file.*;//for readAllLines()
import java.nio.file.Paths;//used to get the path of the file
import java.util.ArrayList;//to work with the array list to save the sentences in an ArrayList 
import java.util.List;
import java.util.Collection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.Scanner;
import java.io.FileWriter;
import java.lang.SecurityException;//if no permission to write to file
import java.io.FileNotFoundException;//if the file can't be created
import java.io.IOException;
import java.util.Properties;//to get working directory
import java.io.BufferedWriter;

public class SentenceRecorder extends JFrame{	
	private JLabel instructions;
	private JLabel searchInstructions;
	private JButton submit;
	private JButton search;
	private GridBagLayout gbLayout;
	private JButton clear;
	private JTextArea input;
	private JTextArea searchResults;//displays the search results
	private String stringInput;
	private List< String > allSentences = new ArrayList< String >();//stores all the sentences in the file
	private String filePath;
	private String fileName;
	private JTextField searchCriterium;
	private File file;
	private BufferedWriter fileWriter;
	private String workingDir;
	private String separator;
	private Scanner scannerInput;//to input data into the file
	private Scanner scannerOutput;//to output data from the file
	private final static Charset ENCODING = StandardCharsets.UTF_8;
	public SentenceRecorder(){
		super("List of sentences to remember");
		searchCriterium = new JTextField();
		separator = System.getProperty("line.separator");//getting the system-dependent separator		
		workingDir = System.getProperty("user.dir");		
		instructions = new JLabel("Enter your sentence.");
		searchInstructions = new JLabel("Search the file");
		input = new JTextArea(10,12);//holds the text input
		searchResults = new JTextArea(10,12);
		submit = new JButton("Submit");//submit button
		//search = new JButton("Search");//search button
		clear = new JButton("Clear");//clear button
		stringInput = "";//initialize string to empty		
		gbLayout = new GridBagLayout();
		//System.out.println("workingDir is " + workingDir);
		//input = new Scanner(System.in);		
		fileName = "sample.txt";		
		file = new File(workingDir, fileName);
		allSentences = getSentenceList(fileName);//copying content of file in arrayList
		printSentenceArray(allSentences);
		//getSentenceContaining("sentence");
		setLayout(gbLayout);//set layout of jframe
		add(instructions, new GridBagConstraints(0,0,2,1,0,0,CENTER,HORIZONTAL, new Insets(10,15,10,15),0,0));
		add(new JScrollPane(input), new GridBagConstraints(0,1,2,1,0.5,0.5,CENTER,BOTH, new Insets(10,15,10,15),10,10));		
		add(submit, new GridBagConstraints(0,2,1,1,1.0,1.0,CENTER,HORIZONTAL,new Insets(10,15,10,15),1,1));
		add(clear, new GridBagConstraints(1,2,1,1,1.0,1.0,CENTER,HORIZONTAL,new Insets(10,15,10,15),1,1));
		add(searchInstructions, new GridBagConstraints(0,3,2,1,0,0,CENTER,HORIZONTAL, new Insets(10,15,10,15),0,0));
		add(searchCriterium, new GridBagConstraints(0,4,2,1,1.0,1.0,CENTER,HORIZONTAL,new Insets(10,15,10,15),1,1));
		add(new JScrollPane(searchResults), new GridBagConstraints(0,5,2,1,0.5,0.5,CENTER,BOTH, new Insets(10,15,10,15),10,10));
		//System.out.printf("Enter your sentence or end of file - ctrl+z or Enter+ctrl+d\n");			
		
		searchCriterium.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e){
				//System.out.println("changedUpdate");
			}
			public void removeUpdate(DocumentEvent e){				
				displayMatchedSentences(getSentenceContaining(getKeyword()));
			}
			public void insertUpdate(DocumentEvent e){				
				displayMatchedSentences(getSentenceContaining(getKeyword()));				
			}
		});
		
		ProcessButtonHandling handler1 = new ProcessButtonHandling();
		ClearButtonHandling handler2 = new ClearButtonHandling();
		//SearchButtonHandling handler3 = new SearchButtonHandling();
		submit.addActionListener(handler1);
		clear.addActionListener(handler2);	
	}//end of constructor
	//inner class for event handlings
	private class ProcessButtonHandling implements ActionListener{
		public void actionPerformed(ActionEvent event){
			stringInput = input.getText();//copy text from textArea to string
			scannerInput = new Scanner(stringInput);
			try{	
				fileWriter = new BufferedWriter( new FileWriter(file,true));
			}			
			catch(SecurityException securityException){//if you don't have write access
				System.err.println("You don't have write access to this file");
				System.exit(1);
			}
			catch(FileNotFoundException fileNotFoundException){
				System.err.println("Error opening or creating the file");
				System.exit(1);
			}
			catch(IOException ioexception){
				System.err.println("General Error with IO");
				System.exit(1);
			}
			while(scannerInput.hasNext()){
				stringInput = scannerInput.nextLine();
				try{
					fileWriter.append(stringInput + separator);
				}
				catch(IOException ioexception){
					System.err.println("General Error with IO");
					ioexception.printStackTrace();
					System.exit(1);
				}
				//System.out.printf("Enter your sentence or end of file - ctrl+z or Enter+ctrl+d\n");
			}
			CloseFile();
			Clear();
		}//end of actionPerformed
		
	}//end of inner class
	private class ClearButtonHandling implements ActionListener{
		public void actionPerformed(ActionEvent event){			
			Clear();
		}//end of actionPerformed		
	}
	/* private class SearchButtonHandling implements ActionListener{
		public void actionPerformed(ActionEvent event){
			//return the matched sentences
			//copy them into the textArea
		}
	} */
	private void Clear(){
		stringInput = "";
		input.setText("");
		CloseFile();		
	}
	public void CloseFile(){
		try{
			fileWriter.close();
		}
		catch(IOException ioexception){
				System.err.println("General Error with IO");
				ioexception.printStackTrace();
				System.exit(1);				
			}
	}//closeFile
	public List<String> getSentenceList(String theFileName){//save file text into array		
		Path path = Paths.get(theFileName);
		
		if(!(Files.isReadable(path))){
			System.out.println("The file is empty. You need to save something in it first.");//to be printed in the textArea
			return null;			
		}		
		try{
			return Files.readAllLines(path, ENCODING);			
		}
		catch(IOException ioexception){
			System.err.println("General Error with IO");
			ioexception.printStackTrace();
			System.exit(1);
		}
		return null;		
	}	
	public void printSentenceArray(List< String > toPrint){
		if((toPrint == null)||(toPrint.size() == 0)){//to avoid NPE
			System.out.println("List is empty: ");
			return;
		}
		System.out.println("Array contains " + toPrint.size() + " sentences:");
			for(String s: toPrint){
				//System.out.printf("%s", toPrint.get(count));
				System.out.println(s);
			}
				
	}
	public List<String> getSentenceContaining(String keyword){// search for keyword and return the whole string/s		
		List< String > matchedSentences = new ArrayList< String >();//stores all the matched sentences in the file			
		if(keyword.length() <= 3){			
			return matchedSentences;//empty list
		}
		System.out.printf("\n\nKeyword is %s. Sentence(s) containing the keyword:\n", keyword);
		
		for(String toFind : allSentences){//loop thru all sentences
			if(toFind.contains(keyword)){//find the ones with matching the keyword				
				matchedSentences.add(toFind);//add the found sentences to the List									
			}
		}			
		if(matchedSentences.size() == 0){//no matches
			System.out.println("None found matching the keyword");			
			matchedSentences.add(" no matches found");
		}	
		printSentenceArray(matchedSentences);
		return matchedSentences;		
	}	
	public String getKeyword(){//get keyword from input field
		String text = searchCriterium.getText();
		return text;
	}
	public void displayMatchedSentences(List< String > matchedSent){//loops thru matched sentences and display them
		searchResults.setText("");//clear output
		for(String theSentence : matchedSent){
			searchResults.append(theSentence + separator);
		}
		
	}
}//end of SentenceRecorder
