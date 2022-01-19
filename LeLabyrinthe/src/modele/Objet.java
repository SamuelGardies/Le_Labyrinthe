package modele;


/**
 * Classe qui représente les objets pr�sents dans le labyrinthe.
 */
public class Objet
{
	/*------------*/
	/* Propriétés */
	/*------------*/
	private String type;			/* type de l'objet */
	private String desc;			/* description de l'objet */
	private boolean estVisible; 	/* visibilit� de l'objet */
	private boolean estDesactive;	/* desactiv� si l'aventurier a interagit avec */


	/*--------------*/
	/* Constructeur */
	/*--------------*/

	public Objet (String t, String d, boolean v)
		{
		this.type = t;
		this.desc = d;
		this.estVisible = v;
		this.estDesactive = false;
		}


	/*----------*/
	/* Méthodes */
	/*----------*/
	
	public String getType () { return this.type; }
	public void setType (String t) { this.type = t; }
	
	public String getDesc () { return this.desc; }
	public void setDesc (String d) { this.desc = d; }
	
	public boolean getEstVisible () { return this.estVisible; }
	public void setEstVisible (boolean v) { this.estVisible = v; }
	
	public boolean getEstDesactive () { return this.estDesactive; }
	public void setEstDesactive (boolean des) { this.estDesactive = des; }
	
	
} /*----- Fin de la classe Objet -----*/