package com.ivz.oms.service.impl;

import com.ivz.oms.service.OMSLoginLogoutService;
import org.springframework.stereotype.Service;

@Service
public class OMSLoginLogoutServiceImpl implements OMSLoginLogoutService {
  @Override
  public Object login() {
    return new Object();
  }

  @Override
  public boolean logout() {
    return false;
  }
}