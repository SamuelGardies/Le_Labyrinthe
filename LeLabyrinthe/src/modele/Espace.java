package modele;


/**
 * Classe qui reprÃ©sente une zone d'espace libre.
 */
public class Espace extends Case
{
	
	/*------------*/
	/* Propriétés */
	/*------------*/

	private String type;
	private boolean contientObjet;
	private boolean contientPiege;
	private boolean contientPersonnage;
	private int distanceFin;

	/*--------------*/
	/* Constructeur */
	/*--------------*/

	public Espace (String t, boolean obj, int d) {this.type = t;	this.contientObjet=obj;		this.contientPiege = false;		this.contientPersonnage = false;		this.distanceFin=d;}

	/*----------*/
	/* MÃ©thodes */
	/*----------*/
	
	public String getType () { return this.type; }
	public void setType (String t) { this.type = t; }
	
	public boolean getContientObjet () { return this.contientObjet; }
	public void setContientObjet (boolean obj) { this.contientObjet = obj; }
	
	public boolean getContientPiege () { return this.contientPiege; }
	public void setContientPiege (boolean piege) { this.contientPiege = piege; }
	
	public boolean getContientPersonnage () { return this.contientPersonnage; }
	public void setContientPersonnage (boolean pnj) { this.contientPersonnage = pnj; }
	
	public int getDistanceFin () { return this.distanceFin; }
	public void setDistanceFin (int d) { this.distanceFin = d; }
	

} /*----- Fin de la classe Espace -----*/
