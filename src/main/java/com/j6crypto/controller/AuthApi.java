package com.j6crypto.controller;

import com.j6crypto.service.ClientService;
import com.j6crypto.service.JwtService;
import com.j6crypto.to.LoginReq;
import com.j6crypto.to.LoginRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@RestController
public class AuthApi {

  @Autowired
  private JwtService jwtService;
  @Autowired
  private ClientService clientService;

  @PostMapping("/auth/login")
  public LoginRes login(@RequestBody LoginReq loginReq) {
    return clientService.login(loginReq);
  }

}
