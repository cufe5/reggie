package com.yujian.reggie.dto;

import com.yujian.reggie.entity.Setmeal;
import com.yujian.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
