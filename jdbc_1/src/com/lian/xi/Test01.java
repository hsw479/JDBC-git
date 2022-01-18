package com.lian.xi;

import com.util.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;


public class Test01 {  // 向customers表中插入一个记录

    public static void main(String[] args) {  // idea要在主方法中进行增删改操作 (在@Test中进行会卡住)
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入姓名");
        String name = sc.next();
        System.out.println("请输入邮件");
        String email = sc.next();
        System.out.println("请输入出生日期");  // *字符串类写成这样：1992-05-07 会被认为是日期类(将其隐性转换日期类)*
        String birth = sc.next();

        String sql = "insert into `customers`(name,email,birth) value(?,?,?)";
        int insert = Insert(sql, name, email, birth);  //返回受影响的行数
        if(insert > 0){
            System.out.println("添加成功!");
        }else{
            System.out.println("添加失败!");
        }
    }



    public static int Insert(String sql,Object...args){    // 需要返回值类型，来提示是否添加成功 （进行增删改的操作时 要用int返回值类型）
             Connection conn = null;
             PreparedStatement ps = null;

        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
             // return ps.execute()  对返回的行数没有需要可用此方法
            // 如果是查询操作有返回结果，就返回true；如果是增删改操作没有返回结果，则返回false

            return ps.executeUpdate();// 对返回的行数有需求
            // 增删改数据会有返回值。返回值为：受影响的行数  如果返回0，则没有行受影响
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn,ps);
        }
        return 0;   // 相当于try里面的语句没有执行成功 增删改操作失败返回0

    }


}
