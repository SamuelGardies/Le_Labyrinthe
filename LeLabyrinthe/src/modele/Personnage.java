package modele;


/**
 * Classe qui représente les personnages pr�sents dans le labyrinthe.
 */
public class Personnage
{
	/*------------*/
	/* Propriétés */
	/*------------*/
	private String type;			/* type du pi�ge */
	private String desc;			/* description du pi�ge */
	private boolean estDesactive;	/* desactiv� si l'aventurier a interagit avec */


	/*--------------*/
	/* Constructeur */
	/*--------------*/

	public Personnage (String t, String d)
		{
		this.type = t;
		this.desc = d;
		this.estDesactive = false;
		}


	/*----------*/
	/* Méthodes */
	/*----------*/
	
	public String getType () { return this.type; }
	public void setType (String t) { this.type = t; }
	
	public String getDesc () { return this.desc; }
	public void setDesc (String d) { this.desc = d; }
	
	public boolean getEstDesactive () { return this.estDesactive; }
	public void setEstDesactive (boolean des) { this.estDesactive = des; }
	
	
} /*----- Fin de la classe Piege -----*/