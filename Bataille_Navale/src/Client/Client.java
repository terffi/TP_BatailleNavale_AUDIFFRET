package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {	
	
	public static void main(String[] args) {
		
		try {
			Socket s = new Socket("localhost",1234);
			PrintWriter out = new PrintWriter(s.getOutputStream(),true);
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));	
			System.out.println("Connexion r�ussie!");	
			
			//recherche d'un autre joueur
			while (!in.readLine().equals("partie trouv�e")){};
			
			String idJoueur = in.readLine(); //le serveur envoie l'id du joueur une fois la partie trouv�e
			
			System.out.println("partie trouv�e ! Vous �tes le joueur "+idJoueur);
			
			Scanner sc = new Scanner(System.in);
			
			boolean lobby = true; 
			while (lobby) {
				
				//cr�ation d'une grille d'attaque vierge
				int[][] grille = new int[10][10];
				
				int ligne=0;
				int colonne=0;
				
				
				//placement des bateaux
				System.out.println("Pour placer vos bateaux, entrez la coordonn�e de la premi�re case suivie de H (haut), B (bas), D (droite), G (gauche) pour la direction (Exemple : F3D) ");
				
				//5 bateaux � placer
				for(int i=0;i<5;i++) {
					
					for(int j=0; j<=12;j++) {
						System.out.println(in.readLine()); //reception et affichage du plateau du client
					}
					
					switch(i) {
					case 0:
						System.out.println("O� voulez vous placer le porte Avion et dans quelle direction ? ");
						break;
					case 1:
						System.out.println("O� voulez vous placer le croiseur et dans quelle direction ? ");
						break;
					case 2:
						System.out.println("O� voulez vous placer le 1e sous-marin et dans quelle direction ? ");
						break;
					case 3:
						System.out.println("O� voulez vous placer le 2e sous-marin et dans quelle direction ? ");
						break;
					case 4:
						System.out.println("O� voulez vous placer le torpilleur et dans quelle direction ? ");
						break;
					default:
						break;
					}
					
					String coord=sc.nextLine();
					out.println(coord); //envoie des coordonn�es au serveur
					String messageServeur = in.readLine(); //r�ception de la r�ponse du serveur
					
					//validation du serveur
					while(!messageServeur.equals("OK")) {
						System.out.println(messageServeur);
						coord=sc.nextLine();
						out.println(coord); //envoie des coordonn�es au serveur
						messageServeur = in.readLine(); //r�ception de la r�ponse du serveur
					}
				}
				
				
				boolean jeu = true;
				while(jeu) {

					//tour du client
					if(in.readLine().equals(idJoueur)) {
						System.out.println("C'est votre tour !\no� souhaitez-vous attaquer ?");
						System.out.println(affichePlateau(grille));
						String coord = sc.nextLine();
						
						out.println(coord); //envoie des coordonn�es au serveur
						String messageServeur = in.readLine(); //r�ception de la r�ponse du serveur
						
						//validation du serveur
						
						while(!messageServeur.equals("touch�") && !messageServeur.equals("coul�") && !messageServeur.equals("victoire") && !messageServeur.equals("plouf")) {
							System.out.println(messageServeur);
							coord=sc.nextLine();
							out.println(coord); //envoie des coordonn�es au serveur
							messageServeur = in.readLine(); //r�ception de la r�ponse du serveur
						}
						
						
						messageServeur = in.readLine(); //reception de la r�ponse du serveur
						
						System.out.println(messageServeur);
						
						//v�rification d'une �ventuelle victoire
						if(!messageServeur.equals("victoire")) {
							ligne = coord.charAt(0) - 'A';
							colonne = coord.charAt(1) - '0';
							
							//mise � jour de la grille d'attaque
							if(messageServeur.equals("touch�") || messageServeur.equals("coul�")) {
								grille[ligne][colonne]=2;
							}
							else {
								grille[ligne][colonne]=1;
							}
						}
						else {
							//fin de la partie
							jeu=false;
						}
					}
					else {
						//tour du joueur adverse
						System.out.println("Tour du joueur adverse");
						
						String messageServeur = in.readLine();
						
						String coord = messageServeur; //reception des coordonn�es de l'attaque
						String attaque = in.readLine(); //reception du resultat de l'attaque
						
						System.out.println("L'adversaire attaque en "+coord);
						System.out.println(attaque);
						
						//verification d'une �ventuelle d�faite
						if(!attaque.equals("defaite")) {
							
							System.out.println("Votre plateau :");
							
							for(int i=0; i<=12;i++) {
								System.out.println(in.readLine()); //reception et affichage du plateau du client
							}
						}
						else {
							//fin de la partie
							jeu=false;
						}
					}
				}
				//fin de la partie
				String messageServeur=in.readLine(); //r�ception du message de serveur
				
				String reponse="";
				
				while(!messageServeur.equals("OK")) {
					System.out.println(messageServeur);
					reponse=sc.nextLine();
					out.println(reponse); //envoi de la r�ponse du joueur
					messageServeur=in.readLine();
				}
				if(reponse.equals("N")) {
					lobby=false;
				}
				
			}
			sc.close();
			s.close();
		} catch(Exception e) {
			//Traitement d'erreur
			e.getMessage();
		}
		
	}
	
	
	public static String affichePlateau(int[][] grille) {
		//m�thode affichant la grille r�sultante des attaques du client
		//grille est un tableau de 10x10
		
		String texte = "  || ";
		for(int i=0;i<10;i++) {
			texte+=" "+i+" |";
		}
		texte+="\n";
		texte+="=============================================";
		texte+="\n";
		for(int i = 0 ; i < 10 ; i++) {
			
			texte+=(char)('A'+i)+" || ";
			
			for(int j = 0 ; j < 10 ; j++) {
				switch(grille[i][j]) {
				case 1 :
					texte+=" * ";
					break;
				case 2 :
					texte+=" x ";
					break;
				default :
					texte+=" ~ ";
					break;
				}
				texte+="|";
			}
			texte+="\n";
		}
		return texte;
	}	
	
}
