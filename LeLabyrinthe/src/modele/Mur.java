package modele;


/**
 * Classe qui représente un mur.
 */
public class Mur extends Case
{

	@Override
	public String getType() {return "Infranchissable";}

	@Override
	public boolean getContientObjet() {return false;}

	@Override
	public boolean getContientPiege() {return false;}

	@Override
	public void setContientPiege(boolean piege) {}

	@Override
	public boolean getContientPersonnage() {return false;}

	@Override
	public void setContientPersonnage(boolean piege) {}

	@Override
	public int getDistanceFin() {return(10000);}

	@Override
	public void setDistanceFin(int d) {};


} /*----- Fin de la classe Mur -----*/
