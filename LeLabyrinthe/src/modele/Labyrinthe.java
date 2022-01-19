package modele;


/**
 * Classe qui reprÃ©sente le labyrinthe.
 */
public class Labyrinthe
{
	/*------------*/
	/* PropriÃ©tÃ©s */
	/*------------*/

	/*----- Taille du labyrinthe -----*/
	private final int taille_du_labyrinthe;

	/*----- Liste des cases composant le labyrinthe -----*/
	private final Case[][] cases;
	
	/*----- Liste des objets présents dans le labyrinthe -----*/
	private final Objet[][] objets;
	
	/*----- Liste des pièges présents dans le labyrinthe -----*/
	private final Piege[][] pieges;
	
	/*----- Liste des personnages présents dans le labyrinthe -----*/
	private final Personnage[][] personnages;

	/*----- Aventurier qui doit traverser le labyrinthe -----*/
	private Aventurier aventurier;


	/*--------------*/
	/* Constructeur */
	/*--------------*/

	public Labyrinthe (int taille)
		{
		this.taille_du_labyrinthe = taille;
		this.cases = new Case[this.taille_du_labyrinthe][this.taille_du_labyrinthe];
		this.objets = new Objet[this.taille_du_labyrinthe][this.taille_du_labyrinthe];
		this.pieges = new Piege[this.taille_du_labyrinthe][this.taille_du_labyrinthe];
		this.personnages = new Personnage[this.taille_du_labyrinthe][this.taille_du_labyrinthe];
		}


	/*----------*/
	/* MÃ©thodes */
	/*----------*/

	/**
	 * Retourne la taille du labyrinthe.
	 */
	public int getTaille () { return this.taille_du_labyrinthe; }


	/**
	 * Affecte / retourne la case en position i,j.
	 */
	public void setCase (int i, int j, Case c) { this.cases[i][j] = c; }

	public Case getCase (int i, int j) { return this.cases[i][j]; }

	/**
	 * Affecte / retourne l'objet en position i,j.
	 */
	public void setObjet (int i, int j, Objet o) { this.objets[i][j] = o; }

	public Objet getObjet (int i, int j) { return this.objets[i][j]; }

	/**
	 * Affecte / retourne le piège en position i,j.
	 */
	public void setPiege (int i, int j, Piege p) { this.pieges[i][j] = p; }

	public Piege getPiege (int i, int j) { return this.pieges[i][j]; }

	/**
	 * Affecte / retourne le personnage en position i,j.
	 */
	public void setPersonnage (int i, int j, Personnage pnj) { this.personnages[i][j] = pnj; }

	public Personnage getPersonnage (int i, int j) { return this.personnages[i][j]; }
	
	/**
	 * Retourne / affecte l'aventurier du labyrinthe.
	 */
	public Aventurier getAventurier () { return this.aventurier; }

	public void setAventurier (Aventurier aventurier) { this.aventurier = aventurier; }

} /*----- Fin de la classe Labyrinthe -----*/
