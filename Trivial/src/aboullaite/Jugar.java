package aboullaite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Jugar extends Thread {

	private static final int maxClientsCount = 10;
	public static clientThread[] threads = new clientThread[maxClientsCount];	

	public static ArrayList<clientThread> jugadors = new ArrayList<clientThread>();	
	public static boolean tiemporespuesta = false;
	ArrayList<clientThread> aciertos = new ArrayList<clientThread>();
	ArrayList<clientThread> fallos = new ArrayList<clientThread>();

	static String[] pregunta;	
	int prgn =0;

	public Jugar(clientThread[] threads) {
		Jugar.threads = threads;
	}

	public Jugar() {

	}
	
	public static clientThread[] getJugadors(){
		return threads;
	}

	public void run() {
		if (Thread.currentThread().isDaemon()) {
			while (true) {
				try {
					
					Clip correctum = AudioSystem.getClip();
					Clip errorum = AudioSystem.getClip();
					
					File errorumum = new File("error.wav");
					File correctumum = new File("correcto.wav");
					
					errorum.open(AudioSystem.getAudioInputStream(errorumum));
					correctum.open(AudioSystem.getAudioInputStream(correctumum));
					
					
					jugadors.removeAll(jugadors);
					sleep(6000);
					for (int i = 0; i < maxClientsCount; i++) {
						if (threads[i] != null) {
							threads[i].os.println("El joc començara en 30 segons, escriu '/jugar' per unirte.");
						}
					}
					sleep(30000);
					int x=0;
					for (int i = 0; i < threads.length; i++) {
						if(threads[i] != null){
							x++;
						}
					}
					for (int i = 0; i < x; i++) {
						if (threads[i].getIsplayer() == true) {
							jugadors.add(threads[i]);
							
						}
					}

					for (int i = 0; i < maxClientsCount; i++) {
						if (threads[i] != null) {
							threads[i].os.println("El joc ha començat.");
						}
					}
					
					 
					if(jugadors!=null){
					while(prgn !=5){
						
					
						ObtenerPreguntas();

						for (int i = 0; i<jugadors.size();i++){
						jugadors.get(i).os.println("____________________________________________");
						jugadors.get(i).os.println(pregunta[0]);
						jugadors.get(i).os.println("1) " + pregunta[1]);
						jugadors.get(i).os.println("2) " + pregunta[2]);
						jugadors.get(i).os.println("3) " + pregunta[3]);
						jugadors.get(i).os.println("4) " + pregunta[4]);
						jugadors.get(i).os.println("C- " + pregunta[5]);
						jugadors.get(i).os.println("____________________________________________");
						}
					
						
					tiemporespuesta=true;
					
					sleep(10000);
					
					tiemporespuesta=false;
					
					for (int i = 0; i < x; i++) {
						if (threads[i].getIsplayer() == true) {
							threads[i].setRes(true);
							
						}
					}
					
					
				for (int i = 0; i <jugadors.size(); i++) {
					if(jugadors.get(i).getResposta() != null){
						if(jugadors.get(i).getResposta().equals(pregunta[6])){

							int p =jugadors.get(i).getPuntos();
							p=p+1;
							jugadors.get(i).setPuntos(p);
							aciertos.add(jugadors.get(i));
						}else{
							fallos.add(jugadors.get(i));
						}
						
					}
					else{
						fallos.add(jugadors.get(i));
					}
				}
				
				for (int i = 0; i <jugadors.size(); i++) {
					jugadors.get(i).setResposta("");
				}
				
					for (int i = 0; i <aciertos.size(); i++) {

						correctum.start();
						aciertos.get(i).os.print("Correcte");
						aciertos.get(i).os.println("");
						


					}
					for (int i = 0; i <fallos.size(); i++) {
						
						errorum.start();
						fallos.get(i).os.print("Incorrecte");
						fallos.get(i).os.println("");
						
					}
					
					
					
					for (int i = 0; i <jugadors.size(); i++) {
						jugadors.get(i).os.print("La resposta era: "+pregunta[5]);
						jugadors.get(i).os.println("");
					}
					aciertos.removeAll(aciertos);
					fallos.removeAll(fallos);
					
					prgn++;
					
					sleep(5000);
					}
					}
					

					
                    int[] vec = new int[jugadors.size()];
                    String[] vec2 = new String[jugadors.size()];

                    for (int i = 0; i < jugadors.size(); i++) {
                            jugadors.get(i).os.println("Partida finalitzada.");
                            vec[i] = jugadors.get(i).getPuntos();
                            vec2[i] = jugadors.get(i).name;
                    }

					int aux;
                    String aux2;
                    for (int i = 0; i < vec.length - 1; i++)
                            for (int j = 0; j < vec.length - i - 1; j++)
                                    if (vec[j + 1] > vec[j]) {
                                            aux = vec[j + 1];
                                            aux2 = vec2[j + 1];
                                            vec[j + 1] = vec[j];
                                            vec2[j + 1] = vec2[j];
                                            vec[j] = aux;
                                            vec2[j] = aux2;
                                    }

                    
                    for (int i = 0; i < jugadors.size(); i++) {                    	
                    	int woto = 1;
                    	
                            for (int z = 0; z < jugadors.size(); z++) {
                                    jugadors.get(i).os.println(woto + ") Jugador: " + vec2[z] + " | Puntuació: " + vec[z] + ".");
                                    woto++;
                            }

                    }
					
					
					for (int i = 0; i < maxClientsCount; i++) {
						if (threads[i] != null) {
							threads[i].setIsplayer(false);
						}
					}
					
					prgn = 0;
					for (int i = 0; i < jugadors.size(); i++) {
		               jugadors.get(i).setPuntos(0);
		                
					}

					sleep(60000);

				} catch (InterruptedException | LineUnavailableException | IOException | UnsupportedAudioFileException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
			


		}
	}

	public boolean isTiemporespuesta() {
		return tiemporespuesta;
	}
	
    public static void ObtenerPreguntas(){

    	Scanner sc = new Scanner(System.in);
    	String arxiuCSV = "preguntas.csv";
    	BufferedReader br = null;
    	String linia = "";
    	String separador = ";";
    	int index = 0; 

    	List<String> linies = new ArrayList<String>();

    	try {
    	br = new BufferedReader(new FileReader(arxiuCSV));
    	while ((linia = br.readLine()) != null) {

    		linies.add(linia);
    	}


    	index = ThreadLocalRandom.current().nextInt(0, linies.size() + 1);

    	String preguntas =  linies.get(index);

    	pregunta = preguntas.split(separador);


    	} catch (FileNotFoundException e) {
    	e.printStackTrace();
    	} catch (IOException e) {
    	e.printStackTrace();
    	} finally {
    	if (br != null) {
    	try {
    	br.close();
    	sc.close();
    	} catch (IOException e) {
    	e.printStackTrace();
    	}
    	}
    	}

    	}


}
