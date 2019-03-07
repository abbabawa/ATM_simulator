/*
 * Title: Point of sale simulator
 * Purpose: CS412 assignment (Demonstrating structured programming)
 *  Author: Abba Bawa, member CS412 group 2
 */

package group2_cs412;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.*;

public class Settings {
	//Declaring class variables
	private JTextArea textArea;
	private POSDatabaseClass POSDatabase; 
	private JButton[] settingButtons = new JButton[10];
	private JButton back;
	private SettingsListener settingListener = new SettingsListener();
	
	//Constructor for setting class. Used to initialize some class variables and establish connection to database
	Settings(JTextArea a, JButton[] buttons, JButton back){
		this.back = back;
		back.addActionListener(settingListener);
		
		try{
			POSDatabase = new POSDatabaseClass();
		}catch(ClassNotFoundException ex){
			a.setText("Error connecting to Database.\n Please check database connection.");
		}catch(SQLException ex){
			a.setText("Error connecting to Database.\n Please check database connection.");
		}
		
		if(POSDatabase != null){
			for(int i = 0; i < 10; i++){
				settingButtons[i] = buttons[i];
				settingButtons[i].addActionListener(settingListener);
			}
			this.textArea = a;
			settingButtons = buttons;
			textArea.setText("1. Change password \n 2. About");
		}
	}
	
	//changePassword method to enable a user change his/her password
	public void changePassword(){
		String oldPin = "", newPin="";
		int result, resultNew;
		String message = "";
		
		boolean continueLoop = false;
		int noOfTries = 1;
		
		while(noOfTries <= 3){
			JPasswordField pass = new JPasswordField(5);
			result = JOptionPane.showConfirmDialog(null, pass,message+"Enter your old password", JOptionPane.OK_CANCEL_OPTION);
			if(result == JOptionPane.OK_OPTION){
				oldPin = pass.getText();
			}
			try{
				int pin = Integer.parseInt(oldPin);
				message = "";
				if(POSDatabase.validatePin(pin)){
					int tries = 1;
					while(tries <= 3){
						JPasswordField passNew = new JPasswordField(5);
						resultNew = JOptionPane.showConfirmDialog(null, passNew,message+"Enter your new password", JOptionPane.OK_CANCEL_OPTION);
						if(resultNew == JOptionPane.OK_OPTION){
							newPin = passNew.getText();
						}
						try{
							int acceptPin = Integer.parseInt(newPin);
							POSDatabase.changePin(pin, acceptPin);
							textArea.setText("Pin has successfully been changed. \nPress back to go back to the main menu.");
							tries = 4;
						}catch(NumberFormatException ex){
							message = "Your password has to be a number.";
						}
						
					}
				}
				else{
					throw new NumberFormatException("Pin error");
				}
				noOfTries = 4;
			}catch(NumberFormatException ex){
				message = "Incorrect pin entered. Please enter your correct pin";
				noOfTries++;
			}catch(SQLException ex){
				JOptionPane.showMessageDialog(null, "A problem was encountered while tyring to change your pin. \nPlease check database connection");
			}
		}
		
	}
	
	//about method to display device information
	public void about(){
		textArea.setText("\tPOS system \n Developed by CS 412 group 2. \n\t Members \n 1. Abba Bawa \n 2.Emmy Ajik \n 3. Sonia Amah \n 4. Alice \n 5. Jerry Dawus");
	}
	
	public class SettingsListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()== settingButtons[1]){
				changePassword();
				e.setSource(settingButtons[9]);
			}
			else if(e.getSource() == settingButtons[2]){
				about();
				e.setSource(settingButtons[9]);
			}
			else if(e.getSource() == back){
				for(int i = 0; i < 10; i++){
					settingButtons[i].removeActionListener(settingListener);
					settingListener = null;
				}
			}
		}
		
	}

}
