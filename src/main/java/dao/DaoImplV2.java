package dao;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public class DaoImplV2 implements IDao {

    @Override
    public double getData() {
        System.out.println("Version web service ");
        return 44;
    }
}
