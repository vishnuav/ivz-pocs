package com.ivz.oms.service.impl;

import com.ivz.common.model.latency.LatencyMonitor;
import com.ivz.oms.service.OMSLoginLogoutService;
import org.springframework.stereotype.Service;

@Service
public class OMSLoginLogoutServiceImpl implements OMSLoginLogoutService {
  @Override
  public Object login() {
    long requestId = LatencyMonitor.monitor(0L, "OMSLoginLogoutServiceImpl.login", System.currentTimeMillis(), "login");
    Object loginResponse = new Object();
    LatencyMonitor.monitor(requestId, "OMSLoginLogoutServiceImpl.login", System.currentTimeMillis(), "login");
    return loginResponse;
  }

  @Override
  public boolean logout() {
    long requestId = LatencyMonitor.monitor(0L, "OMSLoginLogoutServiceImpl.logout", System.currentTimeMillis(), "logout");
    boolean logoutResponse = false;
    LatencyMonitor.monitor(requestId, "OMSLoginLogoutServiceImpl.logout", System.currentTimeMillis(), "logout");
    return logoutResponse;
  }
}