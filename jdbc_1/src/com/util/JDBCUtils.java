package com.util;


import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
        pros.load(is); //load方法：将jdbc文件中的内容(一次性全部)读取到(字节输入)流中 并存储到Properties集合中

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


}
