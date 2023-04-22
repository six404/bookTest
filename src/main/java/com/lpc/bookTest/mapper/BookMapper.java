package com.lpc.bookTest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lpc.bookTest.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @ClassName: BookMapper
 * @Description:
 * @author: lpc
 * @date: 2023年4月19日 下午3:05
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {
}
