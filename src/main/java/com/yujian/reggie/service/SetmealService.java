package com.yujian.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yujian.reggie.dto.SetmealDto;
import com.yujian.reggie.entity.Setmeal;



public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时删除套餐和菜品的关联数据
     * @param ids
     */
    public void removeWithDish(Long[] ids);
}
