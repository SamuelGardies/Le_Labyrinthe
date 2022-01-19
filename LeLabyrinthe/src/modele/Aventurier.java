package modele;


/**
 * Classe qui reprÃ©sente l'aventurier qui traverse le labyrinthe.
 */
public class Aventurier
{
	/*------------*/
	/* PropriÃ©tÃ©s */
	/*------------*/

	/*----- Sa position dans le labyrinthe -----*/
	private int x;
	private int y;
	private int energie;						/* energie de l'aventurier, dépensée en se déplaçant */
	private String statu;						/* statut (en vie ou mort) de l'aventurier */
	private String[] inventaire;				/* contient les objets "passifs" de l'aventurier (ceux qui ne sont pas utilisé de suite en les ramassant */
	public String[] debuff;						/* effet négatif affectant l'aventurier ex: poison */


	/*--------------*/
	/* Constructeur */
	/*--------------*/

	public Aventurier (int x, int y, int e, String s)
		{
		this.x = x;
		this.y = y;
		this.energie = e;
		this.statu = s;
		this.inventaire = new String[10];
		int i;
		for (i=0;i<10;i++)
			{
			inventaire[i] = "Vide";
			}
		this.debuff = new String[15];
		for (i=0;i<15;i++)
			{
			debuff[i] = "Aucun";
			}
		}


	/*----------*/
	/* MÃ©thodes */
	/*----------*/

	public int getX () { return this.x; }
	public void setX (int x) { this.x = x; }

	public int getY () { return this.y; }
	public void setY (int y) { this.y = y; }
	
	public int getEnergie () { return this.energie; }
	public void setEnergie (int e) { if(e>0) 	this.energie = e;
									 else 		this.energie = 0;		/* évite que l'énergie ai une valeur négative */
									if(e>100)	this.energie = 100;}		/* évite que l'énergie dépasse 100 (valeur max) */
	
	public String getStatu () { return this.statu; }
	public void setStatu (String e) { this.statu = e; }
	
	public String[] getInventaire () { return this.inventaire; }
	public void setInventaire (int index,String i) { this.inventaire[index] = i; }
	
	public String[] getDebuff () { return this.debuff; }
	public void setDebuff (int index,String d) { this.debuff[index] = d; }
	

} /*----- Fin de la classe Aventurier -----*/
