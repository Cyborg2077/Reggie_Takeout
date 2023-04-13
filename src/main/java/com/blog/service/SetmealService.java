package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.dto.SetmealDto;
import com.blog.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);
}
