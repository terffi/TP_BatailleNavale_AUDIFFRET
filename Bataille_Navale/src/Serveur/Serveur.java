package Serveur;

import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {
	
	public static void main(String[] args) {
		
		try {
			@SuppressWarnings("resource")
			ServerSocket ecoute = new ServerSocket(1234);
			System.out.println("Serveur lancé!");
			int id=1;
			while(true) {
				Socket Joueur1 = ecoute.accept();
				Socket Joueur2 = ecoute.accept();
				new BataillaNavaleThread(id,Joueur1,Joueur2).start();
				id++;
				
			}
		} catch(Exception e) {
			//traitement d'erreur
			e.getMessage();
		}
		
	}

}
