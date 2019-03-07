/*
 * Title: Point of sale simulator
 *Purpose: CS412 assignment (Demonstrating structured programming)
 *  Author: Abba Bawa, member CS412 group 2
 */

package group2_cs412;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class CS412 extends JFrame{
	//Declaring class variables
	private JTextArea textArea = new JTextArea(8, 25);
	private JButton[] buttons = new JButton[10];
	private HomeMenuListener homeListener;
	private static JButton backButton = new JButton("Back");
	private static JButton power = new JButton("On/Off");
	private int powerValue = 0;
	private Timer timer;
	static CS412 frame;
	
	//Constructor for class CS412. User Interface elements are initialized in the constructor
	public CS412(){
		JPanel keypad = new JPanel();
		keypad.setLayout(new GridLayout(4, 3, 2, 2));
		
		for(int i = 0; i < 10; i++){
			buttons[i] = new JButton(""+i);
		}
		
		homeListener = new HomeMenuListener();
		
		//Registering the listener with the buttons and adding buttons to the user interface(panel called keypad)
		for(int i = 1; i < 10; i++){
			buttons[i].addActionListener(homeListener);
			keypad.add(buttons[i]);
		}
		backButton.addActionListener(homeListener);
		power.addActionListener(homeListener);
		
		keypad.add(power);
		keypad.add(buttons[0]);
		keypad.add(backButton);
		
		//Creating a panel to be used as the screen and adding a text area to it
		JPanel screen = new JPanel();
		screen.add(textArea);
		
		//Code to display start up text and a timer to delay display of home menu while variables are initialized
		textArea.setText("POS Simulator \n Starting up.....");
		textArea.setEditable(false);	//Disable editing ability of Text Area
		timer = new Timer(3000, homeListener);
		timer.start();
		
		add(screen, BorderLayout.NORTH);
		add(keypad, BorderLayout.SOUTH);
		
	}
	
	
	//Creating an inner class to respond to button clicks
	class HomeMenuListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			//Disabling homeMenuListener every time a choice has been made to avoid conflicts with other Listener objects in the other classes 
			for(int i = 0; i < 10; i++){
				buttons[i].removeActionListener(homeListener);
				homeListener = null;
			}
				//Code to create the appropriate transaction object based on the user's choice
				if(e.getSource() == buttons[1]){
					Purchase purchase = new Purchase(textArea, buttons, backButton);
					e.setSource(buttons[9]);
				}
				if(e.getSource() == buttons[2]){
					BillPayment billpayment = new BillPayment(textArea, buttons, backButton);
					e.setSource(buttons[9]);
				}
				if(e.getSource() == buttons[3]){
					VirtualTopUp virtualTopUp = new VirtualTopUp(textArea, buttons, backButton);
					e.setSource(buttons[9]);
				}
				if(e.getSource() == buttons[4]){
					Reports reports = new Reports(textArea, buttons, backButton);
					e.setSource(buttons[9]);
				}
				if(e.getSource() == buttons[5]){
					Settings settings = new Settings(textArea, buttons, backButton);
					e.setSource(buttons[9]);
				}
				if(e.getSource() == backButton){
					backHome();
				}
				if(e.getSource() == power){
					power();
				}
				if(e.getSource() == timer){
					if(powerValue == 0){
						textArea.setText("\tPOS \n 1. Purchase \n 2. Bill payment \n 3. Virtual top up \n 4. Reports \n 5. Settings");
						for(int i = 0; i < 10; i++){
							buttons[i].addActionListener(new HomeMenuListener());
						}
						timer.stop();
						powerValue = 1;
						for(int i = 0; i < 10; i++){
							buttons[i].setEnabled(true);
						}
						backButton.setEnabled(true);
					}else{
						textArea.setText("");
						timer.stop();
						powerValue = 0;
						for(int i = 0; i < 10; i++){
							buttons[i].setEnabled(false);
						}
						backButton.setEnabled(false);
					}
				}
		}
	}
	
	//Power method defining actions to be taken when the On/Off(power) button is clicked
	public void power(){
		//action to be taken if power button is clicked while device is off
		if(powerValue == 0){
			textArea.setText("POS Simulator \n Starting up.....");
			timer = new Timer(3000, new HomeMenuListener());
			timer.start();
		}
		//action to be taken if power button is clicked while device is on
		else{
			textArea.setText("POS \n Shutting down.......");
			timer = new Timer(3000, new HomeMenuListener());
			timer.start();
		}
	}
	
	//backHome method defining action to be taken when back button is clicked 
	public void backHome(){
		textArea.setText("CS 412 ASSIGNMENT \n 1. Purchase \n 2. Bill payment \n 3. Virtual top up \n 4. Reports \n 5. Settings");
		//frame = null;
		//frame = new CS412();
		for(int i = 0; i < 10; i++){
			buttons[i].addActionListener(new HomeMenuListener());
		}
	}
	
	
	//Main method, program execution starts here
	public static void main(String[] args){
		frame = new CS412();
		frame.setTitle("CS 412 GROUP 2 ASSIGNMENT");
		frame.setSize(300, 300);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	
}
