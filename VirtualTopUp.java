/*
 * Title: Point of sale simulator
 * Purpose: CS412 assignment (Demonstrating structured programming)
 *  Author: Abba Bawa, member CS412 group 2
 */

package group2_cs412;

import javax.swing.*;

import java.awt.event.*;
import java.sql.SQLException;

public class VirtualTopUp {
	
	//Declaring class variables
	private JTextArea textArea;
	private JButton[] topUpButtons = new JButton[10];
	private POSDatabaseClass POSDatabase;
	private String serviceProvider = "";
	private TopUpListener topUpListener = new TopUpListener();
	private JButton back;
	
	//Class constructor, called whenever an instance of the class is created to initialize variables and establish connection to database
	VirtualTopUp(JTextArea textArea, JButton[] buttons, JButton back){
		
		this.back = back;
		back.addActionListener(topUpListener);
		
		//try block used to create a database connection and corresponding catch block to catch any error that may be thrown specifically SQLException and ClassNotFoundException
		try{
			POSDatabase = new POSDatabaseClass();
		}catch(SQLException ex){
			textArea.setText("Error connecting to Database.\n Please check database connection.");
		}catch(ClassNotFoundException ex){
			textArea.setText("Error connecting to Database.\n Please check database connection.");
		}
		
		if(POSDatabase != null){
			for(int i = 0; i < 10; i++){
				topUpButtons[i] = buttons[i];
				topUpButtons[i].addActionListener(topUpListener);
			}
			this.textArea = textArea;
			
			topUpButtons = buttons;
			
			textArea.setText(" VTU Menu \n 1. MTN \n 2. Etisalat \n 3. Glo \n 4. Airtel");
		}
	}
	
	//Listener class to respond to button clicks
	public class TopUpListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			try{
				//if else statements to set the service provider before continuing with transaction
				if(e.getSource()== topUpButtons[1]){
					serviceProvider = "MTN";
					e.setSource(topUpButtons[9]);
				}
				else if(e.getSource() == topUpButtons[2]){
					serviceProvider = "Etisalat";
					e.setSource(topUpButtons[9]);
				}
				else if(e.getSource() == topUpButtons[3]){
					serviceProvider = "Glo";
					e.setSource(topUpButtons[9]);
				}
				else if(e.getSource() == topUpButtons[4]){
					serviceProvider = "Airtel";
					e.setSource(topUpButtons[9]);
				}
				
				//if statement to check if service provider has been set, if yes call method buy airtime
				if(serviceProvider != ""){
					buyAirtime(serviceProvider);
					serviceProvider = "";
				}
				else if(e.getSource() == back){//Check if back button has been clicked 
					for(int i = 0; i < 10; i++){
						topUpButtons[i].removeActionListener(topUpListener);
						topUpListener = null;
					}
				}
				
			}catch(SQLException ex){
				textArea.setText(ex.getMessage());
			}
		}
	}
	
	//Method buyAirtime is called after the user has picked a service provider, this method proceeds with the transaction and stores details in the database
	public void buyAirtime(String serviceProvider) throws SQLException{
		int noOfTries = 1;
		String message = "";
		double amount = 0;
		int pin = 0;
		boolean continueLoop = false;
		String phoneNumber;
		
		do{
			String enteredAmount = JOptionPane.showInputDialog(message+"Enter amount ");
			try{
				amount = Double.parseDouble(enteredAmount);
				if(amount <= 0){
					throw new IllegalArgumentException();
				}
				continueLoop = false;
				message = "";
			}catch(NumberFormatException ex){
				continueLoop = true;
				message = "The value entered for amount is invalid, please enter a valid number.\n";
			}catch(IllegalArgumentException ex){
				continueLoop = true;
				JOptionPane.showMessageDialog(null, "Negative value entered, \n Please make sure you enter a positive number");
			}
		}while(continueLoop);
		
		//confirm if phone number is a valid number
		do{
			phoneNumber = JOptionPane.showInputDialog(message+"Enter Phone Number");
			try{
				int check = Integer.parseInt(phoneNumber);
				continueLoop = false;
				message = "";
			}catch(NumberFormatException ex){
				continueLoop = true;
				message = "You have entered an invalid phone number. \nPlease enter a valid mobile phone number. \ne.g 08132217929\n";
			}
			//check length of phone number
			if(phoneNumber.length() == 11){
				continueLoop = false;
				message = "";
			}else{
				continueLoop = true;
				message = "You have entered an invalid phone number. \nPlease enter a valid mobile phone number. \ne.g 08132217929\n";
			}
		}while(continueLoop);
		
		
		//while loop to allow user 3 chances to enter a valid pin
		while(noOfTries <= 3){
			String enteredPin = "";
			try{
				JPasswordField pass = new JPasswordField(5);
				int result = JOptionPane.showConfirmDialog(null, pass,message+" Enter your pin", JOptionPane.OK_CANCEL_OPTION);
				if(result == JOptionPane.OK_OPTION){
					enteredPin = pass.getText();
				}
				pin = Integer.parseInt(enteredPin);
			}catch(NumberFormatException ex){
				
			}
			//if statement to check if entered pin matches an entry in the database
			if(POSDatabase.validatePin(pin)){
				POSDatabase.saveVTU(serviceProvider, amount, POSDatabase.getDate(), POSDatabase.getTime(), phoneNumber);
				textArea.setText("VTU \n Transaction successful. \nPlease press back to go back to main menu. \nOr press\n 1. for MTN \n 2. for Etisalat \n 3. for GLO \n 4. for Airtel" );
				noOfTries = 4;
				return;
			}
			else{
				//Message to be displayed to user if he/she enters an invalid pin
				message  = "The last pin was incorrect. Please re-enter your pin\n";
				noOfTries++;
			}
		}
	}
}
