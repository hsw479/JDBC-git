package com.blob.jdbc;

import com.util.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;

    /*
        使用PreparedStatement实现批量插入数据的操作：
        1.使用Statement  2.使用PrepareStatement   方式二 效率高于 方式一

        update、delete本身就具有批量操作的效果(去掉where,操作整个表的数据)

        方式一：使用Statement
        Connection conn = JDBCUtils.getConnection();
        Statement st = conn.createStatement();
        for(int i = 1;i < 20000;i++){
            String sql = "insert into goods(name) values('name_"+i+"')";   sql语句放在for里面 每次循环一次sql语句都要在线生成一个 容易占内存
            st.execute(sql);
        }

    */


public class InsertTest {

    //题目：向goods表中插入20000条数据
    //方式二：使用PreparedStatement进行批量插入操作
//    @Test
    public void testInsert1() throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            long start = System.currentTimeMillis(); // 记录程序从这里开始的时间 (毫秒为单位 1秒=1000毫秒)

            conn = JDBCUtils.getConnection();
            String sql = "insert into goods(name) values (?)"; //sql语句在外面就进行了预编译，缓存进内存 for里面只需循环填充占位符即可，不占内存
            ps = conn.prepareStatement(sql);

            for (int i = 1; i <= 100000; i++) {
                ps.setObject(1, "name" + i);

                ps.execute(); // 但每填充一次占位符，都会与数据库服务器交互一次
            }

            long end = System.currentTimeMillis(); // 记录程序到这里结果的时间
            System.out.println("花费的时间：" + (end - start));   // 经过测试 插入20000数据 花费的时间：24850 (24.85秒)
                                                                // 经过测试 插入100000数据 花费的时间：120809 (120.809秒)

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }


    }



    //方式三：设置批量操作 (优化方式二的方法)
//    @Test
    public void testInsert2() throws Exception {

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();
            String sql = "insert into goods(name) values (?)";
            ps = conn.prepareStatement(sql);

            for (int i = 1; i <= 100000; i++) {
                ps.setObject(1, "name_" + i); // "name_"+i ：为填充占位符里面的内容 例：name_1 name_2

               // 1.积攒sql语句(先不执行)
                ps.addBatch(); //将对象ps添加到批处理命令中

               // 2.积攒到一定程度后 再执行batch
                if(i % 500==0){
                    ps.executeBatch();

                // 3.清空batch
                    ps.clearBatch();
                }

            }

            long end = System.currentTimeMillis();       // 放在try中
            System.out.println("花费的时间：" + (end - start));  // 经过测试 插入20000数据 花费的时间：24706 (24.706秒)
                                                              // 经过测试 插入100000数据 花费的时间：124036 (124.036秒)

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }


    }


//    @Test
    //方式四：设置连接不允许自动提交数据 (优化方式三的方法) 【推荐】
    public void testInsert3() throws Exception {

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();
            //4.关闭自动提交功能
            conn.setAutoCommit(false);

            String sql = "insert into goods(name) values (?)";
            ps = conn.prepareStatement(sql);

            for (int i = 1; i <= 100000; i++) {
                ps.setObject(1, "name_" + i); // "name_"+i ：为填充占位符里面的内容 例：name_1 name_2

                // 1.积攒sql语句(先不执行)
                ps.addBatch(); //将对象ps添加到批处理命令中

                // 2.积攒到一定程度后 再执行batch
                if(i % 500==0){
                    ps.executeBatch();

                    // 3.清空batch
                    ps.clearBatch();
                }

            }
            //5.统一将所有数据提交
            conn.commit();

            long end = System.currentTimeMillis();       // 放在try中
            System.out.println("花费的时间：" + (end - start));   // 经过测试 插入20000数据 花费的时间：2124 (2.124秒)
                                                               // 经过测试 插入100000数据 花费的时间：9062 (9.062秒)

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }


    }


}
