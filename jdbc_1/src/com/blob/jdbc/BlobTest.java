package com.blob.jdbc;

import com.bean.Customers;
import com.util.JDBCUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;

/*
        测试使用PreparedStatement操作Blob类型的数据
        (Statement无法进行操作)

    */
public class BlobTest {
    @Test
    //向数据表customers中插入Blob类型的字段
    public void testInsert() throws Exception {

        Connection conn = JDBCUtils.getConnection();
        String sql = "insert into customers(name,email,birth,photo) value(?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setObject(1, "cute"); //将Dragon填充进第一个占位符
        ps.setObject(2, "cute@123.com");
        ps.setObject(3, "2000-02-02");
        FileInputStream fis = new FileInputStream(new File("cute.jpg")); // 存储图片需要用到 文件字节输入流(一定要文件流)
        ps.setBlob(4, fis);   // 图片用流的形式存储

        ps.execute();

        JDBCUtils.closeResource(conn, ps);
    }


    @Test
    //查询数据表customers中Blob类型的字段
    public void testQuery() {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            conn = JDBCUtils.getConnection();
            String sql = "select id,name,email,birth,photo from customers where id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, 22);  // 有几个占位符 就填充几个  // *获取字段的值和填充占位符 都可以用Object类型*

            rs = ps.executeQuery();
            if (rs.next()) {

    /*  方法一：里面填索引位
            int id = rs.getInt(1);   // *获取字段的值和填充占位符 都可以用Object类型*
            String name = rs.getString(2);
            String email = rs.getString(3);
            Date birth = rs.getDate(4);
            Blob photo = rs.getBlob(5);
     */

                //   方法二：里面填字段名
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                Date birth = rs.getDate("birth");

                Customers c = new Customers(id,name,email,birth); // 将前四个字段通过构造方法，封装到对象中，再进行打印
                System.out.println(c);


                //将Blob类型的字段下载下来，以文件的方式保存再本地
                Blob blob = rs.getBlob("photo");
                is = blob.getBinaryStream();   // 通过Blob对象blob调用二进制流的方法，用二进制流将数据封装存储
                fos = new FileOutputStream("huoLong.jpg"); // 创建文件字节输出流 给出一个存储数据的路径 (之后将输入流中的图片存储到此处)

                byte[] b = new byte[2147483600]; //  通过字节数组来实现数据在输入流和输出流之间的传输（字节数组相当于两流之间的中间容器）
                int len;   //图片太大 字节数组就要增加
                if((len = is.read(b)) != -1){  //从输入流读取若干字节，保存到字节数组b中，返回的整数表示读取字节的数目 (当数据读取完毕时 返回-1)
                    fos.write(b,0,len); // 将字节数组b中的索引0开始，读取全部数据，写入输出流中
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (is != null)
                    is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            JDBCUtils.closeResource(conn, ps, rs);
        }


    }



}
