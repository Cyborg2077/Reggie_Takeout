package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.Result;
import com.blog.entity.Employee;
import com.blog.service.EmployeeService;
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

    /**
     * 登入功能
     *
     * @param request
     * @param employee
     * @return
     */
    //发送post请求
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //这部分就是MP
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(lqw);
        if (emp == null) {
            return Result.error("登陆失败");
        }
        if (!emp.getPassword().equals(password)) {
            return Result.error("登录失败");
        }
        if (emp.getStatus() == 0) {
            return Result.error("该用户已被禁用");
        }
        //存个Session，只存个id就行了
        request.getSession().setAttribute("employee", emp.getId());
        return Result.success(emp);
    }

    /**
     * 登出功能
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    /**
     * 新增员工
     *
     * @param employee
     * @return
     */
    @PostMapping
    public Result<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增的员工信息：{}", employee.toString());
        //设置默认密码为123456，并采用MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //存入数据库
        employeeService.save(employee);
        return Result.success("添加员工成功");
    }

    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={}", page, pageSize, name);
        //构造分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        //添加过滤条件（当我们没有输入name时，就相当于查询所有了）
        wrapper.like(!(name == null || "".equals(name)), Employee::getName, name);
        //并对查询的结果进行降序排序，根据更新时间
        wrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, wrapper);
        return Result.success(pageInfo);
    }

    /**
     * 通用的修改员工信息
     *
     * @param employee
     * @param request
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody Employee employee, HttpServletRequest request) {
        log.info(employee.toString());
        //获取线程id
        long id = Thread.currentThread().getId();
        log.info("update的线程id为：{}", id);
        employeeService.updateById(employee);
        return Result.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息..");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return Result.success(employee);
        }
        return Result.error("未查询到该员工信息");
    }
}