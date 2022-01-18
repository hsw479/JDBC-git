package com.preparedStatement.jdbc;

import com.bean.Customers;
import com.bean.Order;
import com.util.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

    /*
        使用PreparedStatement实现对不同表的通用的查询操作

     */

public class DifferentQueryTest {

      //针对不同的表 可以查询多条记录
    public void testList() throws Exception {
        String sql = "select id,name,email,birth from customers where id < ?";
        List<Customers> list = getForList(Customers.class, sql, 5);  // 类名.class:用于参数传递
        list.forEach(System.out::println); // 遍历集合专用： 对象名.forEach();

        String sql1 = "select order_id orderId,order_name orderName,order_date orderDate from `order` ";
        List<Order> list1 = getForList(Order.class, sql1); // Object... args 也可以支持不写占位符
        list1.forEach(s -> System.out.println(s)); //   里面可用方法引用(符)，也可以用Lambda表达式（s为自动生成的流对象）
    }

        // 针对不同的表，会传入不同的JavaBean类，所以需要用到泛型  (Class<T> clazz：传入类对象，是为了更好的获取JavaBean类中的属性)
   public <T> List<T> getForList(Class<T> clazz, String sql, Object... args) throws Exception {

            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            ArrayList<T> list = new ArrayList<>(); //创建对象 之后需要添加数据

            while (rs.next()) {  // if只能循环一次
                T t = clazz.newInstance();  //创建对象(以此来对应数据表的一条记录)
                // 给t对象指定的属性赋值的过程
                for (int i = 0; i < columnCount; i++) {
                    Object columnValue = rs.getObject(i + 1);
                    String columnLabel = rsmd.getColumnLabel(i + 1);
                    // 体现反射的动态特性
                    Field field = clazz.getDeclaredField(columnLabel); //*** clazz.getDeclaredField()：从clazz类中去获取指定名称的属性/成员变量 ***
                    field.setAccessible(true); //这样可以提高程序的运行效率
                    field.set(t, columnValue); //再赋值
                }
                list.add(t);  // 赋完值后，将对象t添加进集合(因为要查询多条记录 创建多个对象 所以要存储到集合中)
            }
            return list; //当全部添加进去后,一定要返回,否则集合里面没有值(返回值类型为泛型集合) 返回到第25和29行(List集合接收的地方)

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }

        return null;
    }

//------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------

        //  针对不同的表的通用的查询操作，查询表中一条记录
    public void testGetInstance() {
        String sql = "select id,name,birth,email from customers where id = ?";
        Customers cust = getInstance(Customers.class, sql, 12);
        System.out.println(cust);

        String sql1 = "select order_id orderId,order_name orderName,order_date orderDate from `order` where order_id = ?";
        Order order = getInstance(Order.class, sql1, 1);
        System.out.println(order);
    }


    //此处<T>：为了声明为泛型方法   第二个T和第三个T：具有一致性，都是类的类型(Order,Customers)
    public <T> T getInstance(Class<T> clazz, String sql, Object... args) {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            if (rs.next()) {
                T t = clazz.newInstance(); // **此处T 对应上面Class<T>  为类的类型(Order,Customers)**   通过 Class类的对象.newInstance()方法生成一个实例(对象)
                for (int i = 0; i < columnCount; i++) {
                    Object columnValue = rs.getObject(i + 1);
                    String columnLabel = rsmd.getColumnLabel(i + 1);
                    // 注意：此处Class类不需要加.class
                    Field field = clazz.getDeclaredField(columnLabel); //*** clazz.getDeclaredField()：从clazz类中去获取指定名称的成员变量 ***
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }
                return t; // 只查询一条记录 返回一个对象即可

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }

        return null;

    }


}
