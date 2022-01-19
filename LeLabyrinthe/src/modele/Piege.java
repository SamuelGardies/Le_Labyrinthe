package modele;


/**
 * Classe qui reprÃ©sente les pièges présents dans le labyrinthe.
 */
public class Piege
{
	/*------------*/
	/* PropriÃ©tÃ©s */
	/*------------*/
	private String type;			/* type du piège */
	private String desc;			/* description du piège */
	private boolean estVisible; 	/* visibilité du piège */
	private boolean estDesactive;	/* desactivé si l'aventurier a interagit avec */
	private boolean mimic;			/* sert à changer l'apparence d'un monstre si c'est un faux coffre */
	private boolean illusioniste;	/* sert à changer l'apparence d'un boss si c'est un illusioniste */


	/*--------------*/
	/* Constructeur */
	/*--------------*/

	public Piege (String t, String d, Boolean v)
		{
		this.type = t;
		this.desc = d;
		this.estVisible = v;
		this.estDesactive = false;
		this.mimic = false;
		this.illusioniste = false;
		}


	/*----------*/
	/* MÃ©thodes */
	/*----------*/
	
	public String getType () { return this.type; }
	public void setType (String t) { this.type = t; }
	
	public String getDesc () { return this.desc; }
	public void setDesc (String d) { this.desc = d; }
	
	public boolean getEstVisible () { return this.estVisible; }
	public void setEstVisible (boolean v) { this.estVisible = v; }
	
	public boolean getEstDesactive () { return this.estDesactive; }
	public void setEstDesactive (boolean des) { this.estDesactive = des; }
	
	public boolean getMimic () { return this.mimic; }
	public void setMimic (boolean m) { this.mimic = m; }
	
	public boolean getIllusioniste () { return this.illusioniste; }
	public void setIllusioniste (boolean i) { this.illusioniste = i; }
	
	
} /*----- Fin de la classe Piege -----*/