package comt.connection.jdbc4;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.junit.jupiter.api.Test;


import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

public class DBCPTest {

    @Test
    // 方式一：【不推荐】
    // 测试DBCP数据库连接池技术
    public void testGetConnection() throws Exception {

        // 创建DBCP数据库连接池
        BasicDataSource source = new BasicDataSource();

        // 设置基本信息
        source.setDriverClassName("com.mysql.jdbc.Driver");
        source.setUrl("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone = GMT");
        source.setUsername("root");
        source.setPassword("479");

        // 设置数据库连接池其他的管理操作
        source.setInitialSize(5); // 初始化5个连接

        Connection conn = source.getConnection();
        System.out.println(conn);

    }

    @Test
    // 方式二：【推荐使用配置文件的方式】
    // 使用配置文件
    public void testGetConnectionDBCP() throws Exception {

        // 方式1：获取流的两种方式
    //    InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");

        // 方式2：
        FileInputStream is = new FileInputStream( new File("src/dbcp.properties"));

        Properties pros = new Properties();
        pros.load(is);

        DataSource ds = BasicDataSourceFactory.createDataSource(pros);

        Connection conn = ds.getConnection();
        System.out.println(conn);

    }


}
