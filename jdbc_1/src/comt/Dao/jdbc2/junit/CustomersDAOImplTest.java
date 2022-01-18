package comt.Dao.jdbc2.junit;

import comt.Dao.jdbc2.CustomersDAOImpl;
import comt.bean.Customers;
import comt.util.JDBCUtils;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

public class CustomersDAOImplTest {

    private CustomersDAOImpl dao = new CustomersDAOImpl();



    // 3、使用Druid数据库连接池操作来添加

    
    public void testInsertDruid(){
        Connection conn = null;
        try{
            conn = JDBCUtils.getConnectionDruid();
            Customers cust = new Customers(1,"于小飞","xiaofei@126.com",new Date(42309283482L));
            dao.insert(conn,cust);

            System.out.println("添加成功");

        }catch(Exception e){
            e.printStackTrace();
        }finally{
         JDBCUtils.closeResource(conn,null);
        }
    }


    // 2、使用DBCP数据库连接池操作来查询

    
    public void testOneByIdDruid() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnectionDBCP();

            Customers cust = dao.getOneById(conn, 10);
            System.out.println(cust);

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn,null);
        }
    }

//----------------------------------------------------------------------------------------------------------

    
    public void tsetInsert() {

            Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            Customers cust = new Customers(30,"皇上", "hs@121.com", new Date(43532453245L)); // 需要加id
            dao.insert(conn, cust);
            System.out.println("添加成功!");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn,null);
        }

    }

    
    public void testDeleteById() {

        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            dao.deleteById(conn,27);

            System.out.println("删除成功!");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn,null);
        }
    }

    
    public void testUpdate2() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();

            Customers cust = new Customers(18,"贝多芬","bdf@100.com",new Date(21243233245L)); // 修改id为18的信息(id不改 其他的改)
            dao.update2(conn,cust);

            System.out.println("修改成功!");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn,null);
        }
    }

    
    public void testOneById() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();

            Customers c = dao.getOneById(conn, 10);
            System.out.println(c);

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn,null);
        }
    }


    
    public void testAll() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            List<Customers> list = dao.getAll(conn);
            list.forEach(System.out::println);

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn,null);
        }
    }


    public void getCount() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            Long c = dao.getCount(conn);
            System.out.println("表中的记录数为："+c);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn,null);
        }
    }

    
    public void getMaxBirth() {
            Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();

            java.util.Date maxBirth = dao.getMaxBirth(conn);
            System.out.println("最大的生日："+maxBirth);

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn,null);
        }

    }
}