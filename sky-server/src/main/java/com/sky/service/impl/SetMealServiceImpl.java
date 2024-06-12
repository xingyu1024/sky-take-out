package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetMealDTO;
import com.sky.dto.SetMealPageQueryDTO;
import com.sky.entity.SetMeal;
import com.sky.entity.SetMealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetMealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 套餐业务实现
 */
@Service
@Slf4j
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetMealMapper setMealMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setMealDTO
     */
    @Transactional
    public void saveWithDish(SetMealDTO setMealDTO) {
        SetMeal setMeal = new SetMeal();
        BeanUtils.copyProperties(setMealDTO, setMeal);

        //向套餐表插入数据
        setMealMapper.insert(setMeal);

        //获取生成的套餐id
        Long setMealId = setMeal.getId();

        List<SetMealDish> setMealDishes = setMealDTO.getSetMealDishes();
        setMealDishes.forEach(setMealDish -> {
            setMealDish.setSetMealId(setMealId);
        });

        //保存套餐和菜品的关联关系
        setMealDishMapper.insertBatch(setMealDishes);
    }

    /**
     * 分页查询
     * @param setMealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetMealPageQueryDTO setMealPageQueryDTO) {
        PageHelper.startPage(setMealPageQueryDTO.getPage(), setMealPageQueryDTO.getPageSize());
        Page<SetMealVO> page = setMealMapper.pageQuery(setMealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id ->{
            SetMeal setMeal = setMealMapper.getById(id);
            if(StatusConstant.ENABLE == setMeal.getStatus()){
                //起售中的套餐不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });

        ids.forEach(setMealId ->{
            //删除套餐表中的数据
            setMealMapper.deleteById(setMealId);
            //删除套餐菜品关系表中的数据
            setMealDishMapper.deleteBySetMealId(setMealId);
        });
    }

    /**
     * 根据id查询套餐和套餐菜品关系
     * @param id
     * @return
     */
    public SetMealVO getByIdWithDish(Long id) {
        SetMeal setMeal = setMealMapper.getById(id);
        List<SetMealDish> setMealDishes = setMealDishMapper.getBySetMealId(id);

        SetMealVO setMealVO = new SetMealVO();
        BeanUtils.copyProperties(setMeal, setMealVO);
        setMealVO.setSetMealDishes(setMealDishes);

        return setMealVO;
    }

    /**
     * 修改套餐
     * @param setMealDTO
     */
    @Transactional
    public void update(SetMealDTO setMealDTO) {
        SetMeal setMeal = new SetMeal();
        BeanUtils.copyProperties(setMealDTO, setMeal);

        //1、修改套餐表，执行update
        setMealMapper.update(setMeal);

        //套餐id
        Long setMealId = setMealDTO.getId();

        //2、删除套餐和菜品的关联关系，操作setMeal_dish表，执行delete
        setMealDishMapper.deleteBySetMealId(setMealId);

        List<SetMealDish> setMealDishes = setMealDTO.getSetMealDishes();
        setMealDishes.forEach(setMealDish -> {
            setMealDish.setSetMealId(setMealId);
        });
        //3、重新插入套餐和菜品的关联关系，操作setMeal_dish表，执行insert
        setMealDishMapper.insertBatch(setMealDishes);
    }

}
