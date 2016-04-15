import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.security.auth.login.LoginException;

import java.sql.*;

public class DiscordVerification extends ListenerAdapter {
    
	private static TextChannel tchannel = null;
    private static String [] arr = null;
	int numb = 1;
	
	public static void main(String[] args) {
        try {
            new JDABuilder()
                    .setEmail("")
                    .setPassword("")
                    .addListener(new DiscordVerification())
                    .buildBlocking();
        }
        catch (IllegalArgumentException e) {
            System.out.println("The config was not populated. Please enter an email and password.");
        }
        catch (LoginException e) {
            System.out.println("The provided email / password combination was incorrect. Please provide valid details.");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
    	String msg = event.getMessage().getContent();
    	tchannel = event.getTextChannel();
    	try {
    		arr = msg.split(" ", 3);
    		if (arr[0].contains("/verify")){
    			System.out.println("Has /verify");
        		if (arr[1].toLowerCase().trim().equals("help") || arr[1].trim().equals("?"))
        		{
        			System.out.println("Halp");
        			help();
        		} else if (arr.length == 3)
        		{
        			sendVerification();
        		} else if (arr.length == 2)
        		{
        			verify();
        		} else
        		{
        			help();
        		}
    		}
    	}catch (ArrayIndexOutOfBoundsException e) {
    		help();
    	}
    }
    
    private void help() {
    	tchannel.sendMessage("To send a verification email please use /verify \"CardID\" \"Email\" eg. /verify 0008 egg.member@gmail.com\nTo send verification code please use /verify \"code\"");
    }
    
    private void sendVerification() {
    	//check if card id not assigned to discord. If free, generate code, send email.
    	//If already verified, message saying card verified
    	if (arr[1].matches("[0-9]+") && arr[1].length() == 4) {
    		System.out.println("CardID Valid");
    		if (isValidEmailAddress(arr[2])) {
    			System.out.println("Email Valid");
    			//String stmt = "SELECT * FROM members WHERE ID = ?";
    			String findusr = "SELECT * FROM members WHERE ID = " + arr[1];
    			query(findusr);
    			System.out.println(createHash("str"));
   		} else {
    			tchannel.sendMessage("Invalid Email Address");
    			help();
    		}
    	} else {
    		tchannel.sendMessage("Invalid CardID");
    		help();
    	}
    }
    
    private int createHash(String str)
    {
    	int hash = 7;
    	for (int i = 0; i < str.length(); i++) {
    	    hash = hash*31 + str.charAt(i);
    	}
    	return hash;
    }
    
    private void verify() {
    	//Check code stored in database/some file. If match, tick verified box or add code to database.
    	//If not, verification failed. Please ensure that your code matches
    }
    
    private static boolean isValidEmailAddress(String email) {
    	boolean result = true;
    	try {
    		InternetAddress emailAddr = new InternetAddress(email);
    		emailAddr.validate();
    	} catch (AddressException ex) {
    		result = false;
    	}
    	return result;
    }

    private void query(String qry) {
    	try{
            Connection myConn = DriverManager.getConnection("", "", "");
            PreparedStatement myStmt = myConn.prepareStatement(qry);
            ResultSet myRs = myStmt.executeQuery(qry);
            while (myRs.next()) {
            	System.out.println(myRs.getString("Surname") + ", " + myRs.getString("first_name"));
            	tchannel.sendMessage(myRs.getString("Surname") + ", " + myRs.getString("first_name"));
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
}
