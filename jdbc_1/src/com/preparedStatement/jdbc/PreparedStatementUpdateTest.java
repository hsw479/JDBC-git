package com.preparedStatement.jdbc; // 增删改操作

/*
  使用PreparedStatement来替换Statement，实现对数据库的增删改查操作
   分为两部分： 1、增删改  2、查

   Statement存在两个问题：1.需要拼接sql语句  2.会出现sql注入问题(不进行预编译，直接将SQL语句去操作，而SQL语句中可能会出现问题)
   PrepareStatement 因为会进行预编译，在没有填充占位符之前，就将SQL语句进行编译缓存，只留下占位符等待填充

    PreparedStatement其他好处：
        1.PreparedStatement可以操作Blob类型的数据(以流的形式)，而Statement做不到
        2.PreparedStatement可以实现更高效的批量操作
*/

import com.util.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;


public class PreparedStatementUpdateTest {

        //通用的增删改操作(1.针对增删改 2.针对不同的表)     将可变形参args看成 数组
        public static void update(String sql,Object ...args){ //传入sql语句和可变(多个)形参 并且sql中占位符的个数与可变形参的长度相同!
                Connection conn = null;
                PreparedStatement ps = null;

                try {
                    //1.获取数据库连接
                    conn = JDBCUtils.getConnection(); //调用JDBCUtils类中的getConnection()方法 并用Connection对象接收
                    //2.预编译sql语句，返回PreparedStatement的实例化对象
                    ps = conn.prepareStatement(sql);
                    //3.填充占位符
                    for (int i = 0; i < args.length; i++) {
                            ps.setObject(i + 1, args[i]);   // 注意：MySQL的索引位从1开始 数组从0开始 （可变形参有几个 args里面就有几个索引位）
                    }
                    //4.执行sql语句
                    ps.execute();
            }catch(Exception e){
                    e.printStackTrace();
            }finally{
                    //5.关闭连接 释放资源
                    JDBCUtils.closeResource(conn,ps);

            }



        }


    public static void main(String[] args) {

        //四、修改order表中order_id=4的姓名(使用通用的操作)
           String sql = "update `order` set order_name = ? where order_id = ?";  // 注意 ：表名要加着重号``  （防止是关键字）
           update(sql,"CC",4);

/*       三、删除customers表中id=3的记录(使用通用的操作)
            String sql = "delete from customers where id=?";
            update(sql,3);      // 传入sql语句和id号
*/


/*
         // 二、修改customers表的一条记录 （使用方法 便利格式）

            Connection conn = null;
            PreparedStatement ps = null;

            try{
            //1.获取数据库连接
            conn = JDBCUtils.getConnection();
            
            //2.预编译sql语句，返回PreparedStatement的实例化对象
            String sql = "update customers set name = ? where id=?"; //  ? 为占位符
            ps = conn.prepareStatement(sql); //conn.prepareStatement(String sql)： 为预编译 ps对象知道要干什么

            //3.填充占位符
            ps.setObject(1,"莫扎特");
            ps.setInt(2,18);       // ps.setObject(2,18);也可以

            //4.执行sql语句
            ps.execute();
            }catch(Exception e){
                    e.printStackTrace();
            }finally{
             //5.关闭连接 释放资源
             JDBCUtils.closeResource(conn,ps);
            }
*/


/*

         // 一、向customers表添加一条记录 （完整格式）

            Connection conn = null;
            PreparedStatement ps = null;

            // 1.获取4个信息
            try{
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc");  //获取系统的类加载器

            Properties pros = new Properties();
            pros.load(is); //将jdbc文件中的内容(一次性全部)读取到(字节输入)流中 并存储到Properties集合中

            String driverClass = pros.getProperty("driverClass");
            String url = pros.getProperty("url");
            String user = pros.getProperty("user");
            String password = pros.getProperty("password");

            // 2.注册驱动
            Class.forName(driverClass);

            // 3.建立数据库连接
            conn = DriverManager.getConnection(url,user,password);

            // 4.预编译sql语句，返回PreparedStatement的实例化对象
            String sql = "INSERT INTO customers(name,email,birth) VALUES(?,?,?)";  // ?代表 占位符
            ps = conn.prepareStatement(sql);

            // 5.填充占位符 方法一：
            ps.setString(1,"刘亦菲");
            ps.setString(2,"liuyf@123.com");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //设置日期类型 m：为分钟
            Date date = sdf.parse("1987-8-25"); //将日期字符串型式 转为 日期型式
            ps.setDate(3, new java.sql.Date(date.getTime()));

            // 方法二：【推荐】
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]); //不管什么类型 都可以进行填充 (方便)
            }

            // 6.执行sql操作
            ps.execute();
            }catch(Exception e){
                    e.printStackTrace();
            }finally {
                    // 7.释放资源
                   try {  if(ps != null)
                           ps.close();
                   }catch(Exception e){
                           e.printStackTrace();
                   }

                    try {   if(conn != null)
                            conn.close();
                    } catch (Exception e) {
                            e.printStackTrace();
                    }
            }

*/


    }
}
