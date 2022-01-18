package comt.Transaction.jdbc1;

import com.util.JDBCUtils;
import org.junit.jupiter.api.Test;


import java.lang.reflect.Field;
import java.sql.*;

    /*
        哪些操作会导致数据的自动提交？
            1、DDL操作一旦执行，都会自动提交
                    (set autocommit = 0; 对DDL操作失效)
            2、DML默认情况下，一旦执行，就会自动提交
                    (我们可以通过 set autocommit = 0; 的方式取消DML操作的自动提交)
            3、默认在关闭连接时，会自动的提交数据


    注意：*** 一旦要用上事务 就要将自动提交功能关闭conn.setAutoCommit(false)，当出现意外，数据还可以进行回滚 ***

     */

public class TransactionTest {

//----------------------------------未考虑数据库事务情况下的转账操作------------------------------------------


    // 针对于数据表uesr_table： AA用户给BB用户转账100
    @Test
    public void testUpdate() {
        String sql = "update user_table set balance = balance - 100 where user = ?";
        update1(sql, "AA");

        String sql1 = "update user_table set balance = balance - 100 where user = ?";
        update1(sql1, "BB");
    }


    //通用的增删改操作(针对不同的表) ------version 1.0
    public int update1(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            //1.获取数据库连接
            conn = JDBCUtils.getConnection();
            //2.预编译sql语句，返回PreparedStatement的实例化对象
            ps = conn.prepareStatement(sql);
            //3.填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);   // 注意：MySQL的索引位从1开始 数组从0开始 （可变形参有几个 args里面就有几个索引位）
            }
            //4.执行sql语句
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.关闭连接 释放资源
            JDBCUtils.closeResource(conn, ps);

        }
        return 0; // 返回受影响的行数

    }


    //------------------------------------考虑数据库事务后的转账操作------------------------------------------

    @Test
    public void testUpdateTransaction() {
        Connection conn = null;

        try {
            //1.创建连接 (必须在所有事务之前 创建连接)
            conn = JDBCUtils.getConnection();

            //  System.out.println(conn.getAutoCommit()); 查看默认状态下自动提交的状态：true
            //2.关闭数据的自动提交功能
            conn.setAutoCommit(false);

            // 将所有 DML操作/事务 作为一个整体
            String sql = "update user_table set balance = balance - 100 where user = ?";
            update2(conn, sql, "AA"); //事务一

            //模拟中间发生异常
            //System.out.println(10 / 0);

            String sql1 = "update user_table set balance = balance + 100 where user = ?";
            update2(conn, sql1, "BB"); //事务二

            System.out.println("转账成功!");

            //3.将数据统一提交
            conn.commit(); // 数据提交代表：事务结束

        } catch (Exception e) {
            e.printStackTrace();
            //4.当出现异常，数据进行回滚
            try {
                conn.rollback();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {

            //将自动提交的状态修改回来
            try {
                conn.setAutoCommit(true);  //主要针对后面的数据库连接池做准备
            } catch (SQLException s) {
                s.printStackTrace();
            }

            //4.关闭连接
            JDBCUtils.closeResource(conn, null);  // 连接从外面传入进来，不要在里面关闭，应在外面关闭
        }

    }


    //通用的增删改操作(针对不同的表) ------version 2.0(考虑到事务：连接需要从外面传入)
    public int update2(Connection conn, String sql, Object... args) { // 通过参数的方式 将连接传入进来

        PreparedStatement ps = null;
        try {

            //1.预编译sql语句，返回PreparedStatement的实例化对象
            ps = conn.prepareStatement(sql);
            //2.填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);   // i+1：需要填充的占位符   args[i]：要填充进去的参数
            }
            //3.执行sql语句
            return ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            JDBCUtils.closeResource(null, ps);  // 连接在里面创建的 就在里面关

        }
        return 0;

    }


//---------------------------------------两个事务演示------------------------------------------------------------------

    @Test  // 演示才使用 ：事务一(查询)
    public void testTransactionSelect() throws Exception {
        Connection conn = JDBCUtils.getConnection();

        // System.out.println(conn.getTransactionIsolation()); // 1、 查看隔离级别 返回int类型的值 ( TRANSACTION_REPEATABLE_READ =4、TRANSACTION_READ_COMMITTED =2、TRANSACTION_READ_UNCOMMITTED = 1)

        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);  // 2、将隔离级别设置成：2 读已提交数据

        conn.setAutoCommit(false);// 3、取消自动提交功能

        String sql = "select user,password,balance from user_table where user = ?";
        User user = Query(conn, User.class, sql, "CC");

        System.out.println(user);
    }





    @Test  // 演示才使用 ：事务二(修改)
    public void testTransactionUpdate() throws Exception {
        Connection conn = JDBCUtils.getConnection();

        String sql = "update user_table set balance = ? where user = ?";
        update2(conn,sql,"5000","CC");

        Thread.sleep(15000);
        System.out.println("修改成功");
    }




    // 通用的查询操作，用于返回数据表中的一条记录(version 2.0：考虑上事务)
    public <T> T Query(Connection conn,Class<T> clazz, String sql, Object... args) throws Exception {

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            if (rs.next()) {
                T t = clazz.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    Object columnValue = rs.getObject(i + 1);
                    String columnLabel = rsmd.getColumnLabel(i + 1);

                    Field field = clazz.getDeclaredField(columnLabel); // 获取指定姓名的成员变量/属性
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }
                return t;

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null, ps, rs);
        }

        return null;
    }


}
