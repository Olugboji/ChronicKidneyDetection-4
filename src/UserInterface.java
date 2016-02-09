import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

/**
 * Contains a basic GUI in which user will be able to select the training and testing
 * files and see the results in a text area. 
 *
 */
public class UserInterface {
	
	/**
	 * Main method for this project. 
	 * @param args
	 */
    public static void main(String[] args) {
        new UserInterface();
    }
    
    /**
     * Default Constuctor for this class. A graphical user interface is created containing user options. 
     */
    public UserInterface() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                JFrame frame = new JFrame("Chronic Kidney Disease Detection");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    /**
     * Panel is the simplest container class. A panel provides space in which an application 
     * can attach any other component, including other panels. The default layout manager 
     * for a panel is the FlowLayout layout manager.
     *
     */
    public class TestPane extends JPanel {

        /**
		 * Number of iterations in cross validation
		 */
    	public static final int ITERATIONS =5 ;
    	
		private static final long serialVersionUID = 1L;

		JTextField attrField; 
		/**
         * Button that is used for selecting training file
         */
		private JButton openTrainingFile;

        /**
         * Button that is used for selecting testing file
         */
        private JButton openTestingFile;
        
        /**
         * Button that is used for selecting testing file
         */
        private JButton crossValidationButton;
        /**
         * JLabel for  number of attributes label
         */
        private JLabel attLabel;
        
        /**
         * Text Area which shows current status of the simulation
         */
        private JTextArea textArea;

        /**
         * Instance of fileChooser which is used to select arff files
         * 
         */
        private JFileChooser chooser;

