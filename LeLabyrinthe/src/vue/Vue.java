package vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import modele.Aventurier;
import modele.Labyrinthe;
import modele.Objet;
import modele.Personnage;
import modele.Piege;

import java.awt.Toolkit;


/**
 * Fenêtre de visualisation du labyrinthe.
 */
@SuppressWarnings("serial")
public class Vue extends JFrame
{
	/*------------*/
	/* Propriétés */
	/*------------*/

	/**
	 * Référence vers le labyrinthe que la classe Vue va visualiser.
	 */
	private final Labyrinthe labyrinthe;

	/*----- Barre d'état de la fenêtre -----*/
	private final JLabel barre_etat;

	/*----- Zone de dessin -----*/
	Dessin dessin;
	
	/*--------------*/
	/* Constructeur */
	/*--------------*/
	public Vue (int x, int y, Labyrinthe labyrinthe)
		{
		/*----- Lien avec le labyrinthe -----*/
		this.labyrinthe = labyrinthe;

		/*----- Paramètres de la fenêtre -----*/
		this.setTitle("Labyrinthe                                            Remarque: il est conseill� de jouer avec le son activ�");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocation(x,y);
		this.setLayout(new BorderLayout());

		/*----- Zone de dessin -----*/
		int res = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()-70;
		this.dessin = new Dessin(res);
		this.dessin.setFocusable(true);
		/*----- Attachement des écouteurs des évènements souris et clavier -----*/
		this.dessin.addMouseListener(this.dessin);
		this.dessin.addMouseMotionListener(this.dessin);
		this.dessin.addKeyListener(this.dessin);

		this.add(this.dessin, BorderLayout.CENTER);

		/*----- Barre d'état de la fenêtre -----*/
		this.barre_etat = new JLabel("Barre d'état");
		this.add(this.barre_etat, BorderLayout.SOUTH);

		/*----- Pour ajuster la fenêtre à son contenu  et la rendre visible -----*/
		this.pack();
		this.setVisible(true);
		/* fullscreen:
		 this.setExtendedState(JFrame.MAXIMIZED_BOTH); */
		}

	/*----------------*/
	/* Classe interne */
	/*----------------*/

	class Dessin extends JPanel implements KeyListener, MouseListener, MouseMotionListener
		{
		/*----- Propriétés de la classe interne -----*/
		int largeur;
		int taille_case;

		/*----- Constructeur de la classe interne -----*/
		public Dessin (int larg)
			{
			/*----- Initialisation des données -----*/
			this.taille_case = larg / labyrinthe.getTaille();
			this.largeur = this.taille_case * labyrinthe.getTaille();

			/*----- Paramètre du JPanel -----*/
			this.setPreferredSize(new Dimension(this.largeur+100, this.largeur));			/* +100 pour rajouter une bordure */
			}
		
		/* permet de lire un fichier wav */
		public void jouerMusique (String emplacementMusique)
			{
			try {
			    File wavFile = new File(emplacementMusique);
			    Clip clip = AudioSystem.getClip();
			    clip.open(AudioSystem.getAudioInputStream(wavFile));
			    clip.start();
			} catch (Exception e) {
			    System.out.println(e);
			}
		}
		
		/**
		 * Méthode qui permet de dessiner ou redessinner le labyrinthe lorsque
		 * la méthode repaint() est appellée sur la classe Dessin.
		 */
		
		final int numApparenceIllusioniste = (int)(Math.random()*(3-1+1)+1);
		
