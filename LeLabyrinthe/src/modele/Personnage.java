package modele;


/**
 * Classe qui reprÃ©sente les personnages présents dans le labyrinthe.
 */
public class Personnage
{
	/*------------*/
	/* PropriÃ©tÃ©s */
	/*------------*/
	private String type;			/* type du piège */
	private String desc;			/* description du piège */
	private boolean estDesactive;	/* desactivé si l'aventurier a interagit avec */


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
	/* MÃ©thodes */
	/*----------*/
	
	public String getType () { return this.type; }
	public void setType (String t) { this.type = t; }
	
	public String getDesc () { return this.desc; }
	public void setDesc (String d) { this.desc = d; }
	
	public boolean getEstDesactive () { return this.estDesactive; }
	public void setEstDesactive (boolean des) { this.estDesactive = des; }
	
	
} /*----- Fin de la classe Piege -----*/