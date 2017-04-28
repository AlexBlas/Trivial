
package aboullaite;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class clientThread extends Thread {
	private boolean isplayer = false;
	private String clientName = null;
	private DataInputStream is = null;
	PrintStream os = null;
	private Socket clientSocket = null;
	private final clientThread[] threads;
	private int maxClientsCount;
	public boolean res = true;
	String name;
	 String resposta;
	 public void setRes(Boolean res){
		 this.res = res;
	 }
	 
	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	private int puntos;

	public int getPuntos() {
		return puntos;
	}

	public void setPuntos(int puntos) {
		this.puntos = puntos;
	}

	public String getResposta() {
		return resposta;
	}

	public void setIsplayer(boolean isplayer) {
		this.isplayer = isplayer;
	}

	public clientThread(Socket clientSocket, clientThread[] threads) {
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClientsCount = threads.length;
	}

	public boolean getIsplayer() {
		return isplayer;

	}

	public String getNombre(){
		return name;
	}
	


	@SuppressWarnings("deprecation")
	public void run() {
		int maxClientsCount = this.maxClientsCount;
		clientThread[] threads = this.threads;

		try {

			is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
			
			while (true) {
				os.println("Introdueix el teu nom.");
				name = is.readLine().trim();
				if (name.indexOf('@') == -1) {
					break;
				} else {
					os.println("El nom no pot tenir una '@'.");
				}
			}

			String comm = "/jugar - Unirte a una partida que està apunt de comencar. \n"
					+ "/manual - accedir a aquest manual. \n"
					+ "@[nom_usuari] 'missatge' - Enviar un missatge privat a un usuari concret. \n"
					+ "/sortir - abandona la sala de chat.";

			os.println("Benvingut al chat, " + name + " "
					+ "\n- Per accedir al manual, escriu '/manual' al chat. -");
			
			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] == this) {
						clientName = "@" + name;
						break;
					}
				}
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] != this) {
						threads[i].os.println("--> Un usuari [" + name + "] ha entrat a la sala");
					}
				}
			}

			while (true) {
				String line = is.readLine();

				if (line.equals("/sortir")) {
					break;
				}


				
				if(Jugar.tiemporespuesta && this.isplayer && res == true){
					String resposta = line;
					setResposta(line);
					this.os.println("Resposta: "+resposta);
					res = false;
				}else{


				if (line.startsWith("@")) {
					String[] words = line.split("\\s", 2);
					if (words.length > 1 && words[1] != null) {
						words[1] = words[1].trim();
						if (!words[1].isEmpty()) {
							synchronized (this) {
								for (int i = 0; i < maxClientsCount; i++) {
									if (threads[i] != null && threads[i] != this && threads[i].clientName != null
											&& threads[i].clientName.equals(words[0])) {
										threads[i].os.println("<(PRIVAT)" + name + "> " + words[1]);
										this.os.println(">" + name + "> (Missatge privat per " + threads[i].clientName + "): "
												+ words[1]);
										break;
									}
								}
							}
						}
					}
					 
					 if(line.startsWith(Jugar.pregunta[4]) || line.startsWith(Jugar.pregunta[4].toLowerCase())){
			                synchronized (this) {
			                for (int i = 0; i < maxClientsCount; i++) {
			                  if (threads[i] != null && threads[i].name != null) {
			                          threads[i].os.println("L'usuari "+threads[i].clientName+" ha encertat la pregunta.");
			                  }
			                }
			              }
			        }
					
				} else {

					synchronized (this) {
						for (int i = 0; i < maxClientsCount; i++) {
							if (threads[i] != null && threads[i].clientName != null) {
								threads[i].os.println("<" + name + "> " + line);
							}
						}
					}
				}
				if (line.equals("/jugar")){
					if (!isplayer) {
						isplayer = true;
						os.println("--> " + name + " s'ha unit a la partida.");
					} else {
						isplayer = false;
						os.println("--> " + name + " ha deixat la partida.");
					}

				}
				if(line.equals("/manual")){
					os.println(comm);
				}

				}
			}
			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
						threads[i].os.println("--> L'usuari [" + name + "] abandona el chat.");
					}
				}
			}
			os.println("--> Adeu " + name + ".");

			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] == this) {
						threads[i] = null;
					}
				}
			}

			is.close();
			os.close();
			clientSocket.close();
		} catch (IOException e) {
		}
		
	}
	
}

