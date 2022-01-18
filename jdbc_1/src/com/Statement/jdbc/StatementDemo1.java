package com.Statement.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

/*
    JDBC两种连接方式的演示
 */
public class StatementDemo1 {
    public static void main(String[] args) throws Exception {
        //方法一
        //1. 导入驱动jar包mysql-connector-java-5.1.7-bin.jar
        //2.注册驱动 (使用反射 来获取Driver实现类对象)
        Class.forName("com.mysql.jdbc.Driver"); // com.mysql.jdbc.Driver为mysql的Driver实现类 其底层为DriverManager.registerDriver(new Driver()); 注册驱动

        //3.获取数据库连接对象
        /*提供三个基本信息
          String url = jdbc:mysql://localhost:3306/student
          String user = root
          String password =479
          Connection conn = DriverManager.getConnection(url, user, password);
         */
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "479");

        //4.定义sql语句
        String sql = "update book set price = 150 where bid = 1";

        //5.获取执行sql的对象 Statement
        Statement stmt = conn.createStatement();

        //6.执行sql
        int count = stmt.executeUpdate(sql);

        //7.处理结果 (用sout将结果打印出来)
        System.out.println(count);

        //8.释放资源 (1-2-2-1)
        stmt.close();
        conn.close();


        connectTest();
    }

   private  static void connectTest() throws Exception { //方法不能放在主方法大括号中 而是放在类大括号中
        /*方法二(final版)
         此方法的好处？
         1.实现了数据与代码的分离。实现了解耦
            (假如更换主机或数据库 不用改代码，直接改配置文件即可)
        */
        //1.读取配置文件中的4个基本信息
        InputStream is = StatementDemo1.class.getClassLoader().getResourceAsStream("jdbc");  //方法2： ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc");
        // jdbcDemo1.class.getClassLoader()：获取类加载器 //getResourceAsStream("jdbc"):通过类加载器在classPath目录下获取资源.并且是以流的形式。

        Properties pros = new Properties();
        pros.load(is); //load方法：将jdbc文件中的内容(一次性全部)读取到(字节输入)流中 并存储到Properties集合中

        String url = pros.getProperty("url"); // 通过键对象来返回得到映射的值对象
        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String driverClass = pros.getProperty("driverClass");

        //2.加载驱动
        Class.forName(driverClass);

        //3.建立/获取数据库连接
        Connection conn = DriverManager.getConnection(url, user, password);

        //4.打印连接
        System.out.println(conn);

        //5.结束并释放资源
        conn.close();

    }

}