        private int numAttribute;
        /**
         * Default constructor for the Panel.
         */
        public TestPane() {
        	
        	final Classifier<String, String> bayes =
                    new BayesClassifier<String, String>();
            setLayout(new FlowLayout());
            attLabel = new JLabel("Atrribute #");
            numAttribute = 24;
            openTrainingFile = new JButton("Open Training File");
            openTestingFile = new JButton("Open Testing File");
            crossValidationButton = new JButton("Cross Validate");
            attrField =new JTextField(10);
            textArea = new JTextArea(20, 40);
            textArea.setEnabled(false);
            textArea.setText("Select training/Cross Validate");
            add(new JScrollPane(textArea));
            add(attLabel);
            add(attrField);
            add(openTrainingFile);
            add(openTestingFile);
            add(crossValidationButton);
            openTestingFile.setEnabled(false);
            openTrainingFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (chooser == null) {
                        chooser = new JFileChooser();
                        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        chooser.setAcceptAllFileFilterUsed(false);
                        String s = attrField.getText().trim();
                        if(s.length() == 0 || allNumbers(s) ==false)
                        {
                        	
                        }
                        else
                        {
                        	numAttribute = Integer.parseInt(s);
                        }
                        chooser.addChoosableFileFilter(new FileFilter() {
                            @Override
                            public boolean accept(File f) {
                                return f.isDirectory() || f.getName().toLowerCase().endsWith(".arff");
                            }

                            @Override
                            public String getDescription() {
                                return "Text Files (*.arff)";
                            }
                        });
                    }

                    switch (chooser.showOpenDialog(TestPane.this)) {
                        case JFileChooser.APPROVE_OPTION:
                            try (BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {
                                textArea.setText(null);
                                String sCurrentLine = null;
                                int count = 0;
                                while ((sCurrentLine = br.readLine()) != null) {
                                    textArea.append(sCurrentLine);
                                    textArea.append("\n");
                                    if(sCurrentLine.length() > 1)
                    				if(Character.isDigit(sCurrentLine.charAt(0)) || sCurrentLine.charAt(0) == '?')
                    				{
                    					String tokens[] = sCurrentLine.split(",");
                    					String text[] = new String[tokens.length-1];
                    					for(int i = 0; i < numAttribute; i++)
                    					{
                    						text[i] = tokens[i];
                    					}
                    					bayes.learn(tokens[tokens.length-1], Arrays.asList(text));
                    					count++;
                    				}
                                }
                                textArea.append(count  + " instances read for training.\n");
                                textArea.append("Select testing file.");
                                openTestingFile.setEnabled(true);
                                openTrainingFile.setEnabled(false);
                                
                            } catch (IOException exp) {
                                exp.printStackTrace();
                                JOptionPane.showMessageDialog(TestPane.this, "Failed to read file", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                    }
                }
            });
            openTestingFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (chooser == null) {
                        chooser = new JFileChooser();
                        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        chooser.setAcceptAllFileFilterUsed(false);
                        chooser.addChoosableFileFilter(new FileFilter() {
                            @Override
                            public boolean accept(File f) {
                                return f.isDirectory() || f.getName().toLowerCase().endsWith(".arff");
                            }

                            @Override
                            public String getDescription() {
                                return "Text Files (*.arff)";
                            }
                        });
                    }

                    switch (chooser.showOpenDialog(TestPane.this)) {
                        case JFileChooser.APPROVE_OPTION:
                        	int successfulTests = 0;
                            int failTests = 0;
                            
                            try (BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {
                                textArea.setText(null);
                                String sCurrentLine;
                                while ((sCurrentLine = br.readLine()) != null) {
                    				
                                	textArea.append(sCurrentLine);
                                    textArea.append("\n");
                                    if(sCurrentLine.length() > 1)
                    				if(Character.isDigit(sCurrentLine.charAt(0)) || sCurrentLine.charAt(0) == '?')
                    				{
                    					String tokens[] = sCurrentLine.split(",");
                    					String text[] = new String[tokens.length-1];
                    					for(int i = 0; i < numAttribute; i++)
                    					{
                    						text[i] = tokens[i];
                    					}
                    					if(bayes.classify(Arrays.asList(text)).getCategory().equals(tokens[tokens.length-1]))
                    					{
                    						successfulTests++;
                    					}
                    					else
                    					{
                    						failTests++;
                    					}
                    				}
                    			}
                               
                                
                                if(successfulTests > 0)
                                {
                    	            double accuracy = (double)(successfulTests)/((double)(successfulTests) + (double)(failTests));
                    	    		accuracy*=100.00;
                    	    		textArea.append("Total Tests: " + (successfulTests + failTests) + "\n");
                    	    		textArea.append("Successful Tests: " + (successfulTests) + "\n");
                    	    		textArea.append("Failed Tests: " + (failTests)+ "\n");
                    	    		textArea.append("Accuracy: " + accuracy+ "\n");
                                }
                            } catch (IOException exp) {
                                exp.printStackTrace();
                                JOptionPane.showMessageDialog(TestPane.this, "Failed to read file", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                    }
                }
            });
            crossValidationButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	if (chooser == null) {
                        chooser = new JFileChooser();
                        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        chooser.setAcceptAllFileFilterUsed(false);
                        chooser.addChoosableFileFilter(new FileFilter() {
                            @Override
                            public boolean accept(File f) {
                                return f.isDirectory() || f.getName().toLowerCase().endsWith(".arff");
                            }

                            @Override
                            public String getDescription() {
                                return "Text Files (*.arff)";
                            }
                        });
                    }

                    switch (chooser.showOpenDialog(TestPane.this)) {
                        case JFileChooser.APPROVE_OPTION:
                        	int tSuccess = 0;
                        	int tFailure = 0;
                        	double avgAccuracy = 0;
                        	String s = attrField.getText().trim();
                            if(s.length() == 0 || allNumbers(s) ==false)
                            {
                            	
                            }
                            else
                            {
                            	numAttribute = Integer.parseInt(s);
                            }
                        	for(int k = 0; k < ITERATIONS; k++)
                        	{
	                        	int count = 0;
	                        	textArea.append("Iteration: #" + (k+1) + "\n");
	                        	ArrayList<String> trainingData = new ArrayList<String>();
                                ArrayList<String> testingData = new ArrayList<String>();
	                            try (BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {
	                                //textArea.setText(null);
	                                String sCurrentLine = null;
	                                while ((sCurrentLine = br.readLine()) != null) {
	                                    if(sCurrentLine.length() > 1)
	                                    {
		                    				if(Character.isDigit(sCurrentLine.charAt(0)) || sCurrentLine.charAt(0) == '?')
		                    				{
		                    					count++;
		                    				}
	                                    }
	                                }
	                            } catch (IOException exp) {
	                                exp.printStackTrace();
	                                JOptionPane.showMessageDialog(TestPane.this, "Failed to read file", "Error", JOptionPane.ERROR_MESSAGE);
	                            }
	                            int numIntervals = 0;
	                            numIntervals  = count/10;
	                            textArea.append("Num Intervals: " + numIntervals + "\n");
	                            try (BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {
	                                //textArea.setText(null);
	                                String sCurrentLine = null;
	                                int index = getRandom(0, 10);
	                                int tCnt = 0;
	                                while ((sCurrentLine = br.readLine()) != null) {
	                                    
	                                    if(sCurrentLine.length() > 1)
		                    				if(Character.isDigit(sCurrentLine.charAt(0)) || sCurrentLine.charAt(0) == '?')
		                    				{
		                    					if(tCnt %10 != index)
                    							{
                    								trainingData.add(sCurrentLine);
                    								String tokens[] = sCurrentLine.split(",");
                                					String text[] = new String[tokens.length-1];
                                					for(int l = 0; l < numAttribute; l++)
                                					{
                                						text[l] = tokens[l];
                                					}
                                					bayes.learn(tokens[tokens.length-1], Arrays.asList(text));
                    							}
                    							else
                    							{
                    								testingData.add(sCurrentLine);
                    							}
		                    					tCnt++;	
		                    				}
	                                }
	                                textArea.append("Training Size: " + trainingData.size() + " Testing Data: " + testingData.size() + "\n");
	                                int successfulTests = 0;
	                                int failTests = 0;
	                                for(int i = 0; i < testingData.size(); i++)
	                                {
	                                	String tokens[] = testingData.get(i).split(",");
                    					String text[] = new String[tokens.length-1];
                    					for(int j = 0; j < numAttribute; j++)
                    					{
                    						text[j] = tokens[j];
                    					}
                    					if(bayes.classify(Arrays.asList(text)).getCategory().equals(tokens[tokens.length-1]))
                    					{
                    						successfulTests++;
                    					}
                    					else
                    					{
                    						failTests++;
                    					}
	                                }
	                                textArea.append(trainingData.size() + " instances read for training.\n");
	                                textArea.append(testingData.size() + " instances read for testing.\n");
	                                if(successfulTests > 0)
	                                {
	                                	
	                    	            double accuracy = (double)(successfulTests)/((double)(successfulTests) + (double)(failTests));
	                    	    		accuracy*=100.00;
	                    	    		textArea.append("Total Tests: " + (successfulTests + failTests) + "\n");
	                    	    		textArea.append("Successful Tests: " + (successfulTests) + "\n");
	                    	    		textArea.append("Failed Tests: " + (failTests)+ "\n");
	                    	    		textArea.append("Accuracy: " + accuracy+ "\n");
	                    	    		tSuccess += successfulTests;
	                    	    		tFailure += failTests;
	                                }
	                                
	                                
	                            } catch (IOException exp) {
	                                exp.printStackTrace();
	                                JOptionPane.showMessageDialog(TestPane.this, "Failed to read file", "Error", JOptionPane.ERROR_MESSAGE);
	                            }
                        	}
                        	textArea.append("------------------Complete Statistics------------------\n");
                        	avgAccuracy = (double)(tSuccess)/((double)(tSuccess) + (double)(tFailure));
                        	avgAccuracy*=100.00;
             	    		textArea.append("Total Tests: " + (tSuccess + tFailure) + "\n");
             	    		textArea.append("Successful Tests: " + (tSuccess) + "\n");
             	    		textArea.append("Failed Tests: " + (tFailure)+ "\n");
             	    		textArea.append("Average Accuracy: " + avgAccuracy+ "\n");
             	    		
	                        break;
                            
                    }
                    
                }
            });
            
        }
        /**
         * Generates and return a random number between high and low
         * @param high Maximum Range
         * @param low Minimum range
         * @return random number in range
         */
        public int getRandom( int low, int high)
        {
        	Random r = new Random();
        	int result = r.nextInt(high-low) + low;
        	return result;
        }
        /**
         * Checks if string contains all digits
         * @param s String to be checked
         * @return true if contains all digits, false otherwise
         */
        public boolean allNumbers(String s)
        {
        	for(int i = 0; i < s.length(); i++)
        	{
        		if(Character.isDigit(s.charAt(i)) == false)
        			return false;
        	}
        	return true;
        }
    }

}