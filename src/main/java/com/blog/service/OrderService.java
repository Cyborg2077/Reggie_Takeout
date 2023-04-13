package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Orders;

public interface OrderService extends IService<Orders> {

    void submit(Orders orders);
}
