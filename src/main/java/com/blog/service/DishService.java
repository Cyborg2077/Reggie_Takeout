package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.dto.DishDto;
import com.blog.entity.Dish;


public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);
}
