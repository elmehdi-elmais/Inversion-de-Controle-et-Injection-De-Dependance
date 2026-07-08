package metier;

import dao.IDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Service("metier")
public class MetierImpl implements IMetier {

//    @Qualifier("daoImplV2")
    private IDao dao;


    public MetierImpl() {
    }

    @Autowired
    public MetierImpl(@Qualifier("daoImplV2") IDao dao) {
        this.dao = dao;
    }

    //@Autowired
    //@Qualifier("daoImplV2")
    public void setData(IDao dao) {
        this.dao = dao;
    }

    @Override
    public double calcul() {
        double t = dao.getData();
        return t * 43 / 3;
    }

    @Override
    public void setDao(IDao dao) {
        this.dao = dao;
    }
}
