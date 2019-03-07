/*
 * Title: Point of sale simulator
 * Purpose: CS412 assignment (Demonstrating structured programming)
 *  Author: Abba Bawa, member CS412 group 2
 */

package group2_cs412;

import javax.swing.*;

import java.awt.event.*;
import java.sql.SQLException;
import java.util.InputMismatchException;

public class BillPayment {
	
	private JTextArea textArea = new JTextArea();
	private JButton[] billButtons = new JButton[10];
	private POSDatabaseClass POSDatabase;
	private JButton back;
	private BillPaymentListener billListener = new BillPaymentListener();
	
	//Class constructor requiring a textArea, an array of buttons and the back button
	BillPayment(JTextArea textArea, JButton[] buttons, JButton back){
		this.back = back;
		this.back.addActionListener(billListener);
		try{
			POSDatabase = new POSDatabaseClass();
		}catch(ClassNotFoundException ex){
			textArea.setText("Error connecting to Database.\n Please check database connection.");
		}catch(SQLException ex){
			textArea.setText("Error connecting to Database.\n Please check database connection.");
		}
		
		if(POSDatabase != null){
			for(int i = 0; i < 10; i++){
				billButtons[i] = buttons[i];
				billButtons[i].addActionListener(billListener);
			}
			
			billButtons = buttons;
			textArea.setText("\t Bill Payment \n 1. Water Bill \n 2. Light Bill");
		}
	}
	
	//Listener class for responding to button clicks
	class BillPaymentListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			try{
				String bill_type = "";
				if(e.getSource() == billButtons[1]){
					bill_type = "Water Bill";
					e.setSource(billButtons[9]);
				}
				else if(e.getSource() == billButtons[2]){
					bill_type = "Light Bill";
					e.setSource(billButtons[9]);
				}
				
				if(bill_type == ""){
					if(e.getSource() == back){
						for(int i = 0; i < 10; i++){
							billButtons[i].removeActionListener(billListener);
							billListener = null;
						}
					}
				}
				else{
					int noOfTries = 1;
					String message = "";
					int pin = 0;
					Double amount = 0.0;
					boolean continueLoop = false;
					
					
					do{
						String enteredAmount = JOptionPane.showInputDialog(message+"Enter amount: ");
						try{
							amount = Double.parseDouble(enteredAmount);
							if(amount <= 0){
								throw new IllegalArgumentException();
							}
							continueLoop = false;
							message = "";
						}catch(NumberFormatException ex){
							continueLoop = true;
							message = "Invalid value entered for amount. Please enter a valid number.\n";
						}catch(NullPointerException ex){
							continueLoop = true;
							message = "Invalid value entered for amount. Please enter a valid number.\n";
						}catch(InputMismatchException ex){
							continueLoop = true;
							message = "Invalid value entered for amount. Please enter a valid number.\n";;
						}catch(IllegalArgumentException ex){
							continueLoop = true;
							JOptionPane.showMessageDialog(null, "Negative value entered, \n Please make sure you enter a positive number");
						}
					}while(continueLoop);
					
					
					//while loop to allow the entry of a wrong or invalid pin only 3 times
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
						//if statement to check if pin entered is a valid pin number
						if(POSDatabase.validatePin(pin)){
							POSDatabase.saveBillPayment(bill_type, amount, POSDatabase.getDate(), POSDatabase.getTime());
							e = null;
							textArea.setText("Transaction successful, press the back button to return to main menu. Or \n 1. for Water bill \n 2. for Light Bill");
							noOfTries = 4;
							bill_type = "";
						}
						else{
							message  = "The last pin was incorrect. Please re-enter your pin\n";
							noOfTries++;
						}
					}
				}
			}catch(SQLException ex){
				textArea.setText(ex.getMessage());
			}
			
		}
		
	}
}
