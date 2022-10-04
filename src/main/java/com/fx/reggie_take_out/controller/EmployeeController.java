package com.fx.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fx.reggie_take_out.common.R;
import com.fx.reggie_take_out.entity.Employee;
import com.fx.reggie_take_out.service.EmployeeService;
import com.fx.reggie_take_out.service.impl.EmployeeServiceImpl;
import com.sun.javafx.scene.paint.GradientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
// 推出功能
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
}
