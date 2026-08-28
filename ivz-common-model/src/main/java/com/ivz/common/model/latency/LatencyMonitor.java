package com.ivz.common.model.latency;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class LatencyMonitor {
  private static final AtomicLong requestIDGenerator = new AtomicLong(0);
  private static final Map<Long, Request> requestIdMap = new ConcurrentHashMap<>();

  public static long monitor(long requestId, String event, long dateTime, String message) {
    if (requestId == 0) {
      long generatedRequestId = requestIDGenerator.incrementAndGet();
      requestIdMap.put(generatedRequestId, new Request(generatedRequestId, event, dateTime, message));
      return generatedRequestId;
    }

    Request request = requestIdMap.get(requestId);
    if (request == null) {
      log.info("requestId {} for {} has no active request message {}", requestId, event, message);
      return requestId;
    }
    long elapsedMillis = dateTime - request.startDateTime();
    log.info("requestId {} for {} took {} millis message {}", requestId, event, elapsedMillis, message);
    requestIdMap.remove(requestId);
    return requestId;
  }

  private record Request(long requestId, String event, long startDateTime, String message) {
  }
}