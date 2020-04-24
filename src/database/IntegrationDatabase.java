package database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import oracle.jdbc.OracleTypes;

public class IntegrationDatabase
{	
	private Connection connectDB;
	private String username;
	private String password;
	
	public IntegrationDatabase(String username, String password)
	{
		this.username = username;
		this.password = password;
		
		setConnection();
	}
	
	private void setConnection()
	{
		try 
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connectDB = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", username, password);		
			
			checkPermissions();
			
			JOptionPane.showMessageDialog(null, "Step 1 Complete.");
		}
		catch(ClassNotFoundException cExp)
		{
			System.out.println("Unable to find driver");
		}
		catch(SQLException sExp)
		{
			JOptionPane.showMessageDialog(null, "Invalid Username/Password");
		}
	}
	
	private void checkPermissions()
	{
		try
		{
			CallableStatement cstmt = connectDB.prepareCall("{? = call func_A2_permission}");
			cstmt.registerOutParameter(1, OracleTypes.VARCHAR);
			cstmt.execute();
			
			String str = cstmt.getString(1);
			
			if(str.equals("Y"))
			{
				
			}
			else
			{
				throw new SQLException();
			}
		}
		catch (SQLException sExp)
		{
			JOptionPane.showMessageDialog(null, "User does have the required permissions.\nUser has been disconnected.");
			closeConnection();
		} 
	}
	
	public void importData(String inputfile, String controlfile)
	{
		try
		{
			File file = new File(controlfile);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));

			bw.write("LOAD DATA\n");
			bw.write("INFILE '" + inputfile +"'\n");
			bw.write("REPLACE\n");
			bw.write("INTO TABLE payroll_load\n"); 
			bw.write("FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '\"'\n");
			bw.write("TRAILING NULLCOLS (payroll_date DATE \"Month dd, yyyy\",  employee_id,  amount,  status)\n");
			bw.close();
			
			
			String [] arr = controlfile.split("\\\\");
			String logfile = "";

			for(int i = 0; i < arr.length - 1; i++)
			{
				logfile += arr[i] + "\\";
			}
			
			logfile += "logfile.log";

			
			String cmd = "sqlldr userid=" + username + "/" + password + " control=" + controlfile + " log=" + logfile;

			Runtime rt = Runtime.getRuntime();  
			Process proc = rt.exec(cmd);  
			int exitValue = proc.waitFor();

			if(exitValue != 0)
			{
				throw new InterruptedException();
			}
			
			JOptionPane.showMessageDialog(null, "Step 2 Complete.");
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(null, "An error occured. Please try again.");
		}
		catch (InterruptedException e)
		{
			JOptionPane.showMessageDialog(null, "An error occured. Please try again.");
		}
		catch(NullPointerException e)
		{
			JOptionPane.showMessageDialog(null, "An error occured. Please try again.");
		}
	}
	
	public void exportData(String path, String fileName, String alias)
	{
		try
		{
			alias = alias.toUpperCase();
			
			Statement stmt = connectDB.createStatement();
			String create = "CREATE OR REPLACE DIRECTORY " + alias + " AS '" + path + "'";
			stmt.executeUpdate(create);
			
			
			CallableStatement cstmt = connectDB.prepareCall("{call proc_exportCSV(?, ?)} ");
			cstmt.setString(1, alias); 
			cstmt.setString(2, fileName);
			cstmt.execute();
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	public void updateAccount()
	{	
		try
		{	
			CallableStatement cstmt = connectDB.prepareCall("{call proc_zero_temp_accs()} ");
			cstmt.execute();
			
			JOptionPane.showMessageDialog(null, "Step 3 Complete.");
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void closeConnection()
	{
		try 
		{
			connectDB.close();
		} 
		catch(SQLException e) 
		{
			
		}
	}
}
