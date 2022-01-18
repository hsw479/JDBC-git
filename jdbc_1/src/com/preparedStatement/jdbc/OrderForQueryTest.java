package com.preparedStatement.jdbc;


import com.bean.Order;
import com.util.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.*;

public class OrderForQueryTest {

    
    public void testQuery2() {
        String sql = "select order_id orderId,order_name orderName,order_date orderDate from `order` where order_id = ?";
        Order order = orderForQuery(sql, 2);
        System.out.println(order);
    }

    //通用查询操作
    public Order orderForQuery(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql); //预编译sql语句之后，马上要进行占位符填充
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery(); //将表的结果集 存储到rs中

            ResultSetMetaData rsmd = rs.getMetaData(); //得到表的元数据
            int columnCount = rsmd.getColumnCount(); //得到表的列数

            if (rs.next()) {
                Order order = new Order();
                for (int i = 0; i < columnCount; i++) {
                    Object columnValue = rs.getObject(i + 1);  //获取每个列/字段的值：通过ResultSet  i+1：mysql中索引从1开始，这里代表从第一个列名开始操作

                    // 获取列的列名：通过ResultSetMetaData   String columnName = rsmd.getColumnName(i + 1); (不推荐使用)
                    // 获取列的别名：通过ResultSetMetaData  假如没起别名 获取的还是列/字段名
                    String columnLabel = rsmd.getColumnLabel(i + 1); // 目的：防止表中列名有特殊符号(含有Java不支持的)

                    //通过反射：将指定的值columnValue赋值给对象指定名columnLabel的属性
                    Field field = Order.class.getDeclaredField(columnLabel); // getDeclaredField()：获取指定名称的成员变量(所以别名要与成员变量/属性名相同)
                    field.setAccessible(true); //忽略访问权限修饰符的安全检查  //这样可以提高程序的运行效率
                    field.set(order, columnValue); //通过Order类的对象给其成员变量赋值
                }  // *注意*：是Order.class.getDeclaredField(columnLabel); 不是OrderForQuery.class.getDeclaredField(columnLabel);
                         //因为你要获取的成员变量来自Order类，而不是OrderForQuery类(该类也不存在成员变量)

                return order;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }

        return null;

    }


/*
        //查询order_id=4的记录
        
        public void testQuery1() {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
            conn = JDBCUtils.getConnection();

            String sql = "select order_id,order_name,order_date from `order` where order_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setObject(1, 4);

            rs = ps.executeQuery();
            if (rs.next()) {
                int id = (int) rs.getObject(1);  // 强制类型转换
                String name = (String) rs.getObject(2);
                Date date = rs.getDate(3);

                Order order = new Order(id,name,date);
                System.out.println(order);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn,ps,rs);
        }

     }

*/

}
