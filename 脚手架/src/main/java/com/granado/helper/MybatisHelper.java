package com.granado.helper;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Author: Yang Songlin
 * @Date: 2019/12/6 10:22 下午
 */
@Data
@Slf4j
public class MybatisHelper {

    private static final SqlSessionFactory sqlSessionFactory;

    @Getter
    private static final SqlSession sqlSession;

    static {
        sqlSessionFactory = createSqlSessionFactory("config.xml");
        sqlSession = createSqlSession(sqlSessionFactory);
    }

    private static SqlSessionFactory createSqlSessionFactory(MybatisConfiguration configuration) {
        SqlSessionFactory sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(configuration);
        return sqlSessionFactory;
    }

    private static SqlSessionFactory createSqlSessionFactory(String configFileName) {
        try {
            InputStream is = MybatisHelper.class.getResourceAsStream(configFileName);
            InputStreamReader reader = new InputStreamReader(is);
            SqlSessionFactory sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(reader);
            return sqlSessionFactory;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("configuration file can't be found, {}", e);
            return null;
        }
    }

    private static SqlSession createSqlSession(SqlSessionFactory sqlSessionFactory) {
        return sqlSessionFactory.openSession();
    }
}
