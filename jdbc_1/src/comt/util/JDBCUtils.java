package comt.util;


import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;


/*
    操作数据库的工具类

 */

public class JDBCUtils {

    public static Connection getConnection() throws Exception {  // *获取数据库连接的操作 方法*

        //1.读取配置文件中的4个基本信息
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc");  // 获取系统类加载器 将文件加载到内存，以流的形式
                                                         //getResourceAsStream("jdbc"):通过类加载器在classPath目录下获取资源.并且是以流的形式。
        Properties pros = new Properties();
        pros.load(is); //load(输入流)：将jdbc文件中的内容(一次性全部)读取到(字节输入)流中 并存储到Properties集合中

        String url = pros.getProperty("url"); // 通过键对象来返回得到映射的值对象
        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String driverClass = pros.getProperty("driverClass");

        //2.使用反射 来加载驱动
        Class.forName(driverClass); //获取Driver实现类对象

        //3.建立/获取数据库连接
        Connection conn = DriverManager.getConnection(url, user, password);

        return conn;
    }


    public static void closeResource(Connection conn, Statement ps){  // *关闭连接和Statement的操作 方法*
        // 7.释放资源
        try {   if(conn != null)
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {  if(ps != null)
            ps.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

                                                                                    // 利用方法重载
    public static void closeResource(Connection conn, Statement ps, ResultSet rs) {  // *关闭连接，Statement和ResultSet的操作 方法*

        try{   if(conn != null)
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{  if(ps != null)
              ps.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        try{   if(rs!=null)
               rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    // 使用DBUtils工具进行资源的关闭(两种方式)
    public static void closeResourceDBUtils(Connection conn, Statement ps, ResultSet rs) {
        //方式一：
/*
        try {
            DbUtils.close(conn);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        try {
            DbUtils.close(ps);
        } catch (SQLException e2) {
            e2.printStackTrace();
        }

        try {
            DbUtils.close(rs);
        } catch (SQLException e3) {
            e3.printStackTrace();
        }
*/
        //方式二：【推荐】
        DbUtils.closeQuietly(conn);
        DbUtils.closeQuietly(ps);
        DbUtils.closeQuietly(rs);

    }





    //2、使用DBCP数据库连接池技术来获取数据库连接

    //创建一个数据库连接池(核心操作只要执行一次即可 所以放在方法外面。 放在里面的话：外界访问一次就要执行创建一次)
    private static DataSource ds = null; // 当成属性

   static{   // 静态代码块：随着类的加载而加载 但只加载一次
             InputStream is = null;
       try {
                is = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");     // 方式1：(通过类加载器获取)
                Properties pros = new Properties();
                pros.load(is);

                ds = BasicDataSourceFactory.createDataSource(pros);

                }catch(Exception e){
                e.printStackTrace();
                }
        }

    public static Connection getConnectionDBCP() throws Exception {

        // 获取流的两种方式
     //   FileInputStream is = new FileInputStream(new File("src/dbcp.properties"));// 方式2：(通过文件输入流获取)

        Connection conn = ds.getConnection();

        return conn;
    }





    // 3、使用Druid数据库连接池技术
    private static DataSource source1 = null; // 私有静态化：方便当前类里的方法直接调用，而又不被其他类使用

    static{     //静态代码块：随着类的加载而加载 但只执行一次
                 InputStream is = null;
            try{
                is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties"); // 方式1：(通过类加载器获取)

                Properties pros = new Properties();
                pros.load(is);

                source1 = DruidDataSourceFactory.createDataSource(pros);

                 }catch(Exception e){
                e.printStackTrace();
                 }
         }

    public static Connection getConnectionDruid() throws Exception {  // 不用关闭连接池

        Connection conn = source1.getConnection();
        return conn;
    }



}
