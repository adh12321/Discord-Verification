import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.PrivateChannel;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.security.auth.login.LoginException;

import java.sql.*;
import java.util.List;

public class DiscordVerification extends ListenerAdapter {
    
    private static String [] arr = null;
	int numb = 1;
	
	public static void main(String[] args) {
        try {
            new JDABuilder()
                    .setEmail("treasurer@egg.org.au")
                    .setPassword("3ggp4ss")
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
	public void onReady(ReadyEvent event) {
		//event.getJDA().getAccountManager().setUsername("EggBot");
		//event.getJDA().getAccountManager().update();
		event.getJDA().getAccountManager().setGame("self coding");
		event.getJDA().getAccountManager().setIdle(false);;
	}
	
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
		String msg = event.getMessage().getContent();
    	PrivateChannel pchannel = event.getChannel();
    	try {
    		arr = msg.split(" ", 3);
    		if (arr[0].contains("/verify")){
    			System.out.println("Has /verify");
        		if (arr[1].toLowerCase().trim().equals("help") || arr[1].trim().equals("?"))
        		{
        			System.out.println("Halp");
        			help(pchannel);
        		} else if (arr.length == 3)
        		{
        			sendVerification(pchannel);
        		} else if (arr.length == 2)
        		{
        			verify(pchannel);
        		} else
        		{
        			help(pchannel);
        		}
    		}
    	}catch (ArrayIndexOutOfBoundsException e) {
    		help(pchannel);
    	}
	}
	
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) { //Channel ID: TC:general(142494630842204160)	Channel ID: TC:test(169378377944399872)
    	TextChannel tchannel = event.getChannel();
    	String schannel = tchannel.toString();
    	String pubquery = event.getMessage().getContent();
    	if (schannel.trim().equals("TC:test(169378377944399872)")) {
    		System.out.println("valid channel");
    		if (pubquery.toLowerCase().trim().equals("/verify")) {
    			checkstatus(event, tchannel);
    		}
    	}
    	
    }
    
    private void checkstatus(GuildMessageReceivedEvent event, TextChannel tchannel) { //R:Verified(151237758457741312)
    	User player = event.getAuthor();
    	List<Role> roles = event.getGuild().getRolesForUser(player);
    	boolean verified = false;
    	int x = 0;
    	Role[] rolesarray = new Role[roles.size()];
    	rolesarray = roles.toArray(rolesarray);
    	while(x < roles.size()) {
    		if(rolesarray[x].toString().trim().equals("R:Verified(151237758457741312)")) {
    			verified = true;
    		}
    		x = x + 1;
    	}
    	if(verified) {
    		tchannel.sendMessage("You are already verified you noob. Jeesh");
    	} else {
    		help(event.getAuthor().getPrivateChannel());
    	}
    }
    
    private void help(PrivateChannel pchan) {
    	pchan.sendMessage("To send a verification email please use /verify \"CardID\" \"Email\" eg. /verify 0008 egg.member@gmail.com\nTo send verification code please use /verify \"code\"");
    }
    
    private void sendVerification(PrivateChannel pchan) {
    	//check if card id not assigned to discord. If free, generate code, send email.
    	//If already verified, message saying card verified
    	if (arr[1].matches("[0-9]+") && arr[1].length() == 4) {
    		System.out.println("CardID Valid");
    		if (isValidEmailAddress(arr[2])) {
    			System.out.println("Email Valid");
    			String findusr = "SELECT * FROM members WHERE ID = " + arr[1];
    			query(findusr, pchan);
    			System.out.println(createHash("str"));
   		} else {
    			pchan.sendMessage("Invalid Email Address");
    			help(pchan);
    		}
    	} else {
    		pchan.sendMessage("Invalid CardID");
    		help(pchan);
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
    
    private void verify(PrivateChannel pchan) {
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

    private void query(String qry, PrivateChannel pchan) {
    	try{
            Connection myConn = DriverManager.getConnection("jdbc:mysql://138.25.111.207:3306/egg", "Terra", "3ggd4t4p4ss");
            PreparedStatement myStmt = myConn.prepareStatement(qry);
            ResultSet myRs = myStmt.executeQuery(qry);
            while (myRs.next()) {
            	System.out.println(myRs.getString("Surname") + ", " + myRs.getString("first_name"));
            	pchan.sendMessage(myRs.getString("Surname") + ", " + myRs.getString("first_name"));
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
}
