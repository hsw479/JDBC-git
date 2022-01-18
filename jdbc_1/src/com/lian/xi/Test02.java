package com.lian.xi;

import com.bean.ExamStudent;
import com.util.JDBCUtils;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;
    /*
    对不同的表进行增删改查通用操作的总结：
            1.增删改传递参数不要加(引用)类  查询要加(引用)类  //针对某一表查询 就不用加(引用)类
            2.增删改最后返回的值：受影响的行数(int)  查询最后返回的值：1.类对象t(查询一条结果) 2.集合(查询多条结果)

    */
public class Test02 {
    public static void main(String[] args) throws Exception {  // Junit无法使用,推荐方法嵌套

      //  testInsert(); //调用测试插入的方法

      //  testQuery(); //调用测试查询的方法

        Delete();  //调用测试删除(先查询 后删除)的方法
    }


    // 测试插入
    public static void testInsert() throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("请选择考试类型(4级/6级)：");
        int Type = sc.nextInt();
        System.out.println("身份证号：");
        String IDCard = sc.next();
        System.out.println("准考证号：");
        String ExamCard = sc.next();
        System.out.println("学生姓名：");
        String StudentName = sc.next();
        System.out.println("所在城市：");
        String Location = sc.next();
        System.out.println("成绩：");
        int Grade = sc.nextInt();

        String sql = "insert into `examstudent`(Type,IDCard,ExamCard,StudentName,Location,Grade) values(?,?,?,?,?,?)";
        int insert = Update(sql, Type, IDCard, ExamCard, StudentName, Location, Grade);
        if(insert > 0){
            System.out.println("添加成功");
        }else{
            System.out.println("添加失败");
        }
    }


    // 问题1：插入一条记录到examstudent表中
    public static int Update(String sql,Object...args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
          conn = JDBCUtils.getConnection();
          ps = conn.prepareStatement(sql);
         for (int i = 0; i < args.length; i++) {
             ps.setObject(i + 1, args[i]);
         }

         return ps.executeUpdate();
        }catch(Exception e){
         e.printStackTrace();
         }finally{
         JDBCUtils.closeResource(conn,ps);
        }
        return 0;

    }

//————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————
//————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————

    public static void testQuery(){

        System.out.println("请选择你要输入的类型");
        System.out.println("a:准考证号");
        System.out.println("b:身份证号");
        Scanner sc = new Scanner(System.in);
        String select = sc.next();

        if("a".equalsIgnoreCase(select)){
            System.out.println("请输入准考证号：");
            String examCard1 = sc.next();
                                 // mysql不区分大小写 所以这里的字段名最好是跟属性名一致
            String sql = "select FlowID flowID,Type type,IDCard,ExamCard examCard,StudentName name,Location location,Grade grade from examstudent where examCard = ?";
            ExamStudent query1 = Query(ExamStudent.class, sql, examCard1);
            if(query1 !=null){
                System.out.println(query1);
            }else{
                System.out.println("输入错误！");
            }


        }else if("b".equalsIgnoreCase(select)){
            System.out.println("请输入身份证号：");
            String IDCard1 = sc.next();
            // mysql不区分大小写 所以这里的字段名最好是跟属性名一致
            String sql = "select FlowID flowID,Type type,IDCard,ExamCard examCard,StudentName name,Location location,Grade grade from examstudent where IDCard = ?";
            ExamStudent query2 = Query(ExamStudent.class, sql, IDCard1);
            if(query2 != null){
                System.out.println(query2);
            }else{
                System.out.println("输入错误！");
            }

        }else{
            System.out.println("您的输入有误!请重新进入程序。");
        }

    }



   // 问题2：根据身份证号或者准考证号查询学生成绩信息
    public static <T> T Query(Class<T> clazz,String sql,Object...args) {  // examCard1传递到可变形参中 存储在args[0]的位置 (如还有参数传过来 则数组索引位向后移)

            Connection conn =null;
            PreparedStatement ps = null;
            ResultSet rs = null;

    try {
         conn = JDBCUtils.getConnection();
         ps = conn.prepareStatement(sql);
         for (int i = 0; i < args.length; i++) {
             ps.setObject(i + 1, args[i]); // mysql中索引位为1的占位符 将参数args[0]放置进去 进行填充占位符
         }
         rs = ps.executeQuery();   // 如果输入错误 会在这里就结束 返回null
         ResultSetMetaData rsmd = rs.getMetaData();
         int columnCount = rsmd.getColumnCount();

         if (rs.next()) {
             T t = clazz.newInstance();
             for (int i = 0; i < columnCount; i++) {
                 Object columnValue = rs.getObject(i + 1);
                 String columnLabel = rsmd.getColumnLabel(i + 1);

                 Field field = clazz.getDeclaredField(columnLabel); // 获取指定名称的属性/成员变量
                 field.setAccessible(true);
                 field.set(t, columnValue);
                }
             return t;
            }
        }catch(Exception e){
         e.printStackTrace();
     }finally{
         JDBCUtils.closeResource(conn,ps,rs);
     }

        return null;
    }

//————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————
//————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————

    // 问题3：删除指定的学生信息  (先查询有无此人 再删除)

    public static void Delete() throws Exception {
        System.out.println("请输入学生的考号：");
        Scanner sc = new Scanner(System.in);
        String examCard2 = sc.next();

   /*   String sql = "select FlowID flowID,Type type,IDCard,ExamCard examCard,StudentName name,Location location,Grade grade from examstudent where ExamCard = ?";
        ExamStudent query3 = Query(ExamStudent.class, sql, examCard2);
        if(query3 == null){
            System.out.println("查无此人");
        }else{  }

    */
        // 优化 (直接删除)
            String sql1 = "delete from examstudent where ExamCard = ?";
            int deleteColumn = Update(sql1, examCard2);  // 增删改，可以用一个方法  且返回值为受影响的行数
            if(deleteColumn > 0){
                System.out.println("删除成功!");
            }else {
                   System.out.println("查无此人,删除失败");
            }


    }




}
