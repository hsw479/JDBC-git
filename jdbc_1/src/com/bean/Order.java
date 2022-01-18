package com.bean;

import java.util.Date;

    /*
     ORM编程思想（object relational mapping）
     一个数据表对应Java类
     表中的一条记录对应Java类的一个对象
     表中的一个字段对应Java类的一个属性

     (对应!=相等) 可以给列/字段名起别名，让别名和java中的变量/属性名一致
     */

public class Order {
    private int orderId;
    private String orderName;
    private Date orderDate;

    public Order() {
        super();
    }

    public Order(int orderId, String orderName, Date orderDate) {
        super();
        this.orderId = orderId;
        this.orderName = orderName;
        this.orderDate = orderDate;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderName='" + orderName + '\'' +
                ", orderDate=" + orderDate +
                '}';
    }
}
