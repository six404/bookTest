package com.lpc.bookTest.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lpc.bookTest.entity.Book;
import com.lpc.bookTest.service.BookService;
import com.lpc.bookTest.utils.Code;
import com.lpc.bookTest.utils.R;
import com.lpc.bookTest.utils.TimeUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.javassist.compiler.SymbolTable;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * @ClassName: BookController
 * @Description:
 * @author: lpc
 * @date: 2023年4月19日 下午3:02
 */

@RestController
@RequestMapping("/book")
@Component
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 时间格式化
     */
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/");

    /**
     * 图片保存路径
     */
    @Value("${file-save-path}")
    private String fileSavePath;

    @GetMapping("/hello")
    public int hello() {
        return 1;
    }

    /**
     * 图片上传
     *
     * @param file
     * @param request
     * @return
     */
    @PostMapping("/upload")
    public R uploadPicture(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

        String directory = simpleDateFormat.format(TimeUtil.getTime());

        /**
         * 文件保存目录 D:/images/2020/03/15/
         * 如果目录不存在，则创建
         */
        File dir = new File(fileSavePath + directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        System.out.println("图片上传，保存的位置:" + fileSavePath + directory);

        /**
         * 给文件重新设置一个名字
         * 后缀
         */
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
//        System.out.println(suffix);
        String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + suffix;
//        System.out.println(newFileName);

        //4.创建这个新文件(empty)
        File newFile = new File(fileSavePath + directory + newFileName);
//        System.out.println(newFile.length());
        //5.复制操作
        try {
            file.transferTo(newFile);
//            System.out.println(newFile.length());
            //协议 :// ip地址 ：端口号 / 文件目录(/images/2020/03/15/xxx.jpg)
            String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/images/" + directory + newFileName;
            System.out.println("图片上传，访问URL：" + url);
            //这里可以上传服务器
            return new R(Code.WORK_OK, "上传成功", url);
        } catch (IOException e) {
            return new R(Code.WORK_ERR, "IO异常");
        }
    }


    /**
     * 书城条件分页查询
     * @param current
     * @param pageSize
     * @param book
     * @return
     */
    @PostMapping("/list/{current}/{pageSize}")
    public R selectPage(@PathVariable("current") long current, @PathVariable("pageSize") long pageSize,
                        @RequestBody Book book){
        //mybatis-plus分页
        Page<Book> page = new Page<>(current, pageSize);
        QueryWrapper<Book> wrapper = new QueryWrapper<>();

        String name = book.getName();
        if (!StringUtils.isEmpty(name)){
            wrapper.like("name",name);
        }
        wrapper.eq("is_deleted","0");
        wrapper.orderByDesc("gmt_modified");

        Page<Book> result = bookService.selectPage(page, wrapper);
        if (StringUtils.isEmpty(String.valueOf(result.getRecords()))){
            return new R(Code.WORK_ERR,"查询为空");
        }
        return new R(Code.WORK_OK,"操作成功",result);
    }

    /**
     * 书城新增一本书
     * @param book
     * @return
     */
    @PostMapping("/add-one-book")
    public R addBookInfo(@RequestBody Book book){
        int flag = bookService.addBookInfo(book);
        if (flag != 1){
            return new R(Code.WORK_ERR,"新增书本信息失败！");
        }else {
            return new R(Code.WORK_OK,"新增书本信息成功！");
        }
    }

    /**
     * 根据id获取书本信息
     * @param id
     * @return
     */
    @GetMapping("/get-one-book/{id}")
    public R getOneBook(@PathVariable("id") Integer id){
        Book result = bookService.getOneBook(id);
        if (!Strings.isNotEmpty(result.getName())){
            return new R(Code.WORK_ERR,"根据id获取书本信息失败！");
        }
        return new R(Code.WORK_OK,"获取书本信息成功",result);
    }

    /**
     * 修改一本书的信息
     * @param book
     * @return
     */
    @PostMapping("/upd-one-book")
    public R updOneBook(@RequestBody Book book){
        int flag = bookService.updOneBook(book);
        if (flag != 1){
            return new R(Code.WORK_ERR,"更新书本信息失败！");
        }else {
            return new R(Code.WORK_OK,"更新书本信息成功！");
        }
    }

    /**
     * 删除一本书
     * @param book
     * @return
     */
    @PostMapping("/delete-one-book")
    public R deleteOneBook(@RequestBody Book book){
        int flag = bookService.deleteOneBook(book);
        if (flag != 1){
            return new R(Code.WORK_ERR,"删除书本信息失败！");
        }else {
            return new R(Code.WORK_OK,"删除书本信息成功！");
        }
    }
}
