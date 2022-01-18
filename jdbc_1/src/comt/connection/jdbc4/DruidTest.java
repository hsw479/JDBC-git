package comt.connection.jdbc4;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

public class DruidTest { // 测试获得一个Druid连接池中的连接


    public void getConnectionDruid() throws Exception {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");

        Properties pros = new Properties();
        pros.load(is);

        //创建Druid数据库连接池
        DataSource source = DruidDataSourceFactory.createDataSource(pros); // 接收一个Properties对象 相当于Class.forName();
        Connection conn = source.getConnection();
        System.out.println(conn);
    }
}
