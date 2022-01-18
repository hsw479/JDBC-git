package comt.Dao.jdbc2;

import com.util.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

    /*
            DAO：data(base) access object 数据(库)访问对象：用java代码来操作数据库的一些功能和方法

             封装了针对于数据表的通用的操作
     */

public abstract class BaseDAO { // 设置为抽象类 为后续操作提供通用的方法，不在本类中创建实例对象

    // 1、通用的增删改操作(针对不同的表)
    public int update2(Connection conn, String sql, Object...args) { // 通过参数的方式 将连接传入进来

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


    // 2、 通用的查询操作，返回数据表中的一条记录
    public <T> T Query(Connection conn, Class<T> clazz, String sql, Object... args) throws Exception {

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

            if (rs.next()) { // 判断结果集的下一条是否有数据，如果有数据返回true，指针下移指向下一条数据；反之，返回false，指针不移动，循环结束
                T t = clazz.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    Object columnValue = rs.getObject(i + 1); // 获取对应索引位的值
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


    // 3、 通用的查询操作，返回数据表中的多条记录
    public <T> List<T> getForList(Connection conn, Class<T> clazz, String sql, Object... args) throws Exception {

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

            ArrayList<T> list = new ArrayList<>(); //创建对象 之后需要添加数据

            while (rs.next()) {  // if只能循环一次，只返回一条记录
                T t = clazz.newInstance();  //创建对象(以此来对应数据表的一条记录)
                // 给t对象指定的属性赋值的过程
                for (int i = 0; i < columnCount; i++) {
                    Object columnValue = rs.getObject(i + 1);
                    String columnLabel = rsmd.getColumnLabel(i + 1);
                    // 体现反射的动态特性
                    Field field = clazz.getDeclaredField(columnLabel); //*** clazz.getDeclaredField()：从clazz类中去获取指定名称的属性/成员变量 ***
                    field.setAccessible(true);
                    field.set(t, columnValue); //再赋值
                }
                list.add(t);  // 赋完值后，将对象t添加进集合(因为要查询多条记录 创建多个对象 所以要存储到集合中)
            }
            return list; //当全部添加进去后,一定要返回,否则集合里面没有值(返回值类型为泛型集合)

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null, ps, rs);
        }

        return null;
    }


    /*
        E - Element (在集合中使用，因为集合中存放的是元素)
        T - Type（Java 类）
        K - Key（键）
        V - Value（值）
        N - Number（数值类型）
        ？ -  表示不确定的java类型
    */
    // 4、用于查询特殊值的通用方法(查询行数)
    public <E> E getValue(Connection conn, String sql, Object... args) throws Exception { //定义一个泛型方法<E> 类型为E(自定义类型)

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();
            if (rs.next()) {
                return (E) rs.getObject(1);  // 强转为类型E 并返回该值（获取索引为1的结果集的值）
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null, ps, rs);
        }

        return null;

    }


}
