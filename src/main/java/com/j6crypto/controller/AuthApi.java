package com.j6crypto.controller;

import com.j6crypto.service.JwtService;
import com.j6crypto.to.LoginReq;
import com.j6crypto.to.LoginRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@RestController
public class AuthApi {

  @Autowired
  private JwtService jwtService;

  @PostMapping("/auth/login")
  public LoginRes login(@RequestBody LoginReq loginReq) {
    String token = jwtService.generateToken(loginReq.getUsername());

    return new LoginRes(token);
  }

}
