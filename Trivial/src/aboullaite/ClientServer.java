package aboullaite;

import javax.swing.*;

//Class to precise who is connected : Client or Server
public class ClientServer {

	
	public static void main(String [] args){
		
		Object[] selectioValues = { "Server","Client"};
		String initialSection = "Server";
		
		Object selection = JOptionPane.showInputDialog(null, "Entrant com : ", "MyChatApp", JOptionPane.QUESTION_MESSAGE, null, selectioValues, initialSection);
		if(selection.equals("Server")){
                   String[] arguments = new String[] {};
			new MultiThreadChatServerSync();
			MultiThreadChatServerSync.main(arguments);
		}else if(selection.equals("Client")){
			String IPServer = JOptionPane.showInputDialog("Introdueix la IP del servidor.");
                        String[] arguments = new String[] {IPServer};
			new ChatClient();
			ChatClient.main(arguments);
		}
		
	}
}
