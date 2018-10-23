package com.spring.demo.springbootexample.controller.global;

import com.google.common.collect.Maps;
import com.spring.demo.springbootexample.constants.ErrCodeEnum;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
public class GlobalBasicErrorHandler extends BasicErrorController {

    public GlobalBasicErrorHandler(ServerProperties serverProperties) {
        super(new DefaultErrorAttributes(), serverProperties.getError());
    }
    @Override
    protected ModelAndView resolveErrorView(HttpServletRequest request, HttpServletResponse response, HttpStatus status, Map<String, Object> model) {
        return super.resolveErrorView(request,response,status,model);
    }
    @Override
    protected Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {

        Map<String,Object> respMap = Maps.newHashMap();
        respMap.put("code",ErrCodeEnum.SYS_ERROR.code);
        respMap.put("msg",ErrCodeEnum.SYS_ERROR.msg);
        return respMap;
    }
}
