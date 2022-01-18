package comt.Dao.jdbc2;

import comt.bean.Customers;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

public interface CustomersDAO {  // *此接口用于规范针对于customers表的常用操作* (提一嘴 这个接口不写也罢  直接写实现类也行)
                     // 但是设置接口是为了规范,每个实现类都要连接这个接口 所以接口里面一定要有增删改查方法,单一继承的话你可能会漏掉增删改查中的一项
                    //  此接口是一个规范，方便以后扩展

    // 1.将cust对象添加到数据库中
    public abstract void insert(Connection conn, Customers cust); // 将要添加的信息封装到cust对象中，最后通过cust.getXxx()来调用

    // 2.针对指定的id，删除表中的一条记录
    public abstract void deleteById(Connection conn,int id);

    // 3.针对内存中的cust对象，去修改数据表中指定的记录                    // 增改：可能会涉及到多个数据的变动，所以用到对象(将多个数据用对象进行封装)
    public abstract void update2(Connection conn,Customers cust); // 将新信息封装到cust对象中，再将原来记录的信息 修改为cust对象中的新信息

    // 4.针对指定的id查询得到对应的一条记录
    public abstract Customers getOneById(Connection conn,int id) throws Exception;

    // 5.查询表中的所有记录构成的集合
    public abstract List<Customers> getAll(Connection conn) throws Exception;

    // 6.返回数据表中数据的条目/个数
    // 也就是求count(*) 求总行数，得到一个 一行一列 的结果；返回的类型Long类型（可用Number接收，Number是所有包装类的父类）
    public abstract Long getCount(Connection conn) throws Exception;

    // ps：返回数据表中最大的生日
    public abstract Date getMaxBirth(Connection conn) throws Exception;

}