		@Override
		public void paint (Graphics g)
			{		
			if ( labyrinthe.getAventurier().getEnergie()==0)
				{
					int i;
					for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
					{
						if ( labyrinthe.getAventurier().getInventaire()[i].equals("totem d'immortalit�") )		/* effet totem d'immortalit� */
						{labyrinthe.getAventurier().setEnergie(10);
						labyrinthe.getAventurier().setInventaire(i,"Vide");
						break;}
						if ( labyrinthe.getAventurier().getInventaire()[i].equals("1up") )						/* effet vie suppl�mentaire */
						{labyrinthe.getAventurier().setEnergie(100);
						labyrinthe.getAventurier().setInventaire(i,"Vide");
						break;}
					}
				}
			/* les dessins dans la bordure ne sont pas situ� dans le m�me if que le reste du labyrinthe pour permettre d'afficher la bordure � cot� de l'�cran de mort */
			if (labyrinthe.getAventurier().getStatu()=="vivant")					/* �vit� d'actualiser la bordure si l'aventurier est mort et que l'utilisateur appui sur un bouton */
				{/*----- On efface le dessin en entier de dessin -----*/
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(0,0,this.largeur+100,this.largeur);
			
				/*----- Affichage de la barre d'�nergie de l'aventurier -----*/
				/*---- fonctionnement : on dessine une rectangle vert -----*/
				g.setColor(Color.GREEN);
				g.fillRect(labyrinthe.getTaille()*taille_case+20, 25, 50, 100);
				/*---- qu'on recouvre ensuite par un rectangle blanc de hauteur correspondant � la diff�rence entre la hauteur du rectangle vert (�nergie maximum) et l'�nergie actuelle -----*/
				g.setColor(Color.WHITE);
				g.fillRect(labyrinthe.getTaille()*taille_case+20, 25, 50, 100-labyrinthe.getAventurier().getEnergie());
				/*---- affichage de l'�nergie ---- */
				g.setColor(Color.BLACK);
				g.drawString("Energie "+labyrinthe.getAventurier().getEnergie(),labyrinthe.getTaille()*taille_case+20, 20);}
			
			/*----- On dessine le labyrinthe si l'aventurier a de l'�nergie -----*/
			if (labyrinthe.getAventurier().getEnergie()>0) {
			
			/*----- Chargement des images -----*/
			/*----- Cr�ation de l'image des murs -----*/
			BufferedImage murpng = null;
			try {
				murpng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\mur.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			/*----- Cr�ation de l'image des espaces de type terre -----*/
			BufferedImage terrepng = null;
			try {
				terrepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\terre.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			/*----- Cr�ation des image des espaces de type eau -----*/
			BufferedImage eaupng = null;
			try {
				eaupng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\eau.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			/*----- Cr�ation de l'image des espaces de type sable -----*/
			BufferedImage sablepng = null;
			try {
				sablepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\sable.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			/*----- Cr�ation de l'image des espaces de type glace -----*/
			BufferedImage glacepng = null;
			try {
				glacepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\glace.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			/*----- Cr�ation de l'image des coffres -----*/
			BufferedImage coffrepng = null;
			try {
				coffrepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\coffre.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			/*----- Cr�ation des images des objets -----*/
			BufferedImage piochepng = null;
			try {
				piochepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\pioche.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage hachepng = null;
			try {
				hachepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\hache.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage antidotepng = null;
			try {
				antidotepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\antidote.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage cartepng = null;
			try {
				cartepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\carte.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage coeurpng = null;
			try {
				coeurpng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\coeur.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage pommedpng = null;
			try {
				pommedpng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\pommed.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage chaussurepng = null;
			try {
				chaussurepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\chaussure.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage epeepng = null;
			try {
				epeepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\epee.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage palmespng = null;
			try {
				palmespng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\palmes.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage armurepng = null;
			try {
				armurepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\armure.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage capepng = null;
			try {
				capepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\cape.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage pommedepng = null;
			try {
				pommedepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\pommede.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage totempng = null;
			try {
				totempng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\totem.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage viepng = null;
			try {
				viepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\vie.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			/*----- Cr�ation des images des pi�ges -----*/
			BufferedImage rocherpng = null;
			try {
				rocherpng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\cailloux.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage arbrepng = null;
			try {
				arbrepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\arbre.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage poisonpng = null;
			try {
				poisonpng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\poison.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage piquespng = null;
			try {
				piquespng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\piques.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage bouletpng = null;
			try {
				bouletpng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\boulet.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage monstrepng = null;
			try {
				monstrepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\monstre.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage bosspng = null;
			try {
				bosspng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\boss.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage mimicpng = null;
			try {
				mimicpng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\mimic.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage abimepng = null;
			try {
				abimepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\abime.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			/*----- Cr�ation des images des personnages -----*/
			BufferedImage voleurpng = null;
			try {
				voleurpng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\voleur.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage illusionistepng = null;
			try {
				illusionistepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\illusioniste.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage explorateurpng = null;
			try {
				explorateurpng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\explorateur.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage guerisseurpng = null;
			try {
				guerisseurpng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\guerisseur.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			BufferedImage druidepng = null;
			try {
				druidepng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\druide.png"));
			} catch (IOException e) {
			e.printStackTrace();}
			/*----- Cr�ation de l'image de l'aventurier -----*/
			BufferedImage aventurierpng = null;
			try {
				aventurierpng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\celeste.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			/*----- Affichage des cases du labyrinthe -----*/
			for (int i=0; i < labyrinthe.getTaille(); i++)
				for (int j=0; j < labyrinthe.getTaille(); j++)
					{
					/*----- Image de chaque case -----*/
					if (labyrinthe.getCase(i,j).getClassName().equals("Mur"))	g.drawImage(murpng,taille_case*j, taille_case*i, taille_case, taille_case, null);
					else
						if (labyrinthe.getCase(i,j).getType().equals("Terre"))	g.drawImage(terrepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
						if (labyrinthe.getCase(i,j).getType().equals("Eau"))	g.drawImage(eaupng,taille_case*j, taille_case*i, taille_case, taille_case, null);
						if (labyrinthe.getCase(i,j).getType().equals("Sable"))	g.drawImage(sablepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
						if (labyrinthe.getCase(i,j).getType().equals("Glace"))	g.drawImage(glacepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
					/*----- Ajout des objets -----*/
					if (labyrinthe.getCase(i,j).getContientObjet()==true)
						if (labyrinthe.getObjet(i, j).getEstDesactive()==false)
							if (labyrinthe.getObjet(i, j).getEstVisible()==false)
								g.drawImage(coffrepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
							else 
								switch ( labyrinthe.getObjet(i, j).getType() ) {
									case "Pioche" : 
										g.drawImage(piochepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Hache" : 
										g.drawImage(hachepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Antidote" : 
										g.drawImage(antidotepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Carte" : 
										g.drawImage(cartepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Coeur" : 
										g.drawImage(coeurpng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Pomme dor�e" : 
										g.drawImage(pommedpng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Chaussures de course" : 
										g.drawImage(chaussurepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Chunchunmaru" : 
										g.drawImage(epeepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Palmes" : 
										g.drawImage(palmespng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Armure" : 
										g.drawImage(armurepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Cape d'invisibilit�" : 
										g.drawImage(capepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "totem d'immortalit�" : 
										g.drawImage(totempng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Pomme dor�e enchant�" : 
										g.drawImage(pommedepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;			
									default:
										System.out.println("Erreur dans le switch d'affichage des objets");							
									}
					/*----- Ajout des pi�ges -----*/
					if (labyrinthe.getCase(i,j).getContientPiege()==true)
						if (labyrinthe.getPiege(i, j).getEstDesactive()==false)
							if (labyrinthe.getPiege(i, j).getEstVisible()==true)
								switch ( labyrinthe.getPiege(i, j).getType() ) {
									case "Rocher" : 
										g.drawImage(rocherpng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Arbre" : 
										g.drawImage(arbrepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Poison" : 
										g.drawImage(poisonpng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Piques" : 
										g.drawImage(piquespng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Boulet" : 
										g.drawImage(bouletpng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Monstre" : 
										if (labyrinthe.getPiege(i, j).getMimic()==true)
											g.drawImage(mimicpng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										else
											g.drawImage(monstrepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Boss" : 
										if (labyrinthe.getPiege(i, j).getIllusioniste()==true)
											g.drawImage(illusionistepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										else
											g.drawImage(bosspng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									case "Abime" :
										g.drawImage(abimepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
										break;
									default:
										System.out.println("Erreur dans le switch d'affichage des objets");							
										}
					/*----- Ajout des personnages -----*/
					if (labyrinthe.getCase(i,j).getContientPersonnage()==true)
						if (labyrinthe.getPersonnage(i, j).getEstDesactive()==false)
							switch ( labyrinthe.getPersonnage(i, j).getType() ) {
								case "Voleur" :
									g.drawImage(voleurpng,taille_case*j, taille_case*i, taille_case, taille_case, null);
									break;
								case "Explorateur" :
									g.drawImage(explorateurpng,taille_case*j, taille_case*i, taille_case, taille_case, null);
									break;
								case "Gu�risseur" :
									g.drawImage(guerisseurpng,taille_case*j, taille_case*i, taille_case, taille_case, null);
									break;
								case "Druide" :
									g.drawImage(druidepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
									break;
								case "Illusioniste" :
									switch (numApparenceIllusioniste) {
										case 1:
											g.drawImage(explorateurpng,taille_case*j, taille_case*i, taille_case, taille_case, null);
											break;
										case 2:
											g.drawImage(guerisseurpng,taille_case*j, taille_case*i, taille_case, taille_case, null);
											break;
										case 3:
											g.drawImage(druidepng,taille_case*j, taille_case*i, taille_case, taille_case, null);
											break;
										default:
											System.out.println("Erreur dans le switch d'affichage des illusionistes");
											}
									break;
								default:
									System.out.println("Erreur dans le switch d'affichage des personnages");
								}
					}	
			
			/*----- Affichage de l'aventurier -----*/
			Aventurier aventurier = labyrinthe.getAventurier();
			g.drawImage(aventurierpng,taille_case*aventurier.getY() + taille_case/4, taille_case*aventurier.getX() + taille_case/4, taille_case/2, taille_case/2, null);
			/*----- Affichage de l'�cran de victoire si l'aventurier a gagn� -----*/
			if ( (labyrinthe.getAventurier().getX()==21) && (labyrinthe.getAventurier().getY()==1) )
				{
				jouerMusique(System.getProperty("user.dir")+"\\source\\victoire.wav");
				BufferedImage victoire = null;
				try {
					victoire = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\win.png"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				g.drawImage(victoire,0,0,taille_case*labyrinthe.getTaille(),taille_case*labyrinthe.getTaille(), null);
				}
			/*----- Affichage de l'inventaire de l'aventurier -----*/
			g.drawString("Inventaire",labyrinthe.getTaille()*taille_case+20, 150);
			int i;
			int pos=160;
			for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
				{
				switch (labyrinthe.getAventurier().getInventaire()[i]) {
					case "Boulet":
						g.drawImage(bouletpng,labyrinthe.getTaille()*taille_case+20 , pos, taille_case/2, taille_case/2, null);
						pos = pos+30;
						break;
					case "Pioche":
						g.drawImage(piochepng,labyrinthe.getTaille()*taille_case+20 , pos, taille_case/2, taille_case/2, null);
						pos = pos+30;
						break;
					case "Hache":
						g.drawImage(hachepng,labyrinthe.getTaille()*taille_case+20 , pos, taille_case/2, taille_case/2, null);
						pos = pos+30;
						break;
					case "Chaussures de course":
						g.drawImage(chaussurepng,labyrinthe.getTaille()*taille_case+20 , pos, taille_case/2, taille_case/2, null);
						pos = pos+30;
						break;
					case "Chunchunmaru":
						g.drawImage(epeepng,labyrinthe.getTaille()*taille_case+20 , pos, taille_case/2, taille_case/2, null);
						pos = pos+30;
						break;
					case "Palmes":
						g.drawImage(palmespng,labyrinthe.getTaille()*taille_case+20 , pos, taille_case/2, taille_case/2, null);
						pos = pos+30;
						break;
					case "Armure":
						g.drawImage(armurepng,labyrinthe.getTaille()*taille_case+20 , pos, taille_case/2, taille_case/2, null);
						pos = pos+30;
						break;
					case "Cape d'invisibilit�":
						g.drawImage(capepng,labyrinthe.getTaille()*taille_case+20 , pos, taille_case/2, taille_case/2, null);
						pos = pos+30;
						break;
					case "totem d'immortalit�":
						g.drawImage(totempng,labyrinthe.getTaille()*taille_case+20 , pos, taille_case/2, taille_case/2, null);
						pos = pos+30;
						break;
					case "1up":
						g.drawImage(viepng,labyrinthe.getTaille()*taille_case+20 , pos, taille_case/2, taille_case/2, null);
						pos = pos+30;
						break;						/* default <=> inventaire vide donc on ne fait rien */
					}
				}
			/*----- Affichage des debuff de l'aventurier -----*/
			pos = pos+30;
			g.drawString("Etat",labyrinthe.getTaille()*taille_case+20, pos);
			for (i=0;i<labyrinthe.getAventurier().getDebuff().length;i++)
			{
			switch (labyrinthe.getAventurier().getDebuff()[i]) {
				case "Empoisonn�":
					pos = pos+30;
					final Color vertFonc� = new Color(0,102,0);
					g.setColor(vertFonc�);
					g.drawString("Empoisonn�",labyrinthe.getTaille()*taille_case+20 , pos);
					pos = pos+30;
					break;						/* default <=> aucun debuff donc on ne fait rien */
				}
			}
			
			/* labyrinthe.getTaille()*taille_case+20 */
			}
			
			/*----- Si l'aventurier n'a plus d'�nergie on affiche l'�cran de mort et on jou le son qui vas avec -----*/
			else
			{
			if (labyrinthe.getAventurier().getStatu()=="vivant")				/* �vite de rejouer l'audio si l'utilisateur appui sur un bouton apr�s �tre mort */
				{jouerMusique(System.getProperty("user.dir")+"\\source\\mort.wav");
				BufferedImage mortpng = null;
				try {
					mortpng = ImageIO.read(new File(System.getProperty("user.dir")+"\\source\\mort.png"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				g.drawImage(mortpng,0,0,taille_case*labyrinthe.getTaille(),taille_case*labyrinthe.getTaille(), null);
				labyrinthe.getAventurier().setStatu("mort");}					/* on d�fini �galement son statu sur mort */	
			}
			}
			

		/**
		 * Gestion des interactions souris et clavier sur le labyrinthe.
		 */
		
		/*----- dispObj sert dans l'affichage de l'�tat de la case -----*/
		public String dispObj (boolean condition, Objet obj)	{if (condition==true)
																	if (obj.getEstDesactive()==false)
																		if (obj.getEstVisible()==true)	
																			return ".\nElle contient l'objet : "+obj.getType();	
																		else
																			return ".\nElle contient un objet cach� dans un coffre";
																	else
																		return "";
																else	return ""; }
		
		/*----- dispPiege sert dans l'affichage de l'�tat de la case -----*/
		public String dispPiege (boolean condition, Piege piege)	{if (condition==true)
																		if (piege.getEstDesactive()==false)	
																			if (piege.getEstVisible()==true)	
																				return ".\nElle contient le pi�ge : "+piege.getType();
																			else
																				return "";
																		else
																			return "";
																	else	return ""; }
		
		/*----- dispPersonnage sert dans l'affichage de l'�tat de la case -----*/
		public String dispPersonnage (boolean condition, Personnage pnj)	{String res="";
																			if (condition==true)	
																				if (pnj.getEstDesactive()==false)	 
																					{if (pnj.getType().equals("Illusioniste"))	
																						{switch (numApparenceIllusioniste) {
																							case 1:
																								res=".\nElle contient le personnage : Explorateur";
																								break;
																							case 2:
																								res=".\nElle contient le personnage : Gu�risseur";
																								break;
																							case 3:
																								res=".\nElle contient le personnage : Druide";
																								break;
																							default:
																								res=".\nErreur dans le switch d'affichage du type des illusionistes";
																								}
																						return res;}
																					else
																						return ".\nElle contient le personnage : "+pnj.getType();}
																				else
																					return "";
																			 else	return ""; }
		
		@Override
		public void mouseClicked (MouseEvent e)
			{
			/*----- Lecture de la position de la souris dans le dessin -----*/
			int x = e.getX();
			int y = e.getY();

			/*----- Recherche des coordonnées de la case du labyrinthe sur laquelle le joueur a cliqué -----*/
			int ligne = y / this.taille_case;
			int colonne = x / this.taille_case;

			/*----- On regarde si l'aventiruer est sur la case syr laquelle on vient de cliquer -----*/
			String msgAventurier = "";
			if (labyrinthe.getAventurier().getX() == ligne && labyrinthe.getAventurier().getY() == colonne)
				msgAventurier = "\nL'aventurier est sur cette case.";

			/*----- Etat de la case -----*/
			JOptionPane.showMessageDialog(this, "Vous avez cliqu� sur la case (" + ligne + "," + colonne +").\nC'est un "
												+ labyrinthe.getCase(ligne, colonne).getClassName()
												+ " de type " + labyrinthe.getCase(ligne, colonne).getType()
												+ ".\nCette case est � "+labyrinthe.getCase(ligne, colonne).getDistanceFin()+" cases de la fin"
												+ dispObj(labyrinthe.getCase(ligne, colonne).getContientObjet(),labyrinthe.getObjet(ligne, colonne))
												+ dispPiege(labyrinthe.getCase(ligne, colonne).getContientPiege(),labyrinthe.getPiege(ligne, colonne))
												+ dispPersonnage(labyrinthe.getCase(ligne, colonne).getContientPersonnage(),labyrinthe.getPersonnage(ligne, colonne))
												+ "." + msgAventurier);
			}
		
		public void deplacement(String direction)
		{
		int posX;
		int posY;
		boolean bouge = true;
		boolean desactive = true;
		int degatTour = 0;
		boolean dispMsg = false;
		String recapTour = "";
		boolean esquiveMonstre = false;
		int equipementCbtMonstre = 0; 
		if ( !(labyrinthe.getAventurier().getX()==21 && labyrinthe.getAventurier().getY()==1) ) 
		{
		switch ( direction) 
			{
			case "Haut":
				posX = labyrinthe.getAventurier().getX()-1;
				posY = labyrinthe.getAventurier().getY();
				if ( (posX==-1) && (posY==20) )					/* emp�che de se d�placer vert le haut si on est � l'entr�e du labyrinthe */
					{posX=posX+1;
					posY=posY+1;}
				break;
			case "Bas":
				posX = labyrinthe.getAventurier().getX()+1;
				posY = labyrinthe.getAventurier().getY();
				break;
			case "Droite":
				posX = labyrinthe.getAventurier().getX();
				posY = labyrinthe.getAventurier().getY()+1;
				break;
			case "Gauche":
				posX = labyrinthe.getAventurier().getX();
				posY = labyrinthe.getAventurier().getY()-1;
				break;
			default :
				System.out.println("Erreur dans le d�placement de l'aventurier (switch choix de direction)");
				posX = -100;
				posY = -100;
			}
		int i;
		/* effet des chaussures de course */
		if(labyrinthe.getCase(posX, posY).getType().equals("Terre"))
			for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
				{
				if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chaussures de course") )
					switch (direction)
					{
					case "Haut":
						if ( !(labyrinthe.getCase(posX-1, posY).getType().equals("Infranchissable")) )
							if ( !(posX==1 && posY==1) )					/* emp�che de se d�placer vert le haut si on est � l'entr�e du labyrinthe */
								posX = posX-1;
						break;
					case "Bas":
						if ( !(labyrinthe.getCase(posX+1, posY).getType().equals("Infranchissable")) )
							posX = posX+1;
						break;
					case "Droite":
						if ( !(labyrinthe.getCase(posX, posY+1).getType().equals("Infranchissable")) )
							posY = posY+1;
						break;
					case "Gauche":
						if ( !(labyrinthe.getCase(posX, posY-1).getType().equals("Infranchissable")) )
							posY = posY-1;
						break;
					default :
						System.out.println("Erreur dans le d�placement de l'aventurier (switch direction chaussure)");
					}
				}
		if (labyrinthe.getCase(posX, posY).getType().equals("Infranchissable"))
			bouge=false;
		else
			/* effet du poison */
			for (i=0;i<labyrinthe.getAventurier().getDebuff()[i].length();i++)
				{
				if (labyrinthe.getAventurier().getDebuff()[i].equals("Empoisonn�"))
					{
					degatTour = degatTour+1;
					}
				}
		/* si la case contient un pi�ge */
		if (labyrinthe.getCase(posX, posY).getContientPiege()==true)
			{if (labyrinthe.getPiege(posX, posY).getEstDesactive()==false)
				{
					dispMsg = true;
					int msg2=0;
					switch ( labyrinthe.getPiege(posX, posY).getType() ) 
					{
						case "Rocher" : 
							bouge = false;
							degatTour = degatTour+15;
							recapTour = "R�capitulatif du tour :\nVous n'avez pas pu avancer car un rocher se trouvait sur votre passage.";
							for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
								{
								if (labyrinthe.getAventurier().getInventaire()[i].equals("Pioche"))
									{
									degatTour = degatTour-15;
									recapTour = recapTour+"\nHeureusement vous poss�diez une pioche qui vous a permis de casser le rocher sans vous �puiser.";
									if ( (int)(Math.random()*(3-1+1)+1)==3 )
										{
										recapTour = recapTour+"\nVotre pioche s'est cass�e.";
										for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
											{
											if (labyrinthe.getAventurier().getInventaire()[i].equals("Pioche"))
												labyrinthe.getAventurier().setInventaire(i, "Vide");
											}
										}
									msg2=1;
									}
								else
									if ((recapTour.equals("R�capitulatif du tour :\nVous n'avez pas pu avancer car un rocher se trouvait sur votre passage.")) && msg2==0)				/* pour modifier recapTour une seule fois */
										recapTour = recapTour+"\nMalheureusement, vous ne poss�diez pas de pioche et casser ce rocher � la main vous a donc cout� 15 points de vie.";
								}
							break;
						case "Arbre" : 
							bouge = false;
							degatTour = degatTour+10;
							recapTour = "R�capitulatif du tour :\nVous n'avez pas pu avancer car un arbre se trouvait sur votre passage.";
							for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
								{
								if (labyrinthe.getAventurier().getInventaire()[i].equals("Hache"))
									{
									degatTour = degatTour-10;
									recapTour = recapTour+"\nHeureusement vous poss�diez une hache qui vous a permis de couper l'arbre sans vous �puiser.";
									if ( (int)(Math.random()*(3-1+1)+1)==3 )
										{
										recapTour = recapTour+"\nVotre hache s'est cass�e.";
										for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
											{
											if (labyrinthe.getAventurier().getInventaire()[i].equals("Hache"))
												labyrinthe.getAventurier().setInventaire(i, "Vide");
											}
										}
									msg2=1;
									}
								else
									if ((recapTour.equals("R�capitulatif du tour :\nVous n'avez pas pu avancer car un arbre se trouvait sur votre passage.")) && msg2==0)				/* pour modifier recapTour une seule fois */
										recapTour = recapTour+"\nMalheureusement, vous ne poss�diez pas de hache et couper cet arbre � la main vous a donc cout� 10 points de vie.";
								}
							break;
						case "Poison" : 
							for (i=0;i<labyrinthe.getAventurier().getDebuff().length;i++)
								{
								if (labyrinthe.getAventurier().getDebuff()[i].equals("Aucun"))
									{
									labyrinthe.getAventurier().setDebuff(i, "Empoisonn�");
									recapTour = recapTour+"R�capitulatif du tour :\nVous avez travers� un nuage de gaz. Vous �tes empoisonn�.";
									break;																	/* pour empoisonner une seule fois (au premier "Aucun") */
									}
								}

							break;
						case "Piques" : 
							for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
								{
								if (labyrinthe.getAventurier().getInventaire()[i].equals("Armure"))
									degatTour = degatTour+2;
								else
									{
									degatTour = degatTour+5;
									if (labyrinthe.getPiege(posX, posY).getEstVisible()==true)
										recapTour = recapTour+"R�capitulatif du tour :\nVous vous �tes empal� sur des piques. Vous avez perdu 5 points de vie.";
									else
										recapTour = recapTour+"R�capitulatif du tour :\nA�e! Des piques �taient cach�es sur cette case! Vous avez perdu 5 points de vie.";
									break;																	/* pour subir les d�gats une seule fois */											
									}
								}
							break; 
						case "Boulet" : 
							for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
								{
								if (labyrinthe.getAventurier().getInventaire()[i].equals("Boulet"))
									{
									recapTour = recapTour+"R�capitulatif du tour :\nUn boulet est pr�sent sur la case mais vous en avez d�j� un attach� � votre jambe.";
									desactive = false;
									break;																/* sortir du for */
									}
								if (labyrinthe.getAventurier().getInventaire()[i].equals("Vide"))
									{
									labyrinthe.getAventurier().setInventaire(i, "Boulet");
									recapTour = recapTour+"R�capitulatif du tour :\nUn boulet s'est attach� � vote jambe. Il est tr�s lourd mais ne semble pas vous emp�cher de marcher...";
									}
								break;																	/* pour recevoir le boulet une seule fois (au premier "Aucun") */
								}
							break;
						case "Monstre" : 
							int drop = (int)(Math.random()*(8-1+1)+1);
							int indexVide=-10;			/* comme �a renvoit erreur si le for pour obtenir sa valeur marche pas*/
							for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
								{
								if (labyrinthe.getAventurier().getInventaire()[i].equals("Cape d'invisibilit�"))
									{
									recapTour = recapTour+"R�capitulatif du tour :\nVotre cape d'invisibilit� vous a permis d'avancer sans attirer l'attention du monstre!";
									esquiveMonstre = true;
									desactive = false;
									}
								}
							for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
								if (labyrinthe.getAventurier().getInventaire()[i].equals("Vide"))
									{indexVide = i;
									break;}
							if (esquiveMonstre==false)
								{
								for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
									{
									if (labyrinthe.getAventurier().getInventaire()[i].equals("Chunchunmaru"))
										equipementCbtMonstre = equipementCbtMonstre+1;
									if (labyrinthe.getAventurier().getInventaire()[i].equals("Armure"))
										equipementCbtMonstre = equipementCbtMonstre+2;
									}
								/* donc equipementCbtMonstre = 0 -> l'aventurier n'est pas �quip�	equipementCbtMonstre = 1 -> l'aventurier est �quip� d'une �pee	2 -> d'une armure	3 -> d'une �p�e ET d'une armure */
								switch (equipementCbtMonstre) {
									case 0:
										degatTour = degatTour+5;
										recapTour = recapTour+"R�capitulatif du tour :\nVous avez perdu 5 points de vie en combattant un monstre.";
										if (((int)(Math.random()*(5-1+1)+1))>2)
											{
											desactive = false;
											bouge = false;
											recapTour = recapTour+"\nVous n'avez pas r�ussi � achever le monstre...";
											}
										else
											{
											recapTour = recapTour+"\nLe monstre est vaincu, bon d�barras!";
											if ( (((int)(Math.random()*(2-1+1)+1))==2) || (labyrinthe.getPiege(posX, posY).getMimic()==true) )
												{
												recapTour = recapTour+"\nQuelle chance, le monstre a fait tomber un objet!";
												int msg = 0;
												switch (drop) {
													/*  donne un objet dans la 1er case d'inventaire vide, sauf si l'aventurier poss�de d�j� cet objet */
													case 1:
														msg = 0;
														for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
															{
															if ( labyrinthe.getAventurier().getInventaire()[i].equals("Pioche") )
																msg = 1;
															}
														if (msg==0)
															{
															recapTour = recapTour+"\nVous avez obtenu l'objet: Pioche";
															labyrinthe.getAventurier().setInventaire(indexVide, "Pioche");
															}
														else
															recapTour = recapTour+"\nLe monstre a fait tomber une pioche mais vous poss�dez d�j� cet objet...";
														break;
													case 2:
														msg = 0;
														for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
															{
															if ( labyrinthe.getAventurier().getInventaire()[i].equals("Hache") )
																msg = 1;
															}
														if (msg==0)
															{
															recapTour = recapTour+"\nVous avez obtenu l'objet: Hache";
															labyrinthe.getAventurier().setInventaire(indexVide, "Hache");
															}
														else
															recapTour = recapTour+"\nLe monstre a fait tomber une hache mais vous poss�dez d�j� cet objet...";
														break;
													case 3:
														msg = 0;
														for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
															{
															if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chaussures de course") )
																msg = 1;
															}
														if (msg==0)
															{
															recapTour = recapTour+"\nVous avez obtenu l'objet: Chaussures de course";
															labyrinthe.getAventurier().setInventaire(indexVide, "Chaussures de course");
															}
														else
															recapTour = recapTour+"\nLe monstre a fait tomber des chaussures de course mais vous poss�dez d�j� cet objet...";
														break;
													case 4:
														msg = 0;
														for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
															{
															if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chunchunmaru") )
																msg = 1;
															}
														if (msg==0)
															{
															recapTour = recapTour+"\nVous avez obtenu l'objet: Chunchunmaru";
															labyrinthe.getAventurier().setInventaire(indexVide, "Chunchunmaru");
															}
														else
															recapTour = recapTour+"\nLe monstre a fait tomber Chunchunmaru mais vous poss�dez d�j� cet objet...";
														break;
													case 5:
														msg = 0;
														for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
															{
															if ( labyrinthe.getAventurier().getInventaire()[i].equals("Palmes") )
																msg = 1;
															}
														if (msg==0)
															{
															recapTour = recapTour+"\nVous avez obtenu l'objet: Palmes";
															labyrinthe.getAventurier().setInventaire(indexVide, "Palmes");
															}
														else
															recapTour = recapTour+"\nLe monstre a fait tomber des palmes mais vous poss�dez d�j� cet objet...";
														break;
													case 6:
														msg = 0;
														for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
															{
															if ( labyrinthe.getAventurier().getInventaire()[i].equals("Armure") )
																msg = 1;
															}
														if (msg==0)
															{
															recapTour = recapTour+"\nVous avez obtenu l'objet: Armure";
															labyrinthe.getAventurier().setInventaire(indexVide, "Armure");
															}
														else
															recapTour = recapTour+"\nLe monstre a fait tomber une armure mais vous poss�dez d�j� cet objet...";
														break;
													case 7:
														msg = 0;
														for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
															{
															if ( labyrinthe.getAventurier().getInventaire()[i].equals("Cape d'invisibilit�") )
																msg = 1;
															}
														if (msg==0)
															{
															recapTour = recapTour+"\nVous avez obtenu l'objet: Cape d'invisibilit�";
															labyrinthe.getAventurier().setInventaire(indexVide, "Cape d'invisibilit�");
															}
														else
															recapTour = recapTour+"\nLe monstre a fait tomber une cape d'invisibilit� mais vous poss�dez d�j� cet objet...";
														break;
													case 8:
														msg = 0;
														for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
															{
															if ( labyrinthe.getAventurier().getInventaire()[i].equals("totem d'immortalit�") )
																msg = 1;
															}
														if (msg==0)
															{
															recapTour = recapTour+"\nVous avez obtenu l'objet: totem d'immortalit�";
															labyrinthe.getAventurier().setInventaire(indexVide, "totem d'immortalit�");
															}
														else
															recapTour = recapTour+"\nLe monstre a fait tomber un totem d'immortalit� mais vous poss�dez d�j� cet objet...";
														break;
													default:
														System.out.println("Erreur dans le d�placement de l'aventurier (switch drop monstre)");
												}
												}
											}
										break;
									case 1:
										degatTour = degatTour+5;
										recapTour = recapTour+"R�capitulatif du tour :\nVous avez perdu 5 points de vie en combattant un monstre.";
										if (((int)(Math.random()*(5-1+1)+1))>4)
											{
											desactive = false;
											bouge = false;
											recapTour = recapTour+"\nVous n'avez pas r�ussi � achever le monstre...";
											}
										else
											{
											recapTour = recapTour+"\nGr�ce � votre �p�e, vous n'avez fait qu'une bouch�e du monstre!";
											if ( (((int)(Math.random()*(2-1+1)+1))==2) || (labyrinthe.getPiege(posX, posY).getMimic()==true) )
												{
												recapTour = recapTour+"\nQuelle chance, le monstre a fait tomber un objet!";
												int msg = 0;
												switch (drop) {
													/*  donne un objet dans la 1er case d'inventaire vide, sauf si l'aventurier poss�de d�j� cet objet */
												case 1:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Pioche") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Pioche";
														labyrinthe.getAventurier().setInventaire(indexVide, "Pioche");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber une pioche mais vous poss�dez d�j� cet objet...";
													break;
												case 2:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Hache") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Hache";
														labyrinthe.getAventurier().setInventaire(indexVide, "Hache");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber une hache mais vous poss�dez d�j� cet objet...";
													break;
												case 3:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chaussures de course") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Chaussures de course";
														labyrinthe.getAventurier().setInventaire(indexVide, "Chaussures de course");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber des chaussures de course mais vous poss�dez d�j� cet objet...";
													break;
												case 4:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chunchunmaru") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Chunchunmaru";
														labyrinthe.getAventurier().setInventaire(indexVide, "Chunchunmaru");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber Chunchunmaru mais vous poss�dez d�j� cet objet...";
													break;
												case 5:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Palmes") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Palmes";
														labyrinthe.getAventurier().setInventaire(indexVide, "Palmes");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber des palmes mais vous poss�dez d�j� cet objet...";
													break;
												case 6:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Armure") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Armure";
														labyrinthe.getAventurier().setInventaire(indexVide, "Armure");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber une armure mais vous poss�dez d�j� cet objet...";
													break;
												case 7:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Cape d'invisibilit�") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Cape d'invisibilit�";
														labyrinthe.getAventurier().setInventaire(indexVide, "Cape d'invisibilit�");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber une cape d'invisibilit� mais vous poss�dez d�j� cet objet...";
													break;
												case 8:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("totem d'immortalit�") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: totem d'immortalit�";
														labyrinthe.getAventurier().setInventaire(indexVide, "totem d'immortalit�");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber un totem d'immortalit� mais vous poss�dez d�j� cet objet...";
													break;
													default:
														System.out.println("Erreur dans le d�placement de l'aventurier (switch drop monstre)");
												}
												}
											}
										break;
									case 2:
										degatTour = degatTour+1;
										recapTour = recapTour+"R�capitulatif du tour :\nCapara�onn� dans votre armure, vous n'avez perdu qu'un point de vie en combattant un monstre.";
										if ( (((int)(Math.random()*(2-1+1)+1))==2) || (labyrinthe.getPiege(posX, posY).getMimic()==true) )
											{
											desactive = false;
											bouge = false;
											recapTour = recapTour+"\nVous n'avez pas r�ussi � achever le monstre...";
											}
										else
											{
											recapTour = recapTour+"\nLe monstre est vaincu, bon d�barras!";
											if (((int)(Math.random()*(2-1+1)+1))==2)
												{
												recapTour = recapTour+"\nQuelle chance, le monstre a fait tomber un objet!";
												int msg = 0;
												switch (drop) {
													/*  donne un objet dans la 1er case d'inventaire vide, sauf si l'aventurier poss�de d�j� cet objet */
												case 1:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Pioche") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Pioche";
														labyrinthe.getAventurier().setInventaire(indexVide, "Pioche");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber une pioche mais vous poss�dez d�j� cet objet...";
													break;
												case 2:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Hache") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Hache";
														labyrinthe.getAventurier().setInventaire(indexVide, "Hache");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber une hache mais vous poss�dez d�j� cet objet...";
													break;
												case 3:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chaussures de course") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Chaussures de course";
														labyrinthe.getAventurier().setInventaire(indexVide, "Chaussures de course");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber des chaussures de course mais vous poss�dez d�j� cet objet...";
													break;
												case 4:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chunchunmaru") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Chunchunmaru";
														labyrinthe.getAventurier().setInventaire(indexVide, "Chunchunmaru");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber Chunchunmaru mais vous poss�dez d�j� cet objet...";
													break;
												case 5:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Palmes") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Palmes";
														labyrinthe.getAventurier().setInventaire(indexVide, "Palmes");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber des palmes mais vous poss�dez d�j� cet objet...";
													break;
												case 6:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Armure") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Armure";
														labyrinthe.getAventurier().setInventaire(indexVide, "Armure");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber une armure mais vous poss�dez d�j� cet objet...";
													break;
												case 7:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Cape d'invisibilit�") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Cape d'invisibilit�";
														labyrinthe.getAventurier().setInventaire(indexVide, "Cape d'invisibilit�");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber une cape d'invisibilit� mais vous poss�dez d�j� cet objet...";
													break;
												case 8:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("totem d'immortalit�") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: totem d'immortalit�";
														labyrinthe.getAventurier().setInventaire(indexVide, "totem d'immortalit�");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber un totem d'immortalit� mais vous poss�dez d�j� cet objet...";
													break;
													default:
														System.out.println("Erreur dans le d�placement de l'aventurier (switch drop monstre)");
												}
												}
											}
										break;
									case 3:
										degatTour = degatTour+1;
										recapTour = recapTour+"R�capitulatif du tour :\nCapara�onn� dans votre armure, vous n'avez perdu qu'un point de vie en combattant un monstre.";
										if (((int)(Math.random()*(5-1+1)+1))>4)
											{
											desactive = false;
											bouge = false;
											recapTour = recapTour+"\nVous n'avez pas r�ussi � achever le monstre...";
											}
										else
											{
											recapTour = recapTour+"\nGr�ce � votre �p�e, vous n'avez fait qu'une bouch�e du monstre!";
											if ( (((int)(Math.random()*(2-1+1)+1))==2) || (labyrinthe.getPiege(posX, posY).getMimic()==true) )
												{
												recapTour = recapTour+"\nQuelle chance, le monstre a fait tomber un objet!";
												int msg = 0;
												switch (drop) {
													/*  donne un objet dans la 1er case d'inventaire vide, sauf si l'aventurier poss�de d�j� cet objet */
												case 1:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Pioche") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Pioche";
														labyrinthe.getAventurier().setInventaire(indexVide, "Pioche");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber une pioche mais vous poss�dez d�j� cet objet...";
													break;
												case 2:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Hache") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Hache";
														labyrinthe.getAventurier().setInventaire(indexVide, "Hache");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber une hache mais vous poss�dez d�j� cet objet...";
													break;
												case 3:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chaussures de course") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Chaussures de course";
														labyrinthe.getAventurier().setInventaire(indexVide, "Chaussures de course");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber des chaussures de course mais vous poss�dez d�j� cet objet...";
													break;
												case 4:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chunchunmaru") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Chunchunmaru";
														labyrinthe.getAventurier().setInventaire(indexVide, "Chunchunmaru");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber Chunchunmaru mais vous poss�dez d�j� cet objet...";
													break;
												case 5:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Palmes") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Palmes";
														labyrinthe.getAventurier().setInventaire(indexVide, "Palmes");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber des palmes mais vous poss�dez d�j� cet objet...";
													break;
												case 6:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Armure") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Armure";
														labyrinthe.getAventurier().setInventaire(indexVide, "Armure");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber une armure mais vous poss�dez d�j� cet objet...";
													break;
												case 7:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Cape d'invisibilit�") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Cape d'invisibilit�";
														labyrinthe.getAventurier().setInventaire(indexVide, "Cape d'invisibilit�");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber une cape d'invisibilit� mais vous poss�dez d�j� cet objet...";
													break;
												case 8:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("totem d'immortalit�") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: totem d'immortalit�";
														labyrinthe.getAventurier().setInventaire(indexVide, "totem d'immortalit�");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber un totem d'immortalit� mais vous poss�dez d�j� cet objet...";
													break;
													default:
														System.out.println("Erreur dans le d�placement de l'aventurier (switch drop monstre)");
												}
												}
											}
										break;
									default:
										System.out.println("Erreur dans le d�placement de l'aventurier (switch monstre)");
								}
								}
							break;
						case "Boss" : 									/* TDLR pareil qu'un monstre mais tape plus fort et plus de chance de rester en vie */
							int dropB = (int)(Math.random()*(4-1+1)+1);
							int indexVideB=-10;			/* comme �a renvoit erreur si le for pour obtenir sa valeur marche pas*/
							for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
								{
								if (labyrinthe.getAventurier().getInventaire()[i].equals("Cape d'invisibilit�"))
									{
									recapTour = recapTour+"R�capitulatif du tour :\nVotre cape d'invisibilit� vous a permis d'avancer sans attirer l'attention du boss!";
									esquiveMonstre = true;
									desactive = false;
									}
								}
							for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
								if (labyrinthe.getAventurier().getInventaire()[i].equals("Vide"))
									{indexVideB = i;
									break;}
							if (esquiveMonstre==false)
								{
								for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
									{
									if (labyrinthe.getAventurier().getInventaire()[i].equals("Chunchunmaru"))
										equipementCbtMonstre = equipementCbtMonstre+1;
									if (labyrinthe.getAventurier().getInventaire()[i].equals("Armure"))
										equipementCbtMonstre = equipementCbtMonstre+2;
									}
								/* donc equipementCbtMonstre = 0 -> l'aventurier n'est pas �quip�	equipementCbtMonstre = 1 -> l'aventurier est �quip� d'une �pee	2 -> d'une armure	3 -> d'une �p�e ET d'une armure */
								switch (equipementCbtMonstre) {
									case 0:
										degatTour = degatTour+15;
										recapTour = recapTour+"R�capitulatif du tour :\nVous avez perdu 15 points de vie en combattant un boss.";
										if (((int)(Math.random()*(5-1+1)+1))>1)
											{
											desactive = false;
											bouge = false;
											recapTour = recapTour+"\nVous n'avez pas r�ussi � achever le boss...";
											}
										else
											{
											recapTour = recapTour+"\nBavo! Le boss est vaincu!\nLe boss a fait tomber un objet!";
											int msg=0;
											switch (dropB) {
												case 1:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chaussures de course") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Chaussures de course";
														labyrinthe.getAventurier().setInventaire(indexVideB, "Chaussures de course");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber des chaussures de course mais vous poss�dez d�j� cet objet...";
													break;
												case 2:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("Cape d'invisibilit�") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: Cape d'invisibilit�";
														labyrinthe.getAventurier().setInventaire(indexVideB, "Cape d'invisibilit�");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber des une cape d'invisibilit� mais vous poss�dez d�j� cet objet...";
													break;
												case 3:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("totem d'immortalit�") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet: totem d'immortalit�";
														labyrinthe.getAventurier().setInventaire(indexVideB, "totem d'immortalit�");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber des un totem d'immortalit� mais vous poss�dez d�j� cet objet...";
													break;
												case 4:
													msg = 0;
													for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
														{
														if ( labyrinthe.getAventurier().getInventaire()[i].equals("1up") )
															msg = 1;
														}
													if (msg==0)
														{
														recapTour = recapTour+"\nVous avez obtenu l'objet l�gendaire 1up !!";
														labyrinthe.getAventurier().setInventaire(indexVideB, "1up");
														}
													else
														recapTour = recapTour+"\nLe monstre a fait tomber l'objet 1up mais vous poss�dez d�j� cet objet...";
													break;
												default:
													System.out.println("Erreur dans le d�placement de l'aventurier (switch drop boss)");
											}
											}
										break;
									case 1:
										degatTour = degatTour+15;
										recapTour = recapTour+"R�capitulatif du tour :\nVous avez perdu 15 points de vie en combattant un boss.";
										if (((int)(Math.random()*(5-1+1)+1))>3)
											{
											desactive = false;
											bouge = false;
											recapTour = recapTour+"\nVous n'avez pas r�ussi � achever le boss...";
											}
										else
											{
											recapTour = recapTour+"\nBavo! Vous avez taill� le boss en pi�ce avec vote �p�e!\nLe boss a fait tomber un objet!";
											int msg=0;
											switch (dropB) {
											case 1:
												msg = 0;
												for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
													{
													if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chaussures de course") )
														msg = 1;
													}
												if (msg==0)
													{
													recapTour = recapTour+"\nVous avez obtenu l'objet: Chaussures de course";
													labyrinthe.getAventurier().setInventaire(indexVideB, "Chaussures de course");
													}
												else
													recapTour = recapTour+"\nLe monstre a fait tomber des chaussures de course mais vous poss�dez d�j� cet objet...";
												break;
											case 2:
												msg = 0;
												for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
													{
													if ( labyrinthe.getAventurier().getInventaire()[i].equals("Cape d'invisibilit�") )
														msg = 1;
													}
												if (msg==0)
													{
													recapTour = recapTour+"\nVous avez obtenu l'objet: Cape d'invisibilit�";
													labyrinthe.getAventurier().setInventaire(indexVideB, "Cape d'invisibilit�");
													}
												else
													recapTour = recapTour+"\nLe monstre a fait tomber des une cape d'invisibilit� mais vous poss�dez d�j� cet objet...";
												break;
											case 3:
												msg = 0;
												for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
													{
													if ( labyrinthe.getAventurier().getInventaire()[i].equals("totem d'immortalit�") )
														msg = 1;
													}
												if (msg==0)
													{
													recapTour = recapTour+"\nVous avez obtenu l'objet: totem d'immortalit�";
													labyrinthe.getAventurier().setInventaire(indexVideB, "totem d'immortalit�");
													}
												else
													recapTour = recapTour+"\nLe monstre a fait tomber des un totem d'immortalit� mais vous poss�dez d�j� cet objet...";
												break;
											case 4:
												msg = 0;
												for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
													{
													if ( labyrinthe.getAventurier().getInventaire()[i].equals("1up") )
														msg = 1;
													}
												if (msg==0)
													{
													recapTour = recapTour+"\nVous avez obtenu l'objet l�gendaire 1up !!";
													labyrinthe.getAventurier().setInventaire(indexVideB, "1up");
													}
												else
													recapTour = recapTour+"\nLe monstre a fait tomber l'objet 1up mais vous poss�dez d�j� cet objet...";
												break;
											default:
												System.out.println("Erreur dans le d�placement de l'aventurier (switch drop boss)");
											}
											}
										break;
									case 2:
										degatTour = degatTour+5;
										recapTour = recapTour+"R�capitulatif du tour :\nCapara�onn� dans votre armure, vous n'avez perdu que 5 point de vie en combattant un boss.";
										if (((int)(Math.random()*(5-1+1)+1))>1)
											{
											desactive = false;
											bouge = false;
											recapTour = recapTour+"\nVous n'avez pas r�ussi � achever le boss...";
											}
										else
											{
											recapTour = recapTour+"\nBavo! Le boss est vaincu!\nLe boss a fait tomber un objet!";
											int msg=0;
											switch (dropB) {
											case 1:
												msg = 0;
												for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
													{
													if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chaussures de course") )
														msg = 1;
													}
												if (msg==0)
													{
													recapTour = recapTour+"\nVous avez obtenu l'objet: Chaussures de course";
													labyrinthe.getAventurier().setInventaire(indexVideB, "Chaussures de course");
													}
												else
													recapTour = recapTour+"\nLe monstre a fait tomber des chaussures de course mais vous poss�dez d�j� cet objet...";
												break;
											case 2:
												msg = 0;
												for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
													{
													if ( labyrinthe.getAventurier().getInventaire()[i].equals("Cape d'invisibilit�") )
														msg = 1;
													}
												if (msg==0)
													{
													recapTour = recapTour+"\nVous avez obtenu l'objet: Cape d'invisibilit�";
													labyrinthe.getAventurier().setInventaire(indexVideB, "Cape d'invisibilit�");
													}
												else
													recapTour = recapTour+"\nLe monstre a fait tomber des une cape d'invisibilit� mais vous poss�dez d�j� cet objet...";
												break;
											case 3:
												msg = 0;
												for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
													{
													if ( labyrinthe.getAventurier().getInventaire()[i].equals("totem d'immortalit�") )
														msg = 1;
													}
												if (msg==0)
													{
													recapTour = recapTour+"\nVous avez obtenu l'objet: totem d'immortalit�";
													labyrinthe.getAventurier().setInventaire(indexVideB, "totem d'immortalit�");
													}
												else
													recapTour = recapTour+"\nLe monstre a fait tomber des un totem d'immortalit� mais vous poss�dez d�j� cet objet...";
												break;
											case 4:
												msg = 0;
												for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
													{
													if ( labyrinthe.getAventurier().getInventaire()[i].equals("1up") )
														msg = 1;
													}
												if (msg==0)
													{
													recapTour = recapTour+"\nVous avez obtenu l'objet l�gendaire 1up !!";
													labyrinthe.getAventurier().setInventaire(indexVideB, "1up");
													}
												else
													recapTour = recapTour+"\nLe monstre a fait tomber l'objet 1up mais vous poss�dez d�j� cet objet...";
												break;
											default:
												System.out.println("Erreur dans le d�placement de l'aventurier (switch drop boss)");
											}
											}
										break;
									case 3:
										degatTour = degatTour+5;
										recapTour = recapTour+"R�capitulatif du tour :\nCapara�onn� dans votre armure, vous n'avez perdu que 5 point de vie en combattant un boss.";
										if (((int)(Math.random()*(5-1+1)+1))>2)
											{
											desactive = false;
											bouge = false;
											recapTour = recapTour+"\nVous n'avez pas r�ussi � achever le boss...";
											}
										else
											{
											recapTour = recapTour+"\nBavo! Vous avez taill� le boss en pi�ce avec vote �p�e!\nLe boss a fait tomber un objet!";
											int msg=0;
											switch (dropB) {
											case 1:
												msg = 0;
												for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
													{
													if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chaussures de course") )
														msg = 1;
													}
												if (msg==0)
													{
													recapTour = recapTour+"\nVous avez obtenu l'objet: Chaussures de course";
													labyrinthe.getAventurier().setInventaire(indexVideB, "Chaussures de course");
													}
												else
													recapTour = recapTour+"\nLe monstre a fait tomber des chaussures de course mais vous poss�dez d�j� cet objet...";
												break;
											case 2:
												msg = 0;
												for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
													{
													if ( labyrinthe.getAventurier().getInventaire()[i].equals("Cape d'invisibilit�") )
														msg = 1;
													}
												if (msg==0)
													{
													recapTour = recapTour+"\nVous avez obtenu l'objet: Cape d'invisibilit�";
													labyrinthe.getAventurier().setInventaire(indexVideB, "Cape d'invisibilit�");
													}
												else
													recapTour = recapTour+"\nLe monstre a fait tomber des une cape d'invisibilit� mais vous poss�dez d�j� cet objet...";
												break;
											case 3:
												msg = 0;
												for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
													{
													if ( labyrinthe.getAventurier().getInventaire()[i].equals("totem d'immortalit�") )
														msg = 1;
													}
												if (msg==0)
													{
													recapTour = recapTour+"\nVous avez obtenu l'objet: totem d'immortalit�";
													labyrinthe.getAventurier().setInventaire(indexVideB, "totem d'immortalit�");
													}
												else
													recapTour = recapTour+"\nLe monstre a fait tomber des un totem d'immortalit� mais vous poss�dez d�j� cet objet...";
												break;
											case 4:
												msg = 0;
												for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
													{
													if ( labyrinthe.getAventurier().getInventaire()[i].equals("1up") )
														msg = 1;
													}
												if (msg==0)
													{
													recapTour = recapTour+"\nVous avez obtenu l'objet l�gendaire 1up !!";
													labyrinthe.getAventurier().setInventaire(indexVideB, "1up");
													}
												else
													recapTour = recapTour+"\nLe monstre a fait tomber l'objet 1up mais vous poss�dez d�j� cet objet...";
												break;
											default:
												System.out.println("Erreur dans le d�placement de l'aventurier (switch drop boss)");
											}
											}
										break;
									default:
										System.out.println("Erreur dans le d�placement de l'aventurier (switch boss)");
								}
								}
							break;
						case "Abime" : 
								recapTour = recapTour+"Si vous plongez longtemps votre regard dans l'ab�me, l'ab�me vous regarde aussi.\n(vous �tes mort en tombant dans l'ab�me invisible)";
								labyrinthe.getAventurier().setEnergie(0);
							break;
						default:
							System.out.println("Erreur dans le d�placement de l'aventurier (switch pi�ge)");							
					}
					if (desactive==true)
						labyrinthe.getPiege(posX, posY).setEstDesactive(true);
				}
			}
		/* fin pi�ge */
		/* si la case contient un objet */
		if (labyrinthe.getCase(posX, posY).getContientObjet()==true)
			{
			if (labyrinthe.getObjet(posX, posY).getEstDesactive()==false)
				{
				int indexVideO = -10;
				for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
					if (labyrinthe.getAventurier().getInventaire()[i].equals("Vide"))
						{indexVideO = i;
						break;}
				dispMsg = true;
				desactive = true;
				if (labyrinthe.getObjet(posX, posY).getEstVisible()==true)
					{
					recapTour = recapTour+"\nVous avez trouv� un objet par terre.";
					int msg3=0;
					switch ( labyrinthe.getObjet(posX, posY).getType() ) 
					{
					case "Pioche":
						msg3 = 0;
						for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
							{
							if ( labyrinthe.getAventurier().getInventaire()[i].equals("Pioche") )
								msg3 = 1;
							}
						if (msg3==0)
							{
							recapTour = recapTour+"\nVous avez obtenu l'objet Pioche";
							labyrinthe.getAventurier().setInventaire(indexVideO, "Pioche");
							}
						else
							{
							recapTour = recapTour+"\nVous avez trouv� une pioche mais vous poss�dez d�j� cet objet...";		
							desactive = false;
							}
						break;
					case "Hache":
						msg3 = 0;
						for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
							{
							if ( labyrinthe.getAventurier().getInventaire()[i].equals("Hache") )
								msg3 = 1;
							}
						if (msg3==0)
							{
							recapTour = recapTour+"\nVous avez obtenu l'objet Hache";
							labyrinthe.getAventurier().setInventaire(indexVideO, "Hache");
							}
						else
							{
							recapTour = recapTour+"\nVous avez trouv� une hache mais vous poss�dez d�j� cet objet...";		
							desactive = false;
							}
						break;
					case "Chaussures de course":
						msg3 = 0;
						for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
							{
							if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chaussures de course") )
								msg3 = 1;
							}
						if (msg3==0)
							{
							recapTour = recapTour+"\nVous avez obtenu l'objet Chaussures de course";
							labyrinthe.getAventurier().setInventaire(indexVideO, "Chaussures de course");
							}
						else
							{
							recapTour = recapTour+"\nVous avez trouv� des chaussures de course mais vous poss�dez d�j� cet objet...";		
							desactive = false;
							}
						break;
					case "Chunchunmaru":
						msg3 = 0;
						for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
							{
							if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chunchunmaru") )
								msg3 = 1;
							}
						if (msg3==0)
							{
							recapTour = recapTour+"\nVous avez obtenu l'objet Chunchunmaru";
							jouerMusique(System.getProperty("user.dir")+"\\source\\chunchunmaru.wav");
							labyrinthe.getAventurier().setInventaire(indexVideO, "Chunchunmaru");
							}
						else
							{
							recapTour = recapTour+"\nVous avez trouv� Chunchunmaru mais vous poss�dez d�j� cet objet...";		
							desactive = false;
							}
						break;
					case "Palmes":
						msg3 = 0;
						for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
							{
							if ( labyrinthe.getAventurier().getInventaire()[i].equals("Palmes") )
								msg3 = 1;
							}
						if (msg3==0)
							{
							recapTour = recapTour+"\nVous avez obtenu l'objet Palmes";
							labyrinthe.getAventurier().setInventaire(indexVideO, "Palmes");
							}
						else
							{
							recapTour = recapTour+"\nVous avez trouv� Palmes mais vous poss�dez d�j� cet objet...";		
							desactive = false;
							}
						break;
					case "Armure":
						msg3 = 0;
						for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
							{
							if ( labyrinthe.getAventurier().getInventaire()[i].equals("Armure") )
								msg3 = 1;
							}
						if (msg3==0)
							{
							recapTour = recapTour+"\nVous avez obtenu l'objet Armure";
							labyrinthe.getAventurier().setInventaire(indexVideO, "Armure");
							}
						else
							{
							recapTour = recapTour+"\nVous avez trouv� Armure mais vous poss�dez d�j� cet objet...";		
							desactive = false;
							}
						break;
					case "Cape d'invisibilit�":
						msg3 = 0;
						for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
							{
							if ( labyrinthe.getAventurier().getInventaire()[i].equals("Cape d'invisibilit�") )
								msg3 = 1;
							}
						if (msg3==0)
							{
							recapTour = recapTour+"\nVous avez obtenu l'objet Cape d'invisibilit�";
							labyrinthe.getAventurier().setInventaire(indexVideO, "Cape d'invisibilit�");
							}
						else
							{
							recapTour = recapTour+"\nVous avez trouv� une cape d'invisibilit� mais vous poss�dez d�j� cet objet...";		
							desactive = false;
							}
						break;
					case "totem d'immortalit�":
						msg3 = 0;
						for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
							{
							if ( labyrinthe.getAventurier().getInventaire()[i].equals("totem d'immortalit�") )
								msg3 = 1;
							}
						if (msg3==0)
							{
							recapTour = recapTour+"\nVous avez obtenu l'objet totem d'immortalit�";
							labyrinthe.getAventurier().setInventaire(indexVideO, "totem d'immortalit�");
							}
						else
							{
							recapTour = recapTour+"\nVous avez trouv� un totem d'immortalit� mais vous poss�dez d�j� cet objet...";		
							labyrinthe.getObjet(posX, posY).setEstVisible(true);		
							desactive = false;
							}
						break;
					case "Antidote":
						msg3=0;
						for (i=0;i<labyrinthe.getAventurier().getDebuff().length;i++)
							{
							if ( labyrinthe.getAventurier().getDebuff()[i].equals("Empoisonn�") )
								{
								labyrinthe.getAventurier().setDebuff(i,"Aucun");
								recapTour = recapTour+"\nVous avez trouv� un antidote.\nVous n'�tes plus empoisonn�";
								msg3=1;
								break;
								}
							}
						if ( msg3==0 )
							{
							recapTour = recapTour+"\nVous avez trouv� un antidote mais vous n'�tes pas empoisonn�.";
							desactive=false;
							}
						break;
					case "Carte":
						recapTour = recapTour+"\nVous avez trouv� une carte du labyrinthe.\nLes pi�ges cach�s ont �t� r�v�l�s!";
						for (int h=0; h < labyrinthe.getTaille(); h++)
							for (int j=0; j < labyrinthe.getTaille(); j++)
								if ( labyrinthe.getCase(h, j).getContientPiege()==true )
									labyrinthe.getPiege(h, j).setEstVisible(true);
						break;
					case "Coeur":
						recapTour = recapTour+"\nVous avez trouv� un coeur. Vous avez regagn� 10 points de vie.";
						labyrinthe.getAventurier().setEnergie(labyrinthe.getAventurier().getEnergie()+10);
						break;
					case "Pomme dor�e":
						recapTour = recapTour+"\nVous avez trouv� une pomme dor�e. Vous avez regagn� 20 points de vie.";
						labyrinthe.getAventurier().setEnergie(labyrinthe.getAventurier().getEnergie()+20);
						break;
					case "Pomme dor�e enchant�":
						recapTour = recapTour+"\nVous avez trouv� une pomme dor�e enchant�. Vous avez regagn� 30 points de vie.";
						labyrinthe.getAventurier().setEnergie(labyrinthe.getAventurier().getEnergie()+30);
						break;
					default:
						System.out.println("Erreur dans le d�placement de l'aventurier (switch objet vrai)");
					}
					}
				else
					if (((int)(Math.random()*(5-1+1)+1))==5)
						{
						recapTour = recapTour+"\nCe coffre est affam� !! Les apparences sont parfois trompeuses...";
						degatTour = degatTour+5;
						bouge = false;
						labyrinthe.setPiege(posX, posY, new Piege("Monstre", "Enl�ve 5 points de vie quand attaqu� et peut rester en vie. Laisse forc�ment tomber un objet � sa mort",true));
						labyrinthe.getCase(posX,posY).setContientPiege(true);
						labyrinthe.getPiege(posX, posY).setMimic(true);
						}
					else
						{
						jouerMusique(System.getProperty("user.dir")+"\\source\\item.wav");
						recapTour = recapTour+"\nVous avez trouv� un objet en ouvrant le coffre.";
						int msg3=0;
						switch ( labyrinthe.getObjet(posX, posY).getType() ) 
						{
						case "Pioche":
							msg3 = 0;
							for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
								{
								if ( labyrinthe.getAventurier().getInventaire()[i].equals("Pioche") )
									msg3 = 1;
								}
							if (msg3==0)
								{
								recapTour = recapTour+"\nVous avez obtenu l'objet Pioche";
								labyrinthe.getAventurier().setInventaire(indexVideO, "Pioche");
								}
							else
								{
								recapTour = recapTour+"\nVous avez trouv� une pioche mais vous poss�dez d�j� cet objet...";		
								labyrinthe.getObjet(posX, posY).setEstVisible(true);
								desactive = false;
								}
							break;
						case "Hache":
							msg3 = 0;
							for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
								{
								if ( labyrinthe.getAventurier().getInventaire()[i].equals("Hache") )
									msg3 = 1;
								}
							if (msg3==0)
								{
								recapTour = recapTour+"\nVous avez obtenu l'objet Hache";
								labyrinthe.getAventurier().setInventaire(indexVideO, "Hache");
								}
							else
								{
								recapTour = recapTour+"\nVous avez trouv� une hache mais vous poss�dez d�j� cet objet...";		
								labyrinthe.getObjet(posX, posY).setEstVisible(true);		
								desactive = false;
								}
							break;
						case "Chaussures de course":
							msg3 = 0;
							for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
								{
								if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chaussures de course") )
									msg3 = 1;
								}
							if (msg3==0)
								{
								recapTour = recapTour+"\nVous avez obtenu l'objet Chaussures de course";
								labyrinthe.getAventurier().setInventaire(indexVideO, "Chaussures de course");
								}
							else
								{
								recapTour = recapTour+"\nVous avez trouv� des chaussures de course mais vous poss�dez d�j� cet objet...";		
								labyrinthe.getObjet(posX, posY).setEstVisible(true);		
								desactive = false;
								}
							break;
						case "Chunchunmaru":
							msg3 = 0;
							for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
								{
								if ( labyrinthe.getAventurier().getInventaire()[i].equals("Chunchunmaru") )
									msg3 = 1;
								}
							if (msg3==0)
								{
								recapTour = recapTour+"\nVous avez obtenu l'objet Chunchunmaru";
								labyrinthe.getAventurier().setInventaire(indexVideO, "Chunchunmaru");
								}
							else
								{
								recapTour = recapTour+"\nVous avez trouv� Chunchunmaru mais vous poss�dez d�j� cet objet...";		
								labyrinthe.getObjet(posX, posY).setEstVisible(true);		
								desactive = false;
								}
							break;
						case "Palmes":
							msg3 = 0;
							for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
								{
								if ( labyrinthe.getAventurier().getInventaire()[i].equals("Palmes") )
									msg3 = 1;
								}
							if (msg3==0)
								{
								recapTour = recapTour+"\nVous avez obtenu l'objet Palmes";
								labyrinthe.getAventurier().setInventaire(indexVideO, "Palmes");
								}
							else
								{
								recapTour = recapTour+"\nVous avez trouv� Palmes mais vous poss�dez d�j� cet objet...";		
								labyrinthe.getObjet(posX, posY).setEstVisible(true);		
								desactive = false;
								}
							break;
						case "Armure":
							msg3 = 0;
							for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
								{
								if ( labyrinthe.getAventurier().getInventaire()[i].equals("Armure") )
									msg3 = 1;
								}
							if (msg3==0)
								{
								recapTour = recapTour+"\nVous avez obtenu l'objet Armure";
								labyrinthe.getAventurier().setInventaire(indexVideO, "Armure");
								}
							else
								{
								recapTour = recapTour+"\nVous avez trouv� Armure mais vous poss�dez d�j� cet objet...";			
								labyrinthe.getObjet(posX, posY).setEstVisible(true);	
								desactive = false;
								}
							break;
						case "Cape d'invisibilit�":
							msg3 = 0;
							for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
								{
								if ( labyrinthe.getAventurier().getInventaire()[i].equals("Cape d'invisibilit�") )
									msg3 = 1;
								}
							if (msg3==0)
								{
								recapTour = recapTour+"\nVous avez obtenu l'objet Cape d'invisibilit�";
								labyrinthe.getAventurier().setInventaire(indexVideO, "Cape d'invisibilit�");
								}
							else
								{
								recapTour = recapTour+"\nVous avez trouv� une cape d'invisibilit� mais vous poss�dez d�j� cet objet...";		
								labyrinthe.getObjet(posX, posY).setEstVisible(true);		
								desactive = false;
								}
							break;
						case "totem d'immortalit�":
							msg3 = 0;
							for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
								{
								if ( labyrinthe.getAventurier().getInventaire()[i].equals("totem d'immortalit�") )
									msg3 = 1;
								}
							if (msg3==0)
								{
								recapTour = recapTour+"\nVous avez obtenu l'objet totem d'immortalit�";
								labyrinthe.getAventurier().setInventaire(indexVideO, "totem d'immortalit�");
								}
							else
								{
								recapTour = recapTour+"\nVous avez trouv� un totem d'immortalit� mais vous poss�dez d�j� cet objet...";		
								labyrinthe.getObjet(posX, posY).setEstVisible(true);		
								desactive = false;
								}
							break;
						case "Antidote":
							msg3=0;
							for (i=0;i<labyrinthe.getAventurier().getDebuff().length;i++)
								{
								if ( labyrinthe.getAventurier().getDebuff()[i].equals("Empoisonn�") )
									{
									labyrinthe.getAventurier().setDebuff(i,"Aucun");
									recapTour = recapTour+"\nVous avez trouv� un antidote.\nVous n'�tes plus empoisonn�";
									msg3=1;
									break;
									}
								}
							if ( msg3==0 )
								{
								recapTour = recapTour+"\nVous avez trouv� un antidote mais vous n'�tes pas empoisonn�.";		
								labyrinthe.getObjet(posX, posY).setEstVisible(true);		
								desactive=false;
								}
							break;
						case "Carte":
							recapTour = recapTour+"\nVous avez trouv� une carte du labyrinthe.\nLes pi�ges cach�s ont �t� r�v�l�s!";
							for (int h=0; h < labyrinthe.getTaille(); h++)
								for (int j=0; j < labyrinthe.getTaille(); j++)
									if ( labyrinthe.getCase(h, j).getContientPiege()==true )
										labyrinthe.getPiege(h, j).setEstVisible(true);
							break;
						case "Coeur":
							recapTour = recapTour+"\nVous avez trouv� un coeur. Vous avez regagn� 10 points de vie.";
							labyrinthe.getAventurier().setEnergie(labyrinthe.getAventurier().getEnergie()+10);
							break;
						case "Pomme dor�e":
							recapTour = recapTour+"\nVous avez trouv� une pomme dor�e. Vous avez regagn� 20 points de vie.";
							labyrinthe.getAventurier().setEnergie(labyrinthe.getAventurier().getEnergie()+20);
							break;
						case "Pomme dor�e enchant�":
							recapTour = recapTour+"\nVous avez trouv� une pomme dor�e enchant�. Vous avez regagn� 30 points de vie.";
							labyrinthe.getAventurier().setEnergie(labyrinthe.getAventurier().getEnergie()+30);
							break;
						default:
							System.out.println("Erreur dans le d�placement de l'aventurier (switch objet vrai)");
						}
						}
				if (desactive==true)
					labyrinthe.getObjet(posX, posY).setEstDesactive(true);
				}
			
			}
		/* fin objet */
		/* si la case contient un personnage */
		if (labyrinthe.getCase(posX, posY).getContientPersonnage()==true)
			{
			desactive = false;
			if (labyrinthe.getPersonnage(posX, posY).getEstDesactive()==false)
				{
				dispMsg = true;
				switch ( labyrinthe.getPersonnage(posX, posY).getType() ) {
					case "Voleur":
						recapTour = recapTour+"\nVous avez rencontr� un voleur.";
						for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
							{
							if ( !(labyrinthe.getAventurier().getInventaire()[i].equals("Vide")) )
								{
								recapTour = recapTour+"\nLe voleur vous a d�rob� l'objet : "+labyrinthe.getAventurier().getInventaire()[i]+" !!";
								labyrinthe.getAventurier().setInventaire(i,"Vide");
								break;
								}
							}
						break;
					case "Illusioniste":
						recapTour = recapTour+"\nCatastrophe! Vous avez rencontr� un illusioniste qui avait chang� son apparence pour vous pi�ger!!";
						degatTour = degatTour+15;
						bouge = false;
						labyrinthe.setPiege(posX, posY, new Piege("Boss", "Enl�ve 15 points de vie quand attaqu� et a de fortes chances de rester en vie. Laisse tomber un objet � sa mort",true));
						labyrinthe.getCase(posX,posY).setContientPiege(true);
						labyrinthe.getPiege(posX, posY).setIllusioniste(true);
						desactive=true;
						break;
					case "Explorateur":
						recapTour = recapTour+"\nVous avez rencontr� un explorateur.\nL'explorateur vous � r�v�l� le contenu des coffres!\nL'explorateur est parti...";
						for (int h=0; h < labyrinthe.getTaille(); h++)
							for (int j=0; j < labyrinthe.getTaille(); j++)
								if ( labyrinthe.getCase(h, j).getContientObjet()==true )
									labyrinthe.getObjet(h, j).setEstVisible(true);
						desactive = true;
						break;
					case "Gu�risseur":
						recapTour = recapTour+"\nVous avez rencontr� un gu�risseur.";
						if (labyrinthe.getAventurier().getEnergie()<100)
							{
							if (((int)(Math.random()*(3-1+1)+1))==3)
								{
								labyrinthe.getAventurier().setEnergie(labyrinthe.getAventurier().getEnergie()+20);
								recapTour = recapTour+"\nLe gu�risseur vous a rendu 20 points de vie !\nLe gu�risseur est parti..";
								}
							else
								{
								labyrinthe.getAventurier().setEnergie(labyrinthe.getAventurier().getEnergie()+10);
								recapTour = recapTour+"\nLe gu�risseur vous a rendu 10 points de vie.\nLe gu�risseur est parti..";
								}
							desactive = true;
							}
						break;
					case "Druide":
						recapTour = recapTour+"\nVous avez rencontr� un druide.";
						for (i=0;i<labyrinthe.getAventurier().getDebuff()[i].length();i++)
							{
							if (labyrinthe.getAventurier().getDebuff()[i].equals("Empoisonn�"))
								{
								labyrinthe.getAventurier().setDebuff(i,"Aucun");
								recapTour = recapTour+"\nLe druide vous a soign�, vous n'�tes plus empoisonn� !\nLe druide est parti...";
								desactive = true;
								break;
								}
							}
						break;
					default:
						System.out.println("Erreur dans le d�placement de l'aventurier (switch objet personnages)");
					}
				}
			if (desactive==true)
				labyrinthe.getPersonnage(posX, posY).setEstDesactive(true);
			}
		/* d�place l'aventurier */
		if (bouge==true)
			{
			switch (labyrinthe.getCase(posX, posY).getType())
				{
				case  "Terre":
					degatTour = degatTour+1;
					break;
				case "Glace":
					degatTour = degatTour+1;
					switch (direction)
					{
					case "Haut":
						while ( (labyrinthe.getCase(posX-1, posY).getType().equals("Glace")) && (labyrinthe.getCase(posX-1, posY).getContientObjet()==false) 
								&& (labyrinthe.getCase(posX-1, posY).getContientPiege()==false) && (labyrinthe.getCase(posX-1, posY).getContientPersonnage()==false) )
							posX = posX-1;
						break;
					case "Bas":
						while ( (labyrinthe.getCase(posX+1, posY).getType().equals("Glace")) && (labyrinthe.getCase(posX+1, posY).getContientObjet()==false) 
								&& (labyrinthe.getCase(posX+1, posY).getContientPiege()==false) && (labyrinthe.getCase(posX+1, posY).getContientPersonnage()==false) )
							posX = posX+1;
						break;
					case "Droite":
						while ( (labyrinthe.getCase(posX, posY+1).getType().equals("Glace")) && (labyrinthe.getCase(posX, posY+1).getContientObjet()==false) 
								&& (labyrinthe.getCase(posX, posY+1).getContientPiege()==false) && (labyrinthe.getCase(posX, posY+1).getContientPersonnage()==false) )
							posY = posY+1;
						break;
					case "Gauche":
						while ( (labyrinthe.getCase(posX, posY-1).getType().equals("Glace")) && (labyrinthe.getCase(posX, posY-1).getContientObjet()==false) 
								&& (labyrinthe.getCase(posX, posY-1).getContientPiege()==false) && (labyrinthe.getCase(posX, posY-1).getContientPersonnage()==false) )
							posY = posY-1;
						break;
					default :
						System.out.println("Erreur dans le d�placement de l'aventurier (switch glace)");
					}
					break;
				case "Eau":
					degatTour = degatTour+5;
					for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
					{
					if ( labyrinthe.getAventurier().getInventaire()[i].equals("Boulet") )
						{
						degatTour = degatTour+100;
						recapTour = recapTour+"\nNager avec un boulet attach� aux pieds n'est pas une bonne id�e... \nVous vous �tes noy� !!";
						dispMsg =true;
						}
					}
					for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
					{
					if ( labyrinthe.getAventurier().getInventaire()[i].equals("Palmes") )
						{
						degatTour = degatTour-4;
						}
					}
					break;
				case "Sable":
					degatTour = degatTour+3;
					for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
					{
					if ( labyrinthe.getAventurier().getInventaire()[i].equals("Boulet") )
						{
						degatTour = degatTour+100;
						recapTour = recapTour+"\nTraverser des sables mouvants avec un boulet attach� aux pieds n'est pas une bonne id�e... \nVous avez �t� englouti par les sables mouvants !!";
						dispMsg =true;
						}
					}
					break;
				default :
					System.out.println("Erreur dans le d�placement de l'aventurier (switch environnement)");
				}
			if ( (direction.equals("Haut")) || (direction.equals("Bas")) )
				labyrinthe.getAventurier().setX(posX);
			else
				labyrinthe.getAventurier().setY(posY);
			}
		/* applique les d�gats du tour */
		labyrinthe.getAventurier().setEnergie(labyrinthe.getAventurier().getEnergie()-degatTour);
		repaint();
		if (dispMsg==true)
			JOptionPane.showMessageDialog(this, recapTour);
		}
	}

		@Override
		public void mousePressed (MouseEvent e) { }

		@Override
		public void mouseReleased (MouseEvent e) { }

		@Override
		public void mouseEntered (MouseEvent e) { }

		@Override
		public void mouseExited (MouseEvent e) { }

		@Override
		public void mouseDragged (MouseEvent e) { }

		@Override
		public void mouseMoved (MouseEvent e)
			{
			barre_etat.setText(" Position de la souris : " + e.getX() + " " + e.getY());
			}

		@Override
		public void keyTyped (KeyEvent e) { /* Ne pas utiliser */ }

		@Override
		public void keyPressed (KeyEvent e) { }

		@Override
		public void keyReleased (KeyEvent e)
			{
			/**
			 * Déplacement VISUEL de l'aventurier dans le labyrinthe.			------> POUR TESTER
			 */
			
			/*----- Vers le bas (fl�che) -----*/
			if (e.getKeyCode() == KeyEvent.VK_DOWN)
			{
			labyrinthe.getAventurier().setX(labyrinthe.getAventurier().getX()+1);
			repaint();
			}

			/*----- Vers le haut (fl�che) -----*/
			if (e.getKeyCode() == KeyEvent.VK_UP)
			{
			labyrinthe.getAventurier().setX(labyrinthe.getAventurier().getX()-1);
			repaint();
			}

			/*----- Vers la droite (fl�che) -----*/
			if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			{
			labyrinthe.getAventurier().setY(labyrinthe.getAventurier().getY()+1);
			repaint();
			}

			/*----- Vers la gauche (fl�che) -----*/
			if (e.getKeyCode() == KeyEvent.VK_LEFT)
			{
			labyrinthe.getAventurier().setY(labyrinthe.getAventurier().getY()-1);
			repaint();
			}
			
			/**
			 * Déplacement r�el de l'aventurier dans le labyrinthe.
			 */
			
			/*----- Vers le bas (s) -----*/
			if (e.getKeyCode() == KeyEvent.VK_S)
				deplacement("Bas");

			/*----- Vers le haut (z) -----*/
			if (e.getKeyCode() == KeyEvent.VK_Z)
				deplacement("Haut");

			/*----- Vers la droite (d) -----*/
			if (e.getKeyCode() == KeyEvent.VK_D)
				deplacement("Droite");

			/*----- Vers la gauche (a) -----*/
			if (e.getKeyCode() == KeyEvent.VK_Q)
				deplacement("Gauche");
			
			/*----- Automatique -----*/
			if (e.getKeyCode() == KeyEvent.VK_P) {
				int courteDistance = 10000;
				int posAventurierX=labyrinthe.getAventurier().getX();
				int posAventurierY=labyrinthe.getAventurier().getY();
				int stop=0;
				String direction = "blabla";
				if ( !(labyrinthe.getAventurier().getX()==21 && labyrinthe.getAventurier().getY()==1) ) 
				{
				if ( labyrinthe.getAventurier().getX()==0 && labyrinthe.getAventurier().getY()==20)
					{deplacement("Bas");
					stop=1;}
				if (stop==0) {
				if ( !(labyrinthe.getCase(posAventurierX+1, posAventurierY).getType().equals("Infranchissable")) )
					if ( labyrinthe.getCase(posAventurierX+1, posAventurierY).getDistanceFin() < courteDistance )
						{
						courteDistance = labyrinthe.getCase(posAventurierX+1, posAventurierY).getDistanceFin();
						direction = "Bas";
						}
				if ( !(labyrinthe.getCase(posAventurierX, posAventurierY-1).getType().equals("Infranchissable")) )
					if ( labyrinthe.getCase(posAventurierX, posAventurierY-1).getDistanceFin() < courteDistance )
						{
						courteDistance = labyrinthe.getCase(posAventurierX, posAventurierY-1).getDistanceFin();
						direction = "Gauche";
						}
				if ( !(labyrinthe.getCase(posAventurierX-1, posAventurierY).getType().equals("Infranchissable")) )
					if ( labyrinthe.getCase(posAventurierX-1, posAventurierY).getDistanceFin() < courteDistance )
						{
						courteDistance = labyrinthe.getCase(posAventurierX-1, posAventurierY).getDistanceFin();
						direction = "Haut";
						}
				if ( !(labyrinthe.getCase(posAventurierX, posAventurierY+1).getType().equals("Infranchissable")) )
					if ( labyrinthe.getCase(posAventurierX, posAventurierY+1).getDistanceFin() < courteDistance )
						{
						courteDistance = labyrinthe.getCase(posAventurierX, posAventurierY+1).getDistanceFin();
						direction = "Droite";
						}
					deplacement(direction);}
				}
			}
				
			
			/*----- TEST MORT INSTANT -----*/
			if (e.getKeyCode() == KeyEvent.VK_M)
				{
				labyrinthe.getAventurier().setEnergie(0);
				}
			
			/*----- TEST SE DONNER DES OBJETS -----*/
			if (e.getKeyCode() == KeyEvent.VK_G)
				{
				labyrinthe.getAventurier().setInventaire(1, "Hache");
				System.out.println("Don de l'objet: "+labyrinthe.getAventurier().getInventaire()[1]);
				}
			
			/*----- TEST VOIR INVENTAIRE -----*/
			if (e.getKeyCode() == KeyEvent.VK_I) {
				int i;
				for (i=0;i<labyrinthe.getAventurier().getInventaire().length;i++)
					System.out.println(labyrinthe.getAventurier().getInventaire()[i]);}
				
			}
		} /*----- Fin de la classe interne Dessin -----*/
} /*----- Fin de la classe Vue -----*/