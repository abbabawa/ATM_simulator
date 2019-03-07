/*
 * Title: Point of sale simulator
 * Purpose: CS412 assignment (Demonstrating structured programming)
 *  Author: Bawa Abba , member CS412 group 2
 */

package group2_cs412;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.*;

public class Reports {
	//Class variables
	private JTextArea textArea;
	private JButton[] reportsButtons = new JButton[10];
	private POSDatabaseClass POSDatabase;
	private JButton back;
	private ReportsListener reportsListener = new ReportsListener();
	
	//Constructor
	Reports(JTextArea textArea, JButton[] buttons, JButton back){
		this.back = back;
		this.back.addActionListener(reportsListener);
		
		//Create POSDatabaseClass object to establish database connection
		try{
			POSDatabase = new POSDatabaseClass();
		}catch(SQLException ex){
			textArea.setText("Error connecting to Database.\n Please check database connection.");
		}catch(ClassNotFoundException ex){
			textArea.setText("Error connecting to Database.\n Please check database connection.");
		}
		
		//If connection has been established, add action listener to buttons
		if(POSDatabase != null){
			for(int i = 0; i < 10; i++){
				buttons[i].addActionListener(reportsListener);
			}
			this.textArea = textArea;
			
			reportsButtons = buttons;
			
			textArea.setText("Print \n 1. Today \n 2. Last 1 day \n 3. Last 2 days \n 4. Last 3 days");
		}
	}
	
	//Listener class which defines actions to be taken when buttons are pressed
	public class ReportsListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			try{
				if(e.getSource()== reportsButtons[1]){
					POSDatabase.reports(POSDatabase.getDate());	
					e.setSource(reportsButtons[9]);
				}
				else if(e.getSource()== reportsButtons[2]){
					POSDatabase.reports(POSDatabase.getDate(1));
					e.setSource(reportsButtons[9]);
				}
				else if(e.getSource()== reportsButtons[3]){
					POSDatabase.reports(POSDatabase.getDate(2));
					e.setSource(reportsButtons[9]);
				}
				else if(e.getSource()== reportsButtons[4]){
					POSDatabase.reports(POSDatabase.getDate(3));
					e.setSource(reportsButtons[9]);
				}
				else if(e.getSource() == back){
					for(int i = 0; i < 10; i++){
						reportsButtons[i].removeActionListener(reportsListener);
						//e.setSource(reportsButtons[9]);
						reportsListener = null;
					}
				}
			}catch(SQLException ex){
				textArea.setText(ex.getMessage());
			}
		}
	}
}
