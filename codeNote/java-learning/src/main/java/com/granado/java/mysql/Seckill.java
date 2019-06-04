package com.granado.java.mysql;

import com.granado.java.utils.SQLUtils;
import io.netty.buffer.Unpooled;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: Granado
 * @Date: 2019-05-31 09:17
 */
public class Seckill {

    private static final String SQL_DESCEND_INVENTORY = "update inventory set quantity = quantity - #{number} where id = #{id} and quantity > 0";

    private static final Random RANDOM = new Random();

    private static final String URL = "jdbc:mysql://localhost:3306/test";

    private static final String USER = "root";

    private static final String PASSWORD = "zylzysl1994";

    public static void main(String[] args) {

        DataSource dataSource = SQLUtils.createDatasource(URL, USER, PASSWORD);

        Runnable runnable = () -> {
            int goodsNumber = 1;//RANDOM.nextInt() & (0x0f);
            System.out.println("i will buy " + goodsNumber + " goods, " + threadInfo());
            String sql = SQLUtils.constructSQL(SQL_DESCEND_INVENTORY, Map.of("number", goodsNumber, "id", 1));
            if (SQLUtils.executeUpdate(sql, dataSource)) {
                System.out.println("i have buy " + goodsNumber + " goods, " + threadInfo());
            }
        };

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 2455; i++) {
            executorService.execute(runnable);
        }
        executorService.shutdown();
    }

    public static String threadInfo() {
        return Thread.currentThread().getName() + "-" + Thread.currentThread().getId();
    }
}
