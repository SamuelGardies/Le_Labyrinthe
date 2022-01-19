package modele;


/**
 * Classe qui représente une case du labyrinthe.
 */
public abstract class Case
{
	/**
	 * Méthode qui retourne le nom de la classe de l'objet.
	 */
	public final String getClassName() { return this.getClass().getSimpleName(); }



	public abstract String getType();
	public abstract boolean getContientObjet();
	public abstract boolean getContientPiege();
	public abstract void setContientPiege(boolean piege);
	public abstract boolean getContientPersonnage();
	public abstract void setContientPersonnage(boolean piege);
	public abstract int getDistanceFin ();
	public abstract void setDistanceFin (int d);
} /*----- Fin de la classe Case -----*/
