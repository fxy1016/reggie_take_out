package com.fx.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fx.reggie_take_out.common.R;
import com.fx.reggie_take_out.entity.Employee;
import com.fx.reggie_take_out.service.EmployeeService;
import com.fx.reggie_take_out.service.impl.EmployeeServiceImpl;
import com.sun.javafx.scene.paint.GradientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

//    登录功能

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
//        将页面获取的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        查询数据库有没有这个用户
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
//        返回为空，说明库里没有这个用户
        if (emp == null){
            return R.error("登陆失败，没有找到该用户");
        }
//        密码不一样，一样登陆失败
        if (!emp.getPassword().equals(password)){
            return R.error("密码错误，请重新输入！");
        }
//        查看员工账号是否被禁用

        if (emp.getStatus() == 0){
            return R.error("账号已被禁用");
        }
//        登陆成功将员工id存入session兵返回登陆结果
        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);
    }
// 退出功能
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

//    新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());

//        员工初始密码：123456，但需要MD5处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
//      todo : 更新时间是不是应该放到更新请求更合理，而且大家都是管理员，那么更新人不是只能是自己了？权限不太清晰
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("新员工新增成功！");
    }

    /**
     * 获取员工列表
     * @param page
     * @param pageSize
     * @param name
     * @return
     */

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
//        构建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
//        添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
//        添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
//        执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }
}
