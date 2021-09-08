package com.j6crypto.config;

import com.j6crypto.web.ClientContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer, HandlerInterceptor {
  @Autowired
  private ClientContext clientContext;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(this);
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    try {
      clientContext.setClientId(request.getIntHeader("clientId"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }

}