package util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import pl.bb.broker.brokerdb.util.IHibernateUtil;

/**
 * Created with IntelliJ IDEA.
 * User: BamBalooon
 * Date: 30.05.14
 * Time: 13:13
 * To change this template use File | Settings | File Templates.
 */
public class TestHibernateUtil implements IHibernateUtil {
    private static TestHibernateUtil instance;

    public static TestHibernateUtil getInstance() {
        if(instance==null) {
            instance = new TestHibernateUtil();
        }
        return instance;
    }

    private final SessionFactory SESSION_FACTORY;

    {
        try {
            Configuration conf = new Configuration();
            conf.configure("hibernate-test.cfg.xml");

            StandardServiceRegistryBuilder sb = new StandardServiceRegistryBuilder();
            sb.applySettings(conf.getProperties());
            StandardServiceRegistry registry = sb.build();
            SESSION_FACTORY = conf.buildSessionFactory(registry);
        } catch (Throwable th) {
            throw new ExceptionInInitializerError(th);
        }
    }

    public Session getSession() throws HibernateException {
        return SESSION_FACTORY.openSession();
    }

}
