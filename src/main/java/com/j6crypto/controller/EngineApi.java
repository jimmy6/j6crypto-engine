package com.j6crypto.controller;

import com.j6crypto.engine.CryptoEngine;
import com.j6crypto.engine.entity.MasterDataKey.IdCoin;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.service.AutoTradeOrderService;
import com.j6crypto.to.TimeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@RestController()
@RequestMapping("engine")
public class EngineApi {
  @Autowired
  private AutoTradeOrderService autoTradeOrderService;
  @Autowired
  private CryptoEngine cryptoEngine;

  @PostMapping(path = "ato")
  @ResponseBody
  public Integer createAutoTradeOrder(@RequestBody AutoTradeOrder autoTradeOrder) {
    return autoTradeOrderService.create(autoTradeOrder).getId();
  }

  @PostMapping(path = "monitor")
  public void monitor(@RequestBody TimeData timeData) {
    if (!timeData.getCode().startsWith(IdCoin.DUMMY.name())) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, timeData.getCode() + " is not supported in rest api.");
    }
    cryptoEngine.monitor(timeData);
  }

  @GetMapping(path = "ato/{id}")
  @ResponseBody
  public AutoTradeOrder getAutoTradeAuto(@PathVariable Integer id) {
    return autoTradeOrderService.getAto(id);
  }

  @Deprecated
  @GetMapping(path = "ato/{id}/run")
  public void runAutoTradeAuto(@PathVariable Integer id) {
    cryptoEngine.addAutoTradeOrder(id);
  }

  @GetMapping(path = "ato/{id}/ms/{msId}/terminate")
  public void terminateAutoTradeAuto(@PathVariable Integer id) {
    autoTradeOrderService.terminateAutoTradeOrder(id);
  }

}
