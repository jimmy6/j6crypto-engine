package com.j6crypto.logic.entity.state;

public interface State {
  CommonState getCommonState();
  int getCacheSignalForPeriod();
}
