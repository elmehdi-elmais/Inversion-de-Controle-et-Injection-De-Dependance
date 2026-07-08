package dao;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public class DaoImpl implements IDao {
    @Override
    public double getData() {
        System.out.println("Version base de données");
        double temp = 49;
        return temp;
    }

}
