package pres;

import dao.DaoImpl;
import metier.MetierImpl;
/*
 * Instanciation statique de l'implémentation de l'interface IDao dans le constructeur de la classe MetierImpl
 */
public class Presntation1 {
    public static void main(String[] args) {
        DaoImpl dao = new DaoImpl();
        MetierImpl metier = new MetierImpl(dao);
        System.out.println("Resultat : " + metier.calcul());
    }
}
