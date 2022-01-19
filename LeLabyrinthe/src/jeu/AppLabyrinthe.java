package jeu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import modele.Aventurier;
import modele.Espace;
import modele.Labyrinthe;
import modele.Mur;
import modele.Objet;
import modele.Personnage;
import modele.Piege;
import vue.Vue;


/**
 * Application de l'aventurier et du labyrinthe.
 */
public class AppLabyrinthe 
{
	/* permet de lire un fichier wav */
	public static void jouerMusiqueLoop (String emplacementMusique)
		{
		try {
		    File wavFile = new File(emplacementMusique);
		    Clip clip = AudioSystem.getClip();
		    clip.open(AudioSystem.getAudioInputStream(wavFile));
		    clip.start();
		    clip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (Exception e) {
		    System.out.println(e);
		}
	}
	
	/**
	 * Noms des fichiers contenant des labyrinthes.
	 */
	private static final String LAB_1 = "data" + File.separator + "labyrinthe_1.csv";


	/**
	 * Chargement du labyrinthe et de l'aventurier.
	 *
	 * A partir d'un fichier csv, cette mÃ©thode crÃ©e le labyrinthe et
	 * l'aventurier sur la case de dÃ©part du labyrinthe.
	 */
	public static Labyrinthe chargeLabyrinthe (String fichier)
		{
		Labyrinthe lab = null;
		
		int nbpiege = 3;			/* car 3 pièges prédéfinis dans ce csv */
		
		int nbpnj = 0;

		try (Scanner scanner = new Scanner(new FileInputStream(fichier)))
			{
			/*----- Lecture de la taille du labyrinthe -----*/
			int taille = Integer.valueOf(scanner.nextLine());

			/*----- Initialisation du labyrinthe -----*/
			lab = new Labyrinthe(taille);

			/*----- Lecture du fichier et des types de cases composant le labyrinthe -----*/
			for (int i=0; i<taille; i++)
				{
				/*----- Lecture d'une ligne du fichier -----*/
				String[] liste = scanner.nextLine().trim().split(";");

				int type_case;
				for (int j=0; j<taille; j++)
					{
					type_case = Integer.valueOf(liste[j]);

					/*----- Type 0 --> "Terre ne contenant pas d'objet" -----*/
					if (type_case == 0) lab.setCase(i, j, new Espace("Terre",false, ( 10000 )));
					
					/*----- Type 1 --> "Mur" -----*/
					if (type_case == 1) lab.setCase(i, j, new Mur());
					
					/*----- Type 2 --> "Eau" -----*/
					if (type_case == 2) lab.setCase(i, j, new Espace("Eau",false, (10000 )));
					
					/*----- Type 3 --> "Sable" -----*/
					if (type_case == 3) lab.setCase(i, j, new Espace("Sable",false, ( 10000 )));
					
					/*----- Type 4 --> "Glace ne contenant pas d'objet" -----*/
					if (type_case == 4) lab.setCase(i, j, new Espace("Glace",false, ( 10000 )));
					
					/*----- Type 5 --> "Terre contenant un objet" -----*/
					if (type_case == 5) lab.setCase(i, j, new Espace("Terre",true, ( 10000 )));
					
					/*----- Type 6 --> "Glace contenant un objet" -----*/
					if (type_case == 6) lab.setCase(i, j, new Espace("Glace",true, ( 10000 )));
					
					/*----- Type 7 --> "Espace de dÃ©part" et "Aventurier" -----*/
					if (type_case == 7)
						{
						lab.setCase(i, j, new Espace("Terre",false, ( 10000 )));
						lab.setAventurier(new Aventurier(i, j, 100, "vivant"));
						}
					
					
					/*----- Création et placement des objets dans le labyrinthe -----*/
					if (lab.getCase(i, j).getContientObjet()==true)
						{
						int randomNumVisibilite = (int)(Math.random()*(3-1+1)+1);
						int randomNumTypeObjVisible = (int)(Math.random()*(11-1+1)+1);
						int randomNumTypeObjInvisible = (int)(Math.random()*(13-1+1)+1);
						if (randomNumVisibilite == 1)												/* les objets ont 1 chance sur 3 d'être visibles (caché dans un coffre) */
							switch ( randomNumTypeObjVisible ) {									/* détermine le type de l'objet au hasard entre tout les objets possibles */
								case 1: 															/* rq: certains objets ne peuvent apparaître que si ils sont invisible */
									lab.setObjet(i, j, new Objet("Pioche", "Permet de casser un rocher sans dépenser d'énergie", true));
									break;
								case 2: 
									lab.setObjet(i, j, new Objet("Hache", "Permet de casser un arbre sans dépenser d'énergie", true));
									break;
								case 3: 
									lab.setObjet(i, j, new Objet("Antidote", "Soigne du poison", true));
									break;
								case 4: 
									lab.setObjet(i, j, new Objet("Carte", "Révèle les pièges cachés", true));
									break;
								case 5: 
									lab.setObjet(i, j, new Objet("Coeur", "Rend 10 points de vie", true));
									break;
								case 6: 
									lab.setObjet(i, j, new Objet("Pomme dorée", "Rend 20 points de vie", true));
									break;
								case 7: 
									lab.setObjet(i, j, new Objet("Chaussures de course", "Permet d'avancer de deux cases à chaque déplacement (seulement sur les espaces de type terre)", true));
									break;
								case 8: 
									lab.setObjet(i, j, new Objet("Chunchunmaru", "Cette épée simplifie les combats contre les monstres et permet d'éviter les pnj hostiles", true));
									break;
								case 9: 
									lab.setObjet(i, j, new Objet("Palmes", "réduit le coût des déplacements dans l'eau à 1 point de vie par déplacement", true));
									break;
								case 10: 
									lab.setObjet(i, j, new Objet("Armure", "Réduit les dégats reçu des monstres et des pièges", true));
									break;
								case 11: 
									lab.setObjet(i, j, new Objet("Cape d'invisibilité", "permet d'éviter totalement les monstres", true));
									break;
								default:
									System.out.println("Erreur dans le switch de selection d'objet");}
						else
							switch ( randomNumTypeObjInvisible ) {
							case 1: 
								lab.setObjet(i, j, new Objet("Pioche", "Permet de casser un rocher sans dépenser d'énergie", false));
								break;
							case 2: 
								lab.setObjet(i, j, new Objet("Hache", "Permet de couper un arbre sans dépenser d'énergie", false));
								break;
							case 3: 
								lab.setObjet(i, j, new Objet("Antidote", "Soigne du poison", false));
								break;
							case 4: 
								lab.setObjet(i, j, new Objet("Carte", "Révèle les pièges cachés", false));
								break;
							case 5: 
								lab.setObjet(i, j, new Objet("Coeur", "Rend 10 points de vie", false));
								break;
							case 6: 
								lab.setObjet(i, j, new Objet("Pomme dorée", "Rend 20 points de vie", false));
								break;
							case 7: 
								lab.setObjet(i, j, new Objet("Chaussures de course", "Permet d'avancer de deux cases à chaque déplacement (seulement sur les espaces de type terre)", false));
								break;
							case 8: 
								lab.setObjet(i, j, new Objet("Chunchunmaru", "Cette épée simplifie les combats contre les monstres et permet d'éviter les pnj hostiles", false));
								break;
							case 9: 
								lab.setObjet(i, j, new Objet("Palmes", "réduit le coût des déplacements dans l'eau à 1 point de vie par déplacement", false));
								break;
							case 10: 
								lab.setObjet(i, j, new Objet("Armure", "Réduit les dégats reçu des monstres et des piques", false));
								break;
							case 11: 
								lab.setObjet(i, j, new Objet("Cape d'invisibilité", "permet d'éviter totalement les monstres", false));
								break;
							case 12:
								lab.setObjet(i, j, new Objet("Pomme dorée enchanté", "rend 30 points de vie", false));
								break;
							case 13:
								lab.setObjet(i, j, new Objet("totem d'immortalité", "si l'aventurier meurt, il est résucité avec 10 points de vie", false));
								break;
							default:
								System.out.println("Erreur dans le switch de selection d'objet");}
						}
					/*----- Création et placement des pièges dans le labyrinthe -----*/
					/*----- pièges prédéfinis -----*/
					if ((i==5 && j==18) || (i==18 && j==1))	
						{lab.setPiege(i, j, new Piege("Rocher", "Doit être cassé pour pouvoir franchir la case. Le casser coûte 15 points de vie",true));
						lab.getCase(i, j).setContientPiege(true);}
					if (i==15 && j==5)	
						{lab.setPiege(i, j, new Piege("Arbre", "Doit être coupé pour pouvoir franchir la case. Le casser coûte 10 points de vie",true));
						lab.getCase(i, j).setContientPiege(true);}
					/*----- pièges placés au hasard (total de 10 pièges dans le labyrinthe) -----*/
					if (nbpiege<10)				/* max 10 pièges au total, cases interdites pour les pièges: départ, arrivée, cases des pièges prédéfinis, eau, sable */
						if (((lab.getCase(i, j).getType().equals("Terre")) || (lab.getCase(i, j).getType().equals("Glace"))) && (!(j==20 && i==0)) && (!(j==1 && i==21) && (!(i==5 && j==18)) && (!(i==18 && j==1)) && (!(i==15 && j==5))))
							if(Math.random()<0.03664921465968586387434554973822)		
					/* tant que les 10 pièges totaux ne sont pas atteint, chaque case a 7/191 chance de contenir un piège (nombre de pièges à placer / nombre de cases pouvant contenir un piège)   */
								if(Math.random()<0.7)	/* 30% de chance d'être invisible */
									{int randomNumTypePiegeVisible = (int)(Math.random()*(6-1+1)+1);
									switch ( randomNumTypePiegeVisible ) {
									case 1: 
										lab.setPiege(i, j, new Piege("Rocher", "Doit être cassé pour pouvoir franchir la case. Le casser coûte 15 points de vie",true));
										lab.getCase(i, j).setContientPiege(true);
										nbpiege = nbpiege+1;
										break;
									case 2: 
										lab.setPiege(i, j, new Piege("Arbre", "Doit être coupé pour pouvoir franchir la case. Le casser coûte 10 points de vie",true));
										lab.getCase(i, j).setContientPiege(true);
										nbpiege = nbpiege+1;
										break;
									case 3: 
										lab.setPiege(i, j, new Piege("Poison", "Augmente le coût en point de vie de chaque déplacement de 1. Cumulable, soignable.",true));
										lab.getCase(i, j).setContientPiege(true);
										nbpiege = nbpiege+1;
										break;
									case 4: 
										lab.setPiege(i, j, new Piege("Piques", "Fait perdre 5 points de vie",true));
										lab.getCase(i, j).setContientPiege(true);
										nbpiege = nbpiege+1;
										break;
									case 5: 
										lab.setPiege(i, j, new Piege("Boulet", "Fait couler l'aventurier dans l'eau et les sables mouvants (mort instantannée)",true));
										lab.getCase(i, j).setContientPiege(true);
										nbpiege = nbpiege+1;
										break;
									case 6: 
										if(Math.random()<0.666)		/* un monstre a 0.333% chance d'être un boss */
											{lab.setPiege(i, j, new Piege("Monstre", "Enlève 5 points de vie quand attaqué et peu rester en vie. Peut également laisser tomber un objet à sa mort",true));
											lab.getCase(i, j).setContientPiege(true);
											nbpiege = nbpiege+1;}
										else
											{lab.setPiege(i, j, new Piege("Boss", "Enlève 15 points de vie quand attaqué et a de fortes chances de rester en vie. Laisse tomber un objet à sa mort",true));
											lab.getCase(i, j).setContientPiege(true);
											nbpiege = nbpiege+1;}		
										break;
									default:
										System.out.println("Erreur dans le switch de selection de piège");}}
								else
									if (Math.random()<0.9)
										{lab.setPiege(i, j, new Piege("Piques", "Fait perdre 5 points de vie",false));
										lab.getCase(i, j).setContientPiege(true);
										nbpiege = nbpiege+1;}
									else
										{lab.setPiege(i, j, new Piege("Abime", "Mort instantannée",false));
										lab.getCase(i, j).setContientPiege(true);
										nbpiege = nbpiege+1;}
					/*----- Création et placement des personnages dans le labyrinthe -----*/
					/*----- personnages placés au hasard (total de 5 personnages dans le labyrinthe) -----*/
					if (nbpnj<5)				/* max 5 personnages au total, cases interdites pour les personnages: départ, arrivée, eau, sable */
						if (((lab.getCase(i, j).getType().equals("Terre")) || (lab.getCase(i, j).getType().equals("Glace"))) && (!(j==20 && i==0)) && (!(j==1 && i==21)))
							if(Math.random()<0.02577319587628865979381443298969)		
					/* tant que les 5 personnages totaux ne sont pas atteint, chaque case a 5/191 chance de contenir un personnage (nombre de personnage à placer / nombre de cases pouvant contenir un personnage)   */
								{int randomNumTypePersonnage = (int)(Math.random()*(5-1+1)+1);
								switch ( randomNumTypePersonnage ) {
								case 1: 
									lab.setPersonnage(i, j, new Personnage("Voleur", "Vole un objet de l'aventurier au hasard"));
									lab.getCase(i, j).setContientPersonnage(true);
									nbpnj = nbpnj+1;
									break;
								case 2: 
									lab.setPersonnage(i, j, new Personnage("Illusioniste", "Prend l'apprarence d'un guerisseur mais pôssède les même propriétés qu'un boss"));
									lab.getCase(i, j).setContientPersonnage(true);
									nbpnj = nbpnj+1;
									break;
								case 3: 										
									lab.setPersonnage(i, j, new Personnage("Explorateur", "Révèle tout les objets cachés dans des coffres"));
									lab.getCase(i, j).setContientPersonnage(true);
									nbpnj = nbpnj+1;
									break;
								case 4: 
									lab.setPersonnage(i, j, new Personnage("Guérisseur", "Soigne 10, 20 ou 30 points de vie"));
									lab.getCase(i, j).setContientPersonnage(true);
									nbpnj = nbpnj+1;
									break;
								case 5: 
									lab.setPersonnage(i, j, new Personnage("Druide", "Guéris du poison"));
									lab.getCase(i, j).setContientPersonnage(true);
									nbpnj = nbpnj+1;
									break;
								default:
									System.out.println("Erreur dans le switch de selection de personnage");}}
					}
				}
			}
		catch (FileNotFoundException ex)
			{
			System.err.println("Erreur lors de la lecture du fichier : " + fichier + " - " + ex.getMessage());
			}
		
		/* distance */
		lab.getCase(21, 1).setDistanceFin(0);
		int i;
		int j;
		boolean stop = false;
		while (stop==false)
		{
		stop=true;
		for (i=lab.getTaille()-2;i>0;i-=1)
			{
			for (j=1;j<lab.getTaille()-1;j++)
				{
				if ( !(lab.getCase(i, j).getType().equals("Infranchissable")) )
					{
					if ( lab.getCase(i+1, j).getDistanceFin() < lab.getCase(i, j).getDistanceFin() )
						lab.getCase(i, j).setDistanceFin(lab.getCase(i+1, j).getDistanceFin()+1);
					if ( lab.getCase(i-1, j).getDistanceFin() < lab.getCase(i, j).getDistanceFin() )
						lab.getCase(i, j).setDistanceFin(lab.getCase(i-1, j).getDistanceFin()+1);
					if ( lab.getCase(i, j+1).getDistanceFin() < lab.getCase(i, j).getDistanceFin() )
						lab.getCase(i, j).setDistanceFin(lab.getCase(i, j+1).getDistanceFin()+1);
					if ( lab.getCase(i, j-1).getDistanceFin() < lab.getCase(i, j).getDistanceFin() )
						lab.getCase(i, j).setDistanceFin(lab.getCase(i, j-1).getDistanceFin()+1);
					if ( lab.getCase(i, j+1).getDistanceFin() < lab.getCase(i, j).getDistanceFin() )
						lab.getCase(i, j).setDistanceFin(lab.getCase(i, j+1).getDistanceFin()+1);
					if ( lab.getCase(i, j).getDistanceFin()==10000 )
						stop =false;
					}
				}
			}
		}
		return lab;
		}


	/*---------------------*/
	/* Programme principal */
	/*---------------------*/

	public static void main (String[] s) throws InterruptedException
		{
		/*----- Chargement du labyrinthe -----*/
		Labyrinthe labyrinthe = chargeLabyrinthe(LAB_1);

		/*----- CrÃ©ation de la fenÃªtre de visualisation du labyrinthe et affichage -----*/
		new Vue(100, 100, labyrinthe);
	
		/* musique principale */
		jouerMusiqueLoop(System.getProperty("user.dir")+"\\source\\main.wav");
		}

} /*----- Fin de ma classe AppLabyrinthe -----*/
