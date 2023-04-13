package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.dto.DishDto;
import com.blog.entity.Dish;
import com.blog.entity.DishFlavor;
import com.blog.mapper.DishMapper;
import com.blog.service.DishFlavorService;
import com.blog.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor dishFlavor : flavors) {
            dishFlavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //先根据id查询到对应的dish对象
        Dish dish = this.getById(id);
        //创建一个dishDao对象
        DishDto dishDto = new DishDto();
        //拷贝对象
        BeanUtils.copyProperties(dish, dishDto);
        //条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //根据dish_id来查询对应的菜品口味数据
        queryWrapper.eq(DishFlavor::getDishId, id);
        //获取查询的结果
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        //并将其赋给dishDto
        dishDto.setFlavors(flavors);
        //作为结果返回给前端
        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新当前菜品数据（dish表）
        this.updateById(dishDto);
        //下面是更新当前菜品的口味数据
        //条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //条件是当前菜品id
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        //将其删除掉
        dishFlavorService.remove(queryWrapper);
        //获取传入的新的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        //这些口味数据还是没有dish_id，所以需要赋予其dishId
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        //再重新加入到表中
        dishFlavorService.saveBatch(flavors);
    }
}
