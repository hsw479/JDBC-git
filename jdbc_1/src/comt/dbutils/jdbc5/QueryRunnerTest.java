package comt.dbutils.jdbc5;

import comt.bean.Customers;
import comt.util.JDBCUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/*
        commons-dbutils是Apache组织提供的一个开源JDBC工具类库，封装了针对数据库的增删改查的操作

    */
public class QueryRunnerTest {

    //添加1： 向customers表中添加一条记录
    public void testInsert() throws Exception {
        Connection conn = null;

        try {
            conn = JDBCUtils.getConnectionDruid();

            QueryRunner runner = new QueryRunner();  // 创建对象 要用runner对象来调增删改查的方法

            String sql = "insert into customers(name,email,birth) values(?,?,?)";

            int insert = runner.update(conn, sql, "谢婕妤", "xiejieyu@200.com", "2001-12-11");// 调用dbutils工具里面的增删改方法
            System.out.println("添加了" + insert + "条记录");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }

    }


    // 查询1：查询customers表中的一条记录  BeanHandler<T>
    public void testQuery1() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnectionDruid();
            String sql = "select id,name,email,birth from customers where id =?";

            QueryRunner runner = new QueryRunner();

            // BeanHandler：是ResultSetHandler接口的实现类，用于封装表中的一条记录
            BeanHandler<Customers> handler = new BeanHandler<Customers>(Customers.class); // 带参的构造器

            Customers customer = runner.query(conn, sql, handler, 21); // *查询中的方法都要带有ResultSetHandler接口的实现类对象
            System.out.println(customer);   // 以对象的方式呈现

             } catch (Exception e) {
            e.printStackTrace();
             } finally {
            JDBCUtils.closeResource(conn, null);
             }
    }


    // 查询2：查询customers表中的多条记录   BeanListHandler<T>
    public void testQuery2() throws Exception {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnectionDruid();
            String sql = "select id,name,email,birth from customers where id < ?";

            QueryRunner runner = new QueryRunner();

            // BeanListHandler：是ResultSetHandler接口的实现类，用于封装表中的多条记录构成的集合
            BeanListHandler<Customers> blh = new BeanListHandler<Customers>(Customers.class); // 带参的构造器

            List<Customers> list = runner.query(conn, sql, blh, 5);// 查询中的方法都要带有ResultSetHandler接口的实现类对象
            // 1.方法引用  list.forEach(System.out::println);
            list.forEach(s -> System.out.println(s)); // 2.Lambda表达式
             } catch (Exception e) {
            e.printStackTrace();
            } finally {
            JDBCUtils.closeResource(conn, null);
            }
    }


    // 查询3：查询customers表中的一条记录   MapHandler---不需要泛型
    public void testQuery3() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnectionDruid();
            String sql = "select id,name,email,birth from customers where id = ?";

            QueryRunner runner = new QueryRunner();     // 创建对象 要用runner对象来调增删改查的方法

            // MapHandler：是ResultSetHandler接口的实现类，对应表中的一条记录
            MapHandler handler = new MapHandler();      //  将字段及相应字段的值作为map中的key和value

            Map<String, Object> map = runner.query(conn, sql, handler, 21);// 查询中 的方法都要带有ResultSetHandler接口的实现类对象
            System.out.println(map);    // 以键值对的形式呈现

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }


    // 查询4：查询customers表中的一条记录   MapListHandler---不需要泛型
    public void testQuery4() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnectionDruid();
            String sql = "select id,name,email,birth from customers where id < ?";

            QueryRunner runner = new QueryRunner(); // 创建对象 通过runner对象来调用增删改查的方法

            MapListHandler handler = new MapListHandler();

            List<Map<String, Object>> list = runner.query(conn, sql, handler, 10);  // 以Map双列集合的形式(将字段及相应字段的值作为map中的key和value)存储到List集合中
            list.forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }


    // 查询5：查询customers表中记录的个数   ScalarHandler---不需要泛型
    public void testQuery5() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnectionDruid();
            String sql = "select count(*) from customers";

            QueryRunner runner = new QueryRunner();
            ScalarHandler handler = new ScalarHandler();    //  ScalarHandler：用于查询特殊值

            Long count = (Long) runner.query(conn, sql, handler); // 强转为Long类型
            System.out.println("表中有"+count+"条记录");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }



    // 查询6：查询customers表中谁最大   ScalarHandler---不需要泛型
    public void testQuery6() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnectionDruid();
            String sql = "select max(birth) from customers";

            QueryRunner runner = new QueryRunner();
            ScalarHandler handler = new ScalarHandler();    //  ScalarHandler：用于查询特殊值

            Date maxBirth = (Date) runner.query(conn, sql, handler);
            System.out.println(maxBirth);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }



    //自定义查询(利用匿名内部类重写ResultSetHandler接口中的方法)
    public void testQuery7() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnectionDruid();
            String sql = "select id,name,email,birth from customers where id = ?";

            QueryRunner runner = new QueryRunner();

            ResultSetHandler<Customers> handler = new ResultSetHandler<Customers>() { // 使用匿名内部类的方法 重写ResultSetHandler接口中的方法
                @Override
                public Customers handle(ResultSet resultSet) throws SQLException {  // 为实现了ResultSetHandler接口的子类匿名对象

                    return new Customers(21,"孙允珠","sunyz@479.com",new Date(42020302029L)); // 将数据用对象进行封装
                }
            };

            Customers cust = runner.query(conn, sql, handler,1); // 因为重写方法里面的返回值已固定，不论传什么参数都一样的结果

            System.out.println(cust);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

}
