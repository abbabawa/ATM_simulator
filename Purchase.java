/*
 * Title: Point of sale simulator
 * Purpose: CS412 assignment (Demonstrating structured programming)
 *  Author: Abba Bawa, member CS412 group 2
 */

package group2_cs412;

import javax.swing.*;

import java.awt.event.*;
import java.sql.*;
import java.util.InputMismatchException;

public class Purchase {
	
	//Class variables
	private JTextArea textArea = new JTextArea();
	private JButton[] purchaseButtons = new JButton[10];
	private POSDatabaseClass POSDatabase;
	private JButton back;
	
	private PurchaseListener purchaseListener = new PurchaseListener();
	
	/*Class Constructor. Constructor accepts 3 parameters, a textarea it uses to display menu and options to the user,
	 * an array of number buttons which the user uses to interact with the class and the back button which is used to 
	 * go back to the main menu*/
	Purchase(JTextArea textArea, JButton[] buttons, JButton back){
		
		this.back = back;
		
		this.back.addActionListener(purchaseListener);
		for(int i = 0; i < 10; i++){
			purchaseButtons[i] = buttons[i];
		}
		try{
			POSDatabase = new POSDatabaseClass();
		}catch(ClassNotFoundException ex){
			textArea.setText("Error connecting to Database.\n Please check database connection.");
		}catch(SQLException ex){
			textArea.setText("Error connecting to Database.\n Please check database connection.");
		}
		
		//If statement to check if database connection was successfully created before displaying menu
		if(POSDatabase != null){
			for(int i = 0; i < 10; i++){
				purchaseButtons[i].addActionListener(purchaseListener);
			}
			
			this.textArea = textArea;
			textArea.setText("Purchase \n 1. Pay \n 2. Reprint");
		}
	}
	
	//Method to accept and store payment information in the database
	public void pay() throws SQLException, InputMismatchException, NumberFormatException{
		double amount = 0.0;
		String message = "";
		int noOfTries = 1;
		//textArea.setText("Pay \n Enter amount: "+amount);
		String purpose = JOptionPane.showInputDialog("Goods or service paid for: ");
		boolean continueLoop = false;
		String enteredAmount;
		int pin = 0;
		
		//Loop to avoid invalid input for amount
		do{
			continueLoop = false;
			enteredAmount = JOptionPane.showInputDialog(message+"Enter Amount");
			try{
				amount = Double.parseDouble(enteredAmount);
				if(amount <= 0){
					throw new IllegalArgumentException();
				}
				message = "";
			}catch(InputMismatchException ex){
				continueLoop = true;
				message = "Invalid value entered for amount. Please enter a valid number.\n";
				amount = 0;
			}catch(NumberFormatException ex){
				continueLoop = true;
				message = "Invalid value entered for amount. Please enter a valid number.\n";
			}
			catch(IllegalArgumentException ex){
				continueLoop = true;
				JOptionPane.showMessageDialog(null, "Negative value entered, \n Please make sure you enter a positive number");
			}
		}while(continueLoop);
		
		//while loop to allow only 3 tries for password
		while(noOfTries <= 3){
				String enteredPin = "";
				try{
					JPasswordField pass = new JPasswordField(5);
					int result = JOptionPane.showConfirmDialog(null, pass,message+" Enter your pin", JOptionPane.OK_CANCEL_OPTION);
					if(result == JOptionPane.OK_OPTION){
						enteredPin = pass.getText();
					}
					pin = Integer.parseInt(enteredPin);
				}catch(InputMismatchException ex){
					
				}catch(NumberFormatException ex){
					
				}
			
			if(POSDatabase.validatePin(pin)){
				textArea.setText("Purchase");
				//if statement to check if transaction was successfully stored in database and display appropriate message to user
				if(POSDatabase.savePurchase(purpose, amount, POSDatabase.getDate(), POSDatabase.getTime())){
					textArea.setText("Transaction successful. \n Press back to go to main menu, Or \n 1. to pay \n 2. to reprint receipt" );
				}
				else{
					textArea.setText("Transaction was not successful. \n Please press back to go to main menu and try again" );
				}
				noOfTries = 4;
			}
			else{
				message  = "The last pin was incorrect. Please enter your atm pin.\n";
				noOfTries++;
			}
		}
	}
	
	//Method to reprint details of last purchase transaction
	public void reprint() throws SQLException{
		POSDatabase.rePrint(textArea);
	}
	
	//Inner listener class to respond to button clicks
	class PurchaseListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			if(e.getSource() == purchaseButtons[1]){
				try {
					pay();
					return;
				} catch (SQLException e1) {
					textArea.setText(e1.getMessage());
				}catch(NumberFormatException ex){
					textArea.append("\nWrong value entered,\n please make sure you enter a valid number. \nPress back to restart entry");
				}
				e.setSource(purchaseButtons[9]);
			}
			else if(e.getSource() == purchaseButtons[2]){
				try {
					reprint();
				} catch (SQLException e1) {
					textArea.setText(e1.getMessage());
				}
				e.setSource(purchaseButtons[9]);
			}
			else if(e.getSource() == back){
				for(int i = 0; i < 10; i++){
					purchaseButtons[i].removeActionListener(purchaseListener);
					purchaseListener = null;
				}
			}
		}
	}
	
}
