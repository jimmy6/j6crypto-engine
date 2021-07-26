package com.j6crypto.engine;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class EngineConstant {
  public final static String COIN_TOPIC_PREFIX = "coin.";
  public enum TriggerLogicCodes {
    DummyTrigger
  }

  public enum PmLogicCodes {
    OpenMarketPrice, ReboundMartingale
  }

  public enum StopLogicCodes {
    ProfitPercentageTp, ProfitReduceFromHighest, PriceReduceFromHighest
  }

}
