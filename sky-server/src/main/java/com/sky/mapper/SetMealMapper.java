package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetMealPageQueryDTO;
import com.sky.entity.SetMeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetMealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetMealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 新增套餐
     * @param setMeal
     */
    @AutoFill(OperationType.INSERT)
    void insert(SetMeal setMeal);

    /**
     * 分页查询
     * @param setMealPageQueryDTO
     * @return
     */
    Page<SetMealVO> pageQuery(SetMealPageQueryDTO setMealPageQueryDTO);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    SetMeal getById(Long id);

    /**
     * 根据id删除套餐
     * @param setMealId
     */
    @Delete("delete from setmeal where id = #{setMealId}")
    void deleteById(Long setMealId);

    /**
     * 根据主键动态修改属性
     * @param setMeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(SetMeal setMeal);
}
