package Serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BataillaNavaleThread extends Thread {
	
	int id;	//id de la partie de bataille navale
	BufferedReader inJ1;
	BufferedReader inJ2;
	PrintWriter outJ1;
	PrintWriter outJ2;
	static int nbid=0;
	
	static boolean jeu;
	Plateau p1;
	Plateau p2;
	int joueur=1;
	
	public BataillaNavaleThread(int id, Socket Joueur1,Socket Joueur2){
		//initialisation du thread
		try {
			this.id=id;
			nbid++;
			inJ1 = new BufferedReader(new InputStreamReader(Joueur1.getInputStream()));
			inJ2 = new BufferedReader(new InputStreamReader(Joueur2.getInputStream()));

			outJ1 = new PrintWriter(Joueur1.getOutputStream(),true);
			outJ2 = new PrintWriter(Joueur2.getOutputStream(),true);
			
			//envoie de l'information qu'un lobby a été trouvé aux clients
			String message="partie trouvée";
			outJ1.println(message);
			outJ2.println(message);
			
			//envoie du numéro de joueur correspondant aux clients
			outJ1.println(1);
			outJ2.println(2);
			
		} catch (Exception e) {}
	}
	
	public void run() {
		try {
			boolean lobby=true;
			while(lobby) {
				//initialisation de la partie
				p1 = new Plateau();
				p2 = new Plateau();
				
				//placement des 5 bateaux
				int taille=0;
				int bateau=0;
				for(int i=0;i<5;i++) {
					
					outJ1.println(p1.afficherGrille());
					outJ2.println(p2.afficherGrille());
					
					switch(i) {
					case 0:
						//placement du porte-avion
						taille=5;
						bateau=5;
						break;
					case 1:
						//placement du croiseur
						taille=4;
						bateau=4;
						break;
					case 2:
						//placement du 1e sous-marin;
						taille=3;
						bateau=31;
						break;
					case 3:
						//placement du 2e sous-marin
						taille=3;
						bateau=32;
						break;
					case 4:
						//placement du torpilleur
						taille=2;
						bateau=2;
						break;
					default:
						break;
					}
					
					//joueur 1
					String placement = inJ1.readLine();//reception des coordonnées de placement
					String message = p1.placement(placement,taille,bateau); //placement du bateau
					outJ1.println(message); //envoi du message d'erreur ou de validation
					while(!message.equals("OK")) {
						placement = inJ1.readLine();
						message = p1.placement(placement,taille,bateau);
						outJ1.println(message); //envoi du message d'erreur ou de validation
					}
					
					//joueur 2
					placement = inJ2.readLine();//reception des coordonnées de placement
					message = p2.placement(placement,taille,bateau); //placement du bateau
					outJ2.println(message); //envoi du message d'erreur ou de validation
					while(!message.equals("OK")) {
						placement = inJ2.readLine();
						message = p2.placement(placement,taille,bateau);
						outJ2.println(message); //envoi du message d'erreur ou de validation
					}
				}
				
				
				jeu = true;
				while(jeu) {
					
					String position;
					String message;
					
					//envoie du tour aux clients
					outJ2.println(joueur); 
					outJ1.println(joueur);
					
					if(joueur==1) {	
						//tour du joueur 1

						position = inJ1.readLine();//reception des coordonnées d'attaque
						message = p2.attaque(position); //attaque
						outJ1.println(message); //envoi du message d'erreur ou de validation
						while(!message.equals("touché") && !message.equals("coulé") && !message.equals("victoire") && !message.equals("plouf")) {
							position = inJ1.readLine();
							message = p2.attaque(position);
							outJ1.println(message); //envoi du message d'erreur ou de validation
						}
						
						
						outJ1.println(message); //envoie du résultat de l'attaque au joueur
						
						outJ2.println(position); //envoie des coordonnées de l'attaque au joueur adverse
						
						//vérification d'une éventuelle victoire
						if(message=="victoire") {
							
							outJ2.println("defaite"); //envoie de l'information au joueur adverse
							
							jeu=false; //fin de la partie
							
						}
						else {
							outJ2.println(message); //envoie du résultat de l'attaque au joueur adverse
							outJ2.println(p2.afficherGrille()); //envoie de la grille mise à jour au joueur adverse
							
							joueur=2; //tour du joueur adverse
						}
						
					}
					else{	
						//tour du joueur 2, similaire au tour du joueur 1
						position = inJ2.readLine();//reception des coordonnées d'attaque
						message = p1.attaque(position); //attaque
						outJ2.println(message); //envoi du message d'erreur ou de validation
						while(!message.equals("touché") && !message.equals("coulé") && !message.equals("victoire") && !message.equals("plouf")) {
							position = inJ2.readLine();
							message = p1.attaque(position);
							outJ2.println(message); //envoi du message d'erreur ou de validation
						}
						
						outJ2.println(message); //envoie du résultat de l'attaque au joueur
						
						outJ1.println(position); //envoie des coordonnées de l'attaque au joueur adverse
						
						//vérification d'une éventuelle victoire
						if(message=="victoire") {
							
							outJ1.println("defaite"); //envoie de l'information au joueur adverse
							
							jeu=false;
							
						}
						else {
							outJ1.println(message); //envoie du résultat de l'attaque au joueur adverse
							outJ1.println(p1.afficherGrille()); //envoie de la grille mise à jour au joueur adverse
							
							joueur=1; //tour du joueur adverse
						}

					}
					
				}
				
				//fin de la partie
				
				outJ1.println("Revanche?");
				outJ2.println("Revanche?");
				
				//réponses des joueurs
				//joueur 1
				String message = inJ1.readLine();
				while(!message.equals("Y") && !message.equals("N")) {
					outJ1.println("Y/N");
					message = inJ1.readLine();
				}
				outJ1.println("OK");
				if(message.equals("N")) {
					lobby=false;
				}
				else {
					//joueur 2
					message = inJ2.readLine();
					while(!message.equals("Y") && !message.equals("N")) {
						outJ2.println("Y/N");
						message = inJ2.readLine();
					}
					outJ2.println("OK");
					if(message.equals("N")) {
						lobby=false;
					}
				}
				
			}
			
		} catch(IOException e) {
			//traitement d'erreur
			e.getMessage();
		}
		
	}
	

}
