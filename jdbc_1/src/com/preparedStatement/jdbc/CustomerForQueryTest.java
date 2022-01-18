package com.preparedStatement.jdbc;

import com.bean.Customers;
import com.util.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.*;

 /*
    针对于customers表的查询操作
sql = "select name,email,birth from customers where name = ?";
        Customers customer1 = queryForCustomers(sql,"周杰伦");
        System.out.println(customer1);
*/

public class CustomerForQueryTest {

    
    public void testQuery(){
        String sql = "select id,name,birth,email from customers where id = ?";//占位符的目的：为了防止sql注入
        Customers customer = queryForCustomers(sql,10);
        System.out.println(customer); //打印查询结果

    }


    //针对customers表的通用查询操作
    public Customers queryForCustomers(String sql, Object...args){

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
         conn = JDBCUtils.getConnection();
         //2.预编译sql语句，返回PreparedStatement的实例化对象
         ps = conn.prepareStatement(sql);
         //3.填充占位符
         for (int i = 0; i < args.length; i++) {
             ps.setObject(i + 1, args[i]);  // 注意：后面是args[]数组 （可变形参有几个 args里面就有几个索引位）
         }
         //4.执行，并返回一个结果集对象
         rs = ps.executeQuery();
         //5.**获取结果集的元数据：ResultSetMetaData
         ResultSetMetaData rsmd = rs.getMetaData();
         //6.**通过ResultSetMetaData获取结果集中的列数
         int columnCount = rsmd.getColumnCount();

         if (rs.next()) {  //查到一行结果 就才创建对象
             Customers cust = new Customers();
             for (int i = 0; i < columnCount; i++) {     //处理结果集一行数据中的每一个列

                 Object columnValue = rs.getObject(i + 1); //获取列/字段的值 (每个字段的值的类型不同 所以用Object类)

                 String columnName = rsmd.getColumnName(i + 1);  //获取每个列/字段的列名  mysql的索引位是从1开始的，所以i+1

                 //***给cust对象指定的columnName属性，赋值为columnValue：通过反射给属性赋值
                 Field field = Customers.class.getDeclaredField(columnName); //获取指定名称的成员变量(不考虑修饰符)
                 field.setAccessible(true);   // 暴力反射：忽略访问权限修饰符的安全检查  这样可以提高程序的运行效率
                 field.set(cust , columnValue); // (想给哪个对象设置什么值)

             }
             return cust; //将属性和值返回到Customers类中
         }

     }catch(Exception e){
         e.printStackTrace();
     }finally{
         JDBCUtils.closeResource(conn,ps,rs);
     }

        return null;
    }


/*

//  查询Customers表中id=20的记录信息

    
    public void testQuery1() { 

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = JDBCUtils.getConnection(); //接收返回值conn

            String sql = "select id,name,email,birth from customers where id = ?"; //变量就用占位符 表和字段这种不要用
            ps = conn.prepareStatement(sql);

            ps.setObject(1,13); //完善占位符 id=20

            // 执行，并返回结果集
            rs = ps.executeQuery();  //将结果集封装在rs里面

            //处理结果集
            if (rs.next()) {  // next()：判断结果集的下一条是否有数据，如果有数据返回true，并且指针下移；如果没有，则返回false，指针不下移

                //获取当前这条记录的各个字段的数据值
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String email = rs.getString(3);
                Date birth = rs.getDate(4);

                //方式一：
                // System.out.println("id= "+id+",name= "+name+",email= "+email+",birth= "+birth);   比较麻烦
                //方式二：
                // Object ob = new Object[]{id,name,email,birth};

                //方式三：将数据封装成一个对象（推荐）
                Customers customers = new Customers(id, name, email, birth);
                System.out.println(customers); //查出一个结果 输出一个对象
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭资源
            JDBCUtils.closeResource(conn,ps,rs);
        }

    }

 */



}
