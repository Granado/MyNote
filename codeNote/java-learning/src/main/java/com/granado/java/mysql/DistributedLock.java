package com.granado.java.mysql;

import com.granado.java.utils.SQLUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @Author: Granado
 * @Date: 2019-05-30 16:23
 */
public class DistributedLock {

    private static final String COLUMNS = "id,lock_name,owner_name,lock_state,modify_date,create_date";

    private static final UUID SERVER_ID = UUID.randomUUID();

    private static final String URL = "jdbc:mysql://localhost:3306/test";

    private static final String SQL_FIND_LOCK_BY_NAME = "select * from `lock` where lock_name = #{name}";

    private static final String SQL_CREATE_LOCK = "insert ignore into `lock` (`lock_name`) value(#{lockName})";

    private static final String SQL_LOCK = "update `lock` set lock_state = 1, owner_name = #{ownerName} " +
                                           "where lock_name = #{lockName} and lock_state = 0";

    private static final String SQL_UNLOCK = "update `lock` set lock_state = 0, owner_name = null " +
      "where lock_name = #{lockName} and lock_state = 1 and owner_name = #{ownerName}";

    private String addLock;

    private DataSource dataSource;

    private String lockName;

    public DistributedLock(String lockName) {
        this.lockName = Objects.requireNonNull(lockName);
        dataSource = createDatasource();
        initSql();
        initLock();
    }

    private static DataSource createDatasource() {
        return SQLUtils.createDatasource(URL, "root", "zylzysl1994");
    }

    private void initSql() {

        addLock = constructSQLByOneKeyValue(SQL_CREATE_LOCK, "lockName", lockName);
    }

    private void initLock() {
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()){
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);

            statement.executeUpdate(addLock);

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public boolean unlock() {
        String unlockSQL = SQLUtils.constructSQL(SQL_UNLOCK, Map.of("lockName", lockName, "ownerName", generateServerName()));
        return SQLUtils.executeUpdate(unlockSQL, dataSource);
    }

    public boolean lock() {
        String lockSQL = SQLUtils.constructSQL(SQL_LOCK, Map.of("lockName", lockName, "ownerName", generateServerName()));
        return SQLUtils.executeUpdate(lockSQL, dataSource);
    }

    private String constructSQLByOneKeyValue(String sql, String key, Object value) {
        return SQLUtils.constructSQL(sql, Map.of(key, value));
    }

    private String generateServerName() {
        Thread t = Thread.currentThread();
        StringBuilder builder = new StringBuilder(SERVER_ID.toString());
        builder.append('-').append(t.getName()).append('-').append(t.getId()).append("-").append(t.getState());
        return builder.toString();
    }

    public static void main(String[] args) {
        DistributedLock lock = new DistributedLock("test");
        Runnable runnable = () -> {
            while (!lock.lock()) {
                System.out.println(Thread.currentThread().getName() + ", wait lock!");
                LockSupport.parkNanos(1000 * 1000 * 100);
            }
            try {
                System.out.println(Thread.currentThread().getName() + ", i get lock!");
                System.out.println(Thread.currentThread().getName() + "-" + Thread.currentThread().getId());
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
            } finally {
                lock.unlock();
                System.out.println(Thread.currentThread().getName() + "-release lock");
            }
        };

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            executorService.execute(runnable);
        }
        executorService.shutdown();
    }
}
