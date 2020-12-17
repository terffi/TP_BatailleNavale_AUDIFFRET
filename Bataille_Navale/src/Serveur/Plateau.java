package Serveur;

public class Plateau {
	
	private int[][] grille = new int[10][10];
	//état des beateaux : nombre de cases non touchées
	public int etatPorteAvion=5;
	private int etatCroiseur=4;
	private int etatSousMarin1=3;
	private int etatSousMarin2=3;
	private int etatTorpilleur=2;
	private int etatFlotte=etatPorteAvion+etatCroiseur+etatSousMarin1+etatSousMarin2+etatTorpilleur;

	public Plateau(String porteAvion, String croiseur, String sousMarin1, String sousMarin2, String torpilleur) {
		/*initialisation de la grille d'un joueur
		les string sont de la forme "A1D" : 1e lettre et chiffre pour la position dans la grille, 2e lettre pour la direction dans laquelle est orienté le bateau
		porte-avion : 5 cases
		croiseur : 4 cases
		sous-marins : 3 cases
		torpilleur : 2 cases
		
		états des cases de la grille :
			- 0 : vide
			- 1 : touché
			- 2 : occuppé par le torpilleur
			- 31 : occupé par le sous-marin 1
			- 32 : occupé par le sous-marin 2
			- 4 : occupé par le croiseur
			- 5 : occupé par le porte-avion
		*/
		super();
		
		//placement des bateaux dans la grille
		placementBateau(porteAvion,5,5);
		placementBateau(croiseur,4,4);
		placementBateau(sousMarin1,3,31);
		placementBateau(sousMarin2,3,32);
		placementBateau(torpilleur,2,2);		
		
	}	
	public Plateau() {
		super();
	}




	private boolean placementBateau(String placement, int taille, int bateau) {
		//méthode plaçant les bateaux sur le plateau, renvoi false si il y'a collision avec un autre bateau
		
		int ligne = placement.charAt(0) - 'A';
		int colonne = placement.charAt(1) - '0';
		
		if(placement.charAt(2)=='D' || placement.charAt(2)=='G') {
			//cas où le bateau est sur une ligne horizontale
			
			if(placement.charAt(2)=='D') {
				for(int i=0 ; i<taille ; i++) {
					if(grille[ligne][colonne + i]!=0) {
						return false;
					}
					grille[ligne][colonne + i]=bateau;
				}
			}
			if(placement.charAt(2)=='G') {
				for(int i=0 ; i<taille ; i++) {
					if(grille[ligne][colonne - i]!=0) {
						return false;
					}
					grille[ligne][colonne - i]=bateau;
				}
			}
		}
		
		if(placement.charAt(2)=='H' || placement.charAt(2)=='B') {
			//cas où le bateau est sur une ligne verticale
			
			if(placement.charAt(2)=='B') {
				for(int i=0 ; i<taille ; i++) {
					if(grille[ligne + i][colonne]!=0) {
						return false;
					}
					grille[ligne + i][colonne]=bateau;
				}
			}
			if(placement.charAt(2)=='H') {
				for(int i=0 ; i<taille ; i++) {
					if(grille[ligne - i][colonne]!=0) {
						return false;
					}
					grille[ligne - i][colonne]=bateau;
				}
			}
		}
		
		return true;
	}	


	private String attaqueCase(String position) {
		/*
		attaque sur une case, le string position est de la forme "A5"
		 */
		
		int ligne = position.charAt(0) - 'A';
		int colonne = position.charAt(1) - '0';
		int etatCase = grille[ligne][colonne];
		
		grille[ligne][colonne]=1;
		
		if(etatCase!=0) {
			//l'attaque touche un bateau
			
			//mise à jour de l'état de la flotte
			etatFlotte-=1;
			
			//vérification si la flotte est encore opérationnel
			if (etatFlotte==0) {
				return "victoire";
			}
			
			//mise à jour de l'état de la flotte par bateau
			switch(etatCase) {
			case 5:
				etatPorteAvion-=1;
				if (etatPorteAvion==0)return "coulé";
				break;
			case 4:
				etatCroiseur-=1;
				if(etatCroiseur==0)return"coulé";
				break;
			case 31:
				etatSousMarin1-=1;
				if(etatSousMarin1==0)return "coulé";
				break;
			case 32:
				etatSousMarin2-=1;
				if(etatSousMarin2==0) return "coulé";
				break;
			case 2:
				etatTorpilleur-=1;
				if(etatTorpilleur==0) return "coulé";
				break;
			default:
				break;
			}
			
			return "touché";
			
		}
		else {
			//l'attaque atterrit dans l'eau
			return "plouf";
		}
	}
	
	

	public String afficherGrille() {
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
					texte+=" x ";
					break;
				case 0 :
					texte+=" ~ ";
					break;
				default :
					texte+=" 1 ";
					break;
				}
				texte+="|";
			}
			texte+="\n";
		}
		return texte;
	}
	
	
	public String placement(String coord, int taille, int bateau) {
		//méthode vérifiant la validité des coordonnées de placement de bateau entrées par le client et positionne le bateau si les coordonnées sont valides
		
		if(coord.length()!=3) {
			return "Veuillez entrer 3 characteres ( exemple : J6H ) : ";
		}
		else {
			int ligne = coord.charAt(0) - 'A';
			int colonne = coord.charAt(1) - '0';
					
			//vérification que les coordonnées sont valides
			if(ligne<0 || ligne>10 || colonne<0 || colonne>10) {
				return "Coordonnées invalides, veuillez réessayer : ";
			}
			else {
				//vérification de la direction
				if(coord.charAt(2)!='H' && coord.charAt(2)!='B' && coord.charAt(2)!='G' && coord.charAt(2)!='D') {
					return "La direction entrée est invalide, veuillez réessayer : ";
				}
				else {
					//vérification que le bateau rentre
					if((coord.charAt(2)=='H' && ligne-(taille-1)<0) || (coord.charAt(2)=='B' && ligne+(taille-1)>9) || (coord.charAt(2)=='G' && colonne-(taille-1)<0) || (coord.charAt(2)=='D' && colonne+(taille-1)>9)) {
						return "Le bateau ne rentre pas dans cette direction (taille : "+taille+"), veuillez réessayer : ";
					}
					else {
						//vérification qu'il n'y a pas de collision avec un autre bateau
						if(!placementBateau(coord,taille,0)) {
							return "Collision avec un autre bateau, veuillez réessayer : ";
						}
						else {
							placementBateau(coord, taille, bateau);
							return "OK";
						}
					}
				}
			}
		}
	}
	
	
	
	public String attaque(String coord) {
		//méthode vérifiant la validité des coordonnées d'attaque et effectue l'attaque si les coordonnées sont valide

		if(coord.length()!=2) {
			return "Veuillez entrer 2 characteres ( exemple : D6 ) : ";
		}
		else {		
			int ligne = coord.charAt(0) - 'A';
			int colonne = coord.charAt(1) - '0';
				
			//vérification que les coordonnées sont valides
			if(ligne<0 || ligne>10 || colonne<0 || colonne>10) {
				return "Coordonnées invalides, veuillez réessayer : ";
			}
			else {
				if(grille[ligne][colonne]==1) {
					return "Vous avez déjà attaqué cette case, veuillez réessayer : ";
				}
				else {
					return attaqueCase(coord);
				}
			}
		}	
		
	}
	
	
	
	

}
