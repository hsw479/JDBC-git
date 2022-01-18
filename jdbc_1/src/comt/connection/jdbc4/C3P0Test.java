package comt.connection.jdbc4;


import java.sql.Connection;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0Test {


    public void tesGetConnection() throws Exception {

        // 方式一：获取c3p0数据库连接池
        ComboPooledDataSource cpds = new ComboPooledDataSource(); // DataSource接口的实现类ComboPooledDataSource
        cpds.setDriverClass( "com.mysql.jdbc.Driver" ); // 提供具体路径
        cpds.setJdbcUrl( "jdbc:mysql://localhost:3306/test" );
        cpds.setUser("root");
        cpds.setPassword("479");

        // 通过设置相关的参数，对数据库连接池进行管理...
        cpds.setInitialPoolSize(5); // 设置初始时数据库连接池中的连接数

        Connection conn = cpds.getConnection();
        System.out.println(conn);

        // 销毁c3p0数据库的连接池
//        DataSources.destroy(cpds);
    }



    public void tesGetConnectionC3p0() throws Exception {
        // 方式二：获取c3p0数据库连接池

        ComboPooledDataSource cpds = new ComboPooledDataSource("hellC3p0"); // 自定义的配置起的名
        Connection conn = cpds.getConnection();
        System.out.println(conn);
    }



}
