package aboullaite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class ChatClient {

    static class ChatAccess extends Observable {
        private Socket socket;
        private OutputStream outputStream;

        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
        }

        public void InitSocket(String server, int port) throws IOException {
            socket = new Socket(server, port);
            outputStream = socket.getOutputStream();

            Thread receivingThread = new Thread() {
                @Override
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null)
                            notifyObservers(line);
                    } catch (IOException ex) {
                        notifyObservers(ex);
                    }
                }
            };
            receivingThread.start();
        }

        private static final String CRLF = "\r\n";

        public void send(String text) {
            try {
                outputStream.write((text + CRLF).getBytes());
                outputStream.flush();
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }

        public void close() {
            try {
                socket.close();
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }
    }

    static class ChatFrame extends JFrame implements Observer {

		private static final long serialVersionUID = 1L;
		private JTextArea textArea;
        private JTextField inputTextField;
        private JButton sendButton;
        private ChatAccess chatAccess;

        public ChatFrame(ChatAccess chatAccess) {
            this.chatAccess = chatAccess;
            chatAccess.addObserver(this);
            buildGUI();
        }

        private void buildGUI() {
            textArea = new JTextArea(20, 50);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setBackground(new Color(252, 164, 250));
            textArea.setForeground(Color.BLACK);
            add(new JScrollPane(textArea), BorderLayout.CENTER);

            Box box = Box.createHorizontalBox();
            add(box, BorderLayout.SOUTH);
            inputTextField = new JTextField();
            
            sendButton = new JButton("Send");
            sendButton.setBackground(new Color(165, 23, 163));
            sendButton.setForeground(Color.WHITE);
            box.add(inputTextField);
            box.add(sendButton);

            ActionListener sendListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String str = inputTextField.getText();
                    if (str != null && str.trim().length() > 0)
                        chatAccess.send(str);
                    inputTextField.selectAll();
                    inputTextField.requestFocus();
                    inputTextField.setText("");
                }
            };
            inputTextField.addActionListener(sendListener);
            sendButton.addActionListener(sendListener);

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    chatAccess.close();
                }
            });
            
            this.addWindowListener( new WindowAdapter() {
                public void windowOpened( WindowEvent e ){
                    inputTextField.requestFocus();
                }
            }); 
        }

        public void update(Observable o, Object arg) {
            final Object finalArg = arg;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    textArea.append(finalArg.toString());
                    textArea.append("\n");
                }
            });
        }
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
        String server = args[0];
        int port =2222;
        ChatAccess access = new ChatAccess();

        JFrame frame = new ChatFrame(access);
        frame.setTitle("Trivial - connectat a [" + server + ":" + port + "]");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        
        JPanel panel = new JPanel();        
        panel.setBackground(new Color(234, 129, 232));
        panel.setForeground(Color.BLACK);
        
        frame.getContentPane().add(panel, BorderLayout.EAST);
        
        JList<String> list = new JList<String>();
        list.setModel(new AbstractListModel() {

			private static final long serialVersionUID = 1L;
			String[] values = new String[] {"0 Usuaris en línia"};
        	public int getSize() {
        		return values.length;
        	}
        	public Object getElementAt(int index) {
        		return values[index];
        	}
        });
        
        
        list.setBackground(new Color(234, 129, 232));
        list.setForeground(Color.BLACK);
        
        DefaultListModel<String> m = new DefaultListModel<String>();
       
        
        JButton refr = new JButton("Refrescar");
        refr.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		
        		clientThread[] jj = Jugar.getJugadors();
        		
                for(int i=0; i<jj.length; i++){
                	
                	//String nom = jj[i].getNombre();
                	
                	System.out.println(jj[i]);
                	System.out.println("pepe");
                	
                	//m.addElement(jj[i]);
                }
                
                list.setModel(m);
        		
        	}
        });
        
        refr.setEnabled(false);
        
        
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
        	gl_panel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_panel.createSequentialGroup()
        			.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
        				.addGroup(gl_panel.createSequentialGroup()
        					.addGap(7)
        					.addComponent(list))
        				.addGroup(gl_panel.createSequentialGroup()
        					.addContainerGap()
        					.addComponent(refr)))
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_panel.setVerticalGroup(
        	gl_panel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_panel.createSequentialGroup()
        			.addGap(16)
        			.addComponent(list)
        			.addPreferredGap(ComponentPlacement.RELATED, 307, Short.MAX_VALUE)
        			.addComponent(refr)
        			.addGap(20))
        );
        panel.setLayout(gl_panel);
        frame.setVisible(true);

        try {
            access.InitSocket(server,port);
        } catch (IOException ex) {
            System.out.println("No es pot conectar a [" + server + ":" + port + "]");
            ex.printStackTrace();
            System.exit(0);
        }
    }
}