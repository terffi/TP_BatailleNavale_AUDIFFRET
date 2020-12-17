package Serveur;

public class Plateau {
	
	private int[][] grille = new int[10][10];
	//�tat des beateaux : nombre de cases non touch�es
	public int etatPorteAvion=5;
	private int etatCroiseur=4;
	private int etatSousMarin1=3;
	private int etatSousMarin2=3;
	private int etatTorpilleur=2;
	private int etatFlotte=etatPorteAvion+etatCroiseur+etatSousMarin1+etatSousMarin2+etatTorpilleur;

	public Plateau(String porteAvion, String croiseur, String sousMarin1, String sousMarin2, String torpilleur) {
		/*initialisation de la grille d'un joueur
		les string sont de la forme "A1D" : 1e lettre et chiffre pour la position dans la grille, 2e lettre pour la direction dans laquelle est orient� le bateau
		porte-avion : 5 cases
		croiseur : 4 cases
		sous-marins : 3 cases
		torpilleur : 2 cases
		
		�tats des cases de la grille :
			- 0 : vide
			- 1 : touch�
			- 2 : occupp� par le torpilleur
			- 31 : occup� par le sous-marin 1
			- 32 : occup� par le sous-marin 2
			- 4 : occup� par le croiseur
			- 5 : occup� par le porte-avion
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
		//m�thode pla�ant les bateaux sur le plateau, renvoi false si il y'a collision avec un autre bateau
		
		int ligne = placement.charAt(0) - 'A';
		int colonne = placement.charAt(1) - '0';
		
		if(placement.charAt(2)=='D' || placement.charAt(2)=='G') {
			//cas o� le bateau est sur une ligne horizontale
			
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
			//cas o� le bateau est sur une ligne verticale
			
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
			
			//mise � jour de l'�tat de la flotte
			etatFlotte-=1;
			
			//v�rification si la flotte est encore op�rationnel
			if (etatFlotte==0) {
				return "victoire";
			}
			
			//mise � jour de l'�tat de la flotte par bateau
			switch(etatCase) {
			case 5:
				etatPorteAvion-=1;
				if (etatPorteAvion==0)return "coul�";
				break;
			case 4:
				etatCroiseur-=1;
				if(etatCroiseur==0)return"coul�";
				break;
			case 31:
				etatSousMarin1-=1;
				if(etatSousMarin1==0)return "coul�";
				break;
			case 32:
				etatSousMarin2-=1;
				if(etatSousMarin2==0) return "coul�";
				break;
			case 2:
				etatTorpilleur-=1;
				if(etatTorpilleur==0) return "coul�";
				break;
			default:
				break;
			}
			
			return "touch�";
			
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
		//m�thode v�rifiant la validit� des coordonn�es de placement de bateau entr�es par le client et positionne le bateau si les coordonn�es sont valides
		
		if(coord.length()!=3) {
			return "Veuillez entrer 3 characteres ( exemple : J6H ) : ";
		}
		else {
			int ligne = coord.charAt(0) - 'A';
			int colonne = coord.charAt(1) - '0';
					
			//v�rification que les coordonn�es sont valides
			if(ligne<0 || ligne>10 || colonne<0 || colonne>10) {
				return "Coordonn�es invalides, veuillez r�essayer : ";
			}
			else {
				//v�rification de la direction
				if(coord.charAt(2)!='H' && coord.charAt(2)!='B' && coord.charAt(2)!='G' && coord.charAt(2)!='D') {
					return "La direction entr�e est invalide, veuillez r�essayer : ";
				}
				else {
					//v�rification que le bateau rentre
					if((coord.charAt(2)=='H' && ligne-(taille-1)<0) || (coord.charAt(2)=='B' && ligne+(taille-1)>9) || (coord.charAt(2)=='G' && colonne-(taille-1)<0) || (coord.charAt(2)=='D' && colonne+(taille-1)>9)) {
						return "Le bateau ne rentre pas dans cette direction (taille : "+taille+"), veuillez r�essayer : ";
					}
					else {
						//v�rification qu'il n'y a pas de collision avec un autre bateau
						if(!placementBateau(coord,taille,0)) {
							return "Collision avec un autre bateau, veuillez r�essayer : ";
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
		//m�thode v�rifiant la validit� des coordonn�es d'attaque et effectue l'attaque si les coordonn�es sont valide

		if(coord.length()!=2) {
			return "Veuillez entrer 2 characteres ( exemple : D6 ) : ";
		}
		else {		
			int ligne = coord.charAt(0) - 'A';
			int colonne = coord.charAt(1) - '0';
				
			//v�rification que les coordonn�es sont valides
			if(ligne<0 || ligne>10 || colonne<0 || colonne>10) {
				return "Coordonn�es invalides, veuillez r�essayer : ";
			}
			else {
				if(grille[ligne][colonne]==1) {
					return "Vous avez d�j� attaqu� cette case, veuillez r�essayer : ";
				}
				else {
					return attaqueCase(coord);
				}
			}
		}	
		
	}
	
	
	
	

}
