package WumpusEnvironment.View.MainWindow;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import WumpusEnvironment.Model.Agent.*;
import WumpusEnvironment.Model.Map.*;

public class Logger extends JPanel implements ActionListener {
	public static final String logSeparator = "---------------\r\n";
	
	JScrollPane logScrollPane; //the scroll pane that will hold the log
	static JTextArea log; //the actual log
	JButton saveLogButton; //the button to save the log
	JFileChooser logSaver;
	
	public Logger(){
		log = new JTextArea();
		log.setEditable(false);
		log.setFont(new Font("Consolas",Font.PLAIN, 12));
		log.setLineWrap(true);
		log.setWrapStyleWord(true);
		logScrollPane = new JScrollPane(log);
		logScrollPane.setWheelScrollingEnabled(true);
		logScrollPane.setMaximumSize(new Dimension(100, 400));
		saveLogButton = new JButton("Save log...");
		saveLogButton.addActionListener(this);
		setLayout(new BorderLayout());
		add(new JLabel("Agent Log", 0), "North");
		add(logScrollPane,"Center");
		add(saveLogButton,"South");
		validate();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if(o.equals(saveLogButton)){
			logSaver = new JFileChooser(".");
			logSaver.setDialogTitle("Choose where to save the log");
			logSaver.setFileSelectionMode(JFileChooser.FILES_ONLY);
	        logSaver.setFileFilter(new FileFilter() {
	        	 
	            public String getDescription() {
	                return "Log files (*.txt)";
	            }
	         
	            public boolean accept(File f) {
	                if (f.isDirectory()) {
	                    return true;
	                } else {
	                    return f.getName().toLowerCase().endsWith(".txt");
	                }
	            }
	        });
	        int result = logSaver.showOpenDialog(this);
	        if(result == JFileChooser.APPROVE_OPTION){
	        	String logFileName = logSaver.getSelectedFile().getName();
	        	if(logFileName.indexOf('.') < 0)
	        		logFileName += ".txt";
	        	File logFile = new File(logFileName);
	        	try
	            {
	              FileWriter localFileWriter = new FileWriter(logFile);
	              PrintWriter localPrintWriter = new PrintWriter(localFileWriter);
	              
	              localPrintWriter.println(log.getText());
	              localPrintWriter.close();
	            }
	            catch (IOException localIOException)
	            {
	              localIOException.printStackTrace();
	            }
	        }
		}
	}
	
	public static void generateLogEntry(Agent a, Grid g){
		//log.append(g.gridToString()); //print the grid
		log.append(logSeparator); //print the separator for the next round
		log.append("\r\n");
		log.append(a.locationToString()); //print the Agent's location
		log.append("Performance Value: " + a.getPerformanceValue() + "\r\n");
		//log.append("Agent heading: " + a.headingToString() + "\r\n"); //print the Agent's heading
		log.append(a.movementStatusToString()); //print what happened last step
	}
	
	public static void writeToLog(String s){
		log.append(s);
	}
	
	public static void clear(){
		log.setText(null);
	}
	
	public static void printMapStart(){
		writeToLog(logSeparator +
				" * Map start *\r\n" +
				logSeparator);
	}
}
