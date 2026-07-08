package pres;

import dao.DaoImpl;
import dao.IDao;
import metier.IMetier;
import metier.MetierImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Scanner;

/*
 * Instanciation statique de l'implémentation de l'interface IDao dans le constructeur de la classe MetierImpl
 */
public class Presntation2 {
    public static void main(String[] args) throws FileNotFoundException,ClassNotFoundException , InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Scanner scanner = new Scanner(new File("config.txt"));

        String daoClassName = scanner.nextLine();
        Class cDao = Class.forName(daoClassName);
        IDao dao = (IDao)cDao.newInstance();

        String metierClassName = scanner.nextLine();
        Class cMetier =  Class.forName(metierClassName);

       IMetier metier = (IMetier) cMetier.getConstructor(IDao.class).newInstance(dao);

        // IMetier metier = (IMetier) cMetier.getConstructor().newInstance();
        // Method setDao = cMetier.getDeclaredMethod("setDao", IDao.class);
        // setDao.invoke(metier, dao);

        System.out.println("Resultat : " + metier.calcul());
    }
}
