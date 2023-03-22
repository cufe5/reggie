package com.yujian.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yujian.reggie.common.CustomException;
import com.yujian.reggie.dto.SetmealDto;
import com.yujian.reggie.entity.Setmeal;
import com.yujian.reggie.entity.SetmealDish;
import com.yujian.reggie.mapper.SetmealMapper;
import com.yujian.reggie.service.SetmealDishService;
import com.yujian.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 删除套餐，同时删除套餐和菜品的关联数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(Long[] ids) {

        List<Long> list = new ArrayList<>();
        for (Long id : ids){
            list.add(id);

        }
        //查询套餐状态，确认是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,list);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = (int) this.count(queryWrapper);
        if(count > 0){
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //如果可以删除，先删除套餐中的数据
        this.removeByIds(list);

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,list);

        //删除关系表中的数据
        setmealDishService.remove(lambdaQueryWrapper);

    }

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());


        //保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(setmealDishes);


    }
}
