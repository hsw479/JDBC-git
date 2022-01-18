package comt.Select;

import comt.bean.Customers;
import comt.util.JDBCUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.*;

public class QueryDruid {

    // 使用Druid数据库连接池进行查询操作
    @Test
    public void testSelect()  { // 不需要关闭数据库连接池(连接也不需要关闭)
        Connection conn = null;
    try{
        conn = JDBCUtils.getConnectionDruid();
        String sql = "select id,name,email,birth from customers where id = ?";
        Customers cust = select(conn, Customers.class, sql, 1);
        System.out.println(cust);
     }catch(Exception e){
        e.printStackTrace();
    }
    }

       public <T> T select(Connection conn,Class<T> clazz,String sql,Object...args) {
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
                   Object columnValue = rs.getObject(i + 1);    // 获取每个字段的值
                   String columnLabel = rsmd.getColumnLabel(i + 1); // 获取每个字段的别名

                   Field field = clazz.getDeclaredField(columnLabel);
                   field.setAccessible(true);
                   field.set(t, columnValue);

               }
               return t; // 将值返回到19行Customers.class
           }
       }catch(Exception e){
           e.printStackTrace();
       }finally{
           JDBCUtils.closeResource(null,ps,rs);
       }

           return null;
       }



}
