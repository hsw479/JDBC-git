package comt.Dao.jdbc2;

import comt.bean.Customers;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

public class CustomersDAOImpl extends BaseDAO implements CustomersDAO{

    @Override
    public void insert(Connection conn, Customers cust) {

        String sql = "insert into customers(name,email,birth) values(?,?,?)";
        int i = update2(conn, sql, cust.getName(), cust.getEmail(), cust.getBirth());
        if(i > 0){
            System.out.println("修改成功!");
        }else{
            System.out.println("修改失败!");
        }
    }

    @Override
    public void deleteById(Connection conn, int id) {

        String sql = "delete from customers where id =?";
        update2(conn,sql,id);

    }

    @Override
    public void update2(Connection conn, Customers cust) {

        String sql = "update customers set name=?,email=?,birth=? where id = ?";
        update2(conn,sql,cust.getName(),cust.getEmail(),cust.getBirth(),cust.getId());
    }

    @Override
    public Customers getOneById(Connection conn, int id) throws Exception {
        String sql = "select id,name,email,birth from customers where id =?";
        Customers customer = Query(conn, Customers.class, sql, id);
        return customer;
    }

    @Override
    public List<Customers> getAll(Connection conn) throws Exception {
        String sql = "select id,name,email,birth from customers";
        List<Customers> list = getForList(conn, Customers.class, sql);
        return list;
    }

    @Override
    public Long getCount(Connection conn) throws Exception {

        String sql = "select count(*) from customers";
        return getValue(conn, sql);

    }

    @Override
    public Date getMaxBirth(Connection conn) throws Exception {
        String sql = "select max(birth) from customers";
       return getValue(conn,sql);

    }
}
