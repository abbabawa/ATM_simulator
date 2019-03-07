/*
 *Title: Point of sale simulator
 *Purpose: CS412 assignment (Demonstrating structured programming)
 *Author: Abba Bawa, member CS412 group 2
 */

package group2_cs412;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.*;
import java.text.*;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;

import javax.swing.JTextArea;

public class POSDatabaseClass {
	//Class variables
	private Connection connection;
	private JFrame reportsDisplay;
	
	//constructor
	public POSDatabaseClass() throws SQLException, ClassNotFoundException{
		//Any time an object of this class is created, a connection to the database is established
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos", "root", "");
	}
	
	//Method to check if a pin is valid(i.e. stored in the database)
	public boolean validatePin(int pin) throws SQLException{
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM account WHERE pin = '"+pin+"'");
		
		if(resultSet.next()){
			return true;
		}
		else{
			return false;
		}
	}
	
	//Method to save details of a purchase transaction in the database
	public boolean savePurchase(String purpose, double amount, String date, String time)throws SQLException{
		try{
			String query = "INSERT INTO purchase(purpose, amount, date, time) VALUES (?, ?, ?, ?)";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			
			preparedStatement.setString(1, purpose);
			preparedStatement.setDouble(2, amount);
			preparedStatement.setString(3, date);
			preparedStatement.setString(4, time);
			
			preparedStatement.executeUpdate();
			
			return true;
		}catch(SQLException ex){
			return false;
		}
	}
	
	//Method to save details of a bill payment transaction in the database
	public boolean saveBillPayment(String bill_type, double amount, String date, String time)throws SQLException{
		try{
			String query = "INSERT INTO bill_payment(bill_type, amount, date, time) VALUES (?, ?, ?, ?)";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			
			preparedStatement.setString(1, bill_type);
			preparedStatement.setDouble(2, amount);
			preparedStatement.setString(3, date);
			preparedStatement.setString(4,time);
			
			preparedStatement.executeUpdate();
			
			return true;
		}catch(SQLException ex){
			return false;
		}
	}
	
	//Method to save details of a virtual top up transaction in the database
	public boolean saveVTU(String service_provider, double amount, String date, String time, String phone_number)throws SQLException{
		try{
			String query = "INSERT INTO vtu(service_provider, amount, date, time, phone_number) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			
			preparedStatement.setString(1, service_provider);
			preparedStatement.setDouble(2, amount);
			preparedStatement.setString(3, date);
			preparedStatement.setString(4,time);
			preparedStatement.setString(5, phone_number);
			
			preparedStatement.executeUpdate();
			
			return true;
		}catch(SQLException ex){
			return false;
		}
	}
	
	//Method to print details of the last purchase transaction entered into the database
	public void rePrint(JTextArea textArea) throws SQLException{
		try{
			String query = "SELECT * FROM purchase ORDER BY date DESC";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			resultSet.next();
			textArea.setText("Transaction type: "+resultSet.getString(2) +"\nDate: "+resultSet.getString(4));
			
		}catch(SQLException ex){
			
		}
	}
	
	//Method to print details of transactions which occurred from the passed in date till the current date
	public void reports(String date) throws SQLException{
		reportsDisplay = new JFrame();
		JPanel panel = new JPanel();
		JTextArea displayText = new JTextArea();
		JScrollPane scrollpane = new JScrollPane(displayText);
		scrollpane.setPreferredSize(new Dimension(200, 300));
		displayText.setWrapStyleWord(true);
		displayText.setLineWrap(true);
		
		panel.add(scrollpane, BorderLayout.EAST);
		reportsDisplay.add(panel);
		String purchaseQuery = "SELECT * FROM purchase WHERE date >= '"+date+"'";
		String billQuery = "SELECT * FROM bill_payment WHERE date >= '"+date+"'";
		String vtuQuery = "SELECT * FROM vtu WHERE date >= '"+date+"'";
		
		PreparedStatement purchaseStatement = connection.prepareStatement(purchaseQuery);
		PreparedStatement billStatement = connection.prepareStatement(billQuery);
		PreparedStatement vtuStatement = connection.prepareStatement(vtuQuery);
		
		ResultSet purchaseResultSet = purchaseStatement.executeQuery();
		ResultSet billResultSet = billStatement.executeQuery();
		ResultSet vtuResultSet = vtuStatement.executeQuery();
		
		displayText.append("Reports from: "+date+"\n");
		
		while (purchaseResultSet.next())
			displayText.append("\n Transaction type: "+purchaseResultSet.getString(2)+"\n Transaction date: "+purchaseResultSet.getString(4)+"\nAmount: "+purchaseResultSet.getString(3)+"\n");
		while (billResultSet.next())
			displayText.append("\n Transaction type: "+billResultSet.getString(2)+"\n Transaction date: "+billResultSet.getString(4)+"\nAmount: "+billResultSet.getString(3)+"\n");
		while (vtuResultSet.next())
			displayText.append("\n Transaction type: "+vtuResultSet.getString(2)+"\n Transaction date: "+vtuResultSet.getString(4)+"\nAmount: "+vtuResultSet.getString(3)+"\n");
		
		displayText.setEditable(false);
		reportsDisplay.setTitle("reports from "+date);
		reportsDisplay.setSize(500, 500);
		reportsDisplay.pack();
		reportsDisplay.setLocationRelativeTo(null);
		reportsDisplay.setVisible(true);
	}
	
	//Method to enable users change their account pin
	public void changePin(int oldPin, int newPin) throws SQLException{
		String query = "UPDATE account SET pin = ? WHERE pin = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		
		preparedStatement.setInt(1, newPin);
		preparedStatement.setInt(2, oldPin);
		
		preparedStatement.executeUpdate();
		
	}
	
	//Method to get the current date, which is essential when saving transactions in the database 
	public String getDate(){
		Date date = new Date();
		
		SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
		return (ft.format(date));
		
	}
	
	//An overloaded getDate method which accepts a number of days. Used to print reports
	public String getDate(int days){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -days);
		Date neededDate = cal.getTime();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
		System.out.print(neededDate);
		return ft.format(neededDate);
	}
	
	//Method to get the current system time.
	public String getTime(){
		Date time = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
		return (ft.format(time));
	}

}
