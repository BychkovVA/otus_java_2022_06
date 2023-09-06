package ru.otus.jpql.demo;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.jpql.core.repository.DataTemplateHibernate;
import ru.otus.jpql.core.repository.HibernateUtils;
import ru.otus.jpql.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.jpql.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.jpql.crm.model.Address;
import ru.otus.jpql.crm.model.Client;
import ru.otus.jpql.crm.model.Phone;
import ru.otus.jpql.crm.service.DbServiceClientImpl;

import java.util.ArrayList;

public class DbServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
///
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
///
        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);


        ArrayList<Long> clientIds = new ArrayList();
        Long clientId;

        for (var idx = 0; idx < 100; idx++) {
        var address = new Address("Someone's street " + idx);
            // создание экземпляра Client
            Client clientEkz = new Client("dbServiceFirst"+ idx, address);
            // добавление нескольких телефонов
            clientEkz.addPhone(new Phone("89994444444"+ idx));
            clientEkz.addPhone(new Phone("89995555555"+ idx));
            clientEkz.addPhone(new Phone("89997777777"+ idx));
            // сохранение клиента в базе со всеми связанными сущностями(в нём же и происходит КЭШирование)
            clientId = dbServiceClient.saveClient(clientEkz).getId();
            clientIds.add(clientId);
        }
        // Попытка чтения списка клиентов БЕЗ кэша
        log.info("Start without cahse");
        clientIds.forEach(cId -> {log.info(dbServiceClient.getClient(cId).get().getName());});
        log.info("End without cahse");

        // Попытка чтения списка клиентов С кэшом
        log.info("Start with cahse");
        clientIds.forEach(cId -> {log.info(dbServiceClient.getClientWithCache(cId).get().getName());});
        log.info("End with cahse");
        // Попытка чтения списка клиентов С кэшом НО после отчистки GC
        System.gc();
        log.info("Start with cahse after clear GC");
        clientIds.forEach(cId -> {log.info(dbServiceClient.getClientWithCache(cId).get().getName());});
        log.info("End with cahse after clear GC");

/*       var clientFirst = dbServiceClient.saveClient(new Client("dbServiceFirst", address));

        clientFirst.addPhone(new Phone("+1234567890"));
        clientFirst.addPhone(new Phone("+1234567891"));
        clientFirst.addPhone(new Phone("+1234567892"));


        dbServiceClient.saveClient(clientFirst);

        var address2 = new Address("Someone's else street");
        var clientSecond = dbServiceClient.saveClient(new Client("dbServiceSecond", address2));
        clientSecond.addPhone(new Phone("+0987654321"));
        dbServiceClient.saveClient(clientSecond);


        var clientSecondSelected = dbServiceClient.getClient(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
        log.info("clientSecondSelected:{}", clientSecondSelected);
///
        dbServiceClient.saveClient(new Client(clientSecondSelected.getId(), "dbServiceSecondUpdated", clientSecondSelected.getAddress(), clientSecondSelected.getPhones()));
        var clientUpdated = dbServiceClient.getClient(clientSecondSelected.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecondSelected.getId()));
        log.info("clientUpdated:{}", clientUpdated);

        log.info("All clients");
        dbServiceClient.findAll().forEach(client -> log.info("client:{}", client));

*/
    }
}
