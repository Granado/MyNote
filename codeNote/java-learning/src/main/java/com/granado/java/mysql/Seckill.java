package com.granado.java.mysql;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.granado.java.utils.SQLUtils;
import io.netty.buffer.Unpooled;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @Author: Granado
 * @Date: 2019-05-31 09:17
 */
public class Seckill {

    private static final String SQL_DESCEND_INVENTORY = "update inventory set quantity = quantity - #{number} where id = #{id} and quantity >= #{number}";

    private static final Random RANDOM = new Random();

    private static final String URL = "jdbc:mysql://localhost:3306/test";

    private static final String USER = "root";

    private static final String PASSWORD = "zylzysl1994";

    private static final AtomicInteger BUY_COUNT = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {

        DataSource dataSource = SQLUtils.createDatasource(URL, USER, PASSWORD);

        Callable<Integer> runnable = () -> {
            int goodsNumber = RANDOM.nextInt() & (0xff);
            //System.out.println("i will buy " + goodsNumber + " goods, " + threadInfo());
            String sql = SQLUtils.constructSQL(SQL_DESCEND_INVENTORY, Map.of("number", goodsNumber, "id", 1));
            if (SQLUtils.executeUpdate(sql, dataSource)) {
                BUY_COUNT.addAndGet(goodsNumber);
                System.out.println("i have buy " + goodsNumber + " goods, " + threadInfo());
                return goodsNumber;
            }
            return 0;
        };

        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Callable<Integer>> callables = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            callables.add(runnable);
        }

        List<Future<Integer>> results = executorService.invokeAll(callables);
        executorService.shutdown();
        int count = 0;
        while (!results.isEmpty()) {
            Iterator<Future<Integer>> iterator = results.iterator();
            while (iterator.hasNext()) {
                Future<Integer> result = iterator.next();
                if (result.isDone()) {
                    count += result.get();
                    iterator.remove();
                }
            }
        }

        System.out.println(count);
    }

    public static String threadInfo() {
        return Thread.currentThread().getName() + "-" + Thread.currentThread().getId();
    }
}
