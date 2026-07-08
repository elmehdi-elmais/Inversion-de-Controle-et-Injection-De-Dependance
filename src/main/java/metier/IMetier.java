package metier;

import  dao.IDao;

public interface IMetier {
    double calcul();
    void setDao(IDao dao);
}
