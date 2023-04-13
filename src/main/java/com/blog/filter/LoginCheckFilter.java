package com.blog.filter;

import com.alibaba.fastjson.JSON;
import com.blog.common.BaseContext;
import com.blog.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //强转
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}", requestURI);

        //定义不需要处理的请求
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                //对用户登陆操作放行
                "/user/login",
                "/user/sendMsg"
        };

        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3.如果不需要处理，则直接放行
        if (check) {
            log.info("本次请求：{}，不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //4.判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("后端用户已登录，id为{}", request.getSession().getAttribute("employee"));
            //在这里获取一下线程id
            long id = Thread.currentThread().getId();
            log.info("doFilter的线程id为：{}", id);
            //根据session来获取之前我们存的id值
            Long empId = (Long) request.getSession().getAttribute("employee");
            //使用BaseContext封装id
            BaseContext.setCurrentId(empId);
            if (request.getSession().getAttribute("user") != null)
                return;
            filterChain.doFilter(request, response);
            return;
        }

        //判断前端用户是否登录
        if(request.getSession().getAttribute("user") != null){
            log.info("前端用户已登录，用户id为：{}",request.getSession().getAttribute("user"));
            Long userId = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }

        //5.如果未登录则返回未登录结果,通过输出流方式向客户端页面响应数据
        log.info("后台用户未登录");
        log.info("后台用户id：{}", request.getSession().getAttribute("employee"));
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
    }

    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                //匹配
                return true;
            }
        }
        //不匹配
        return false;
    }
}