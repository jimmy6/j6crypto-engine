package com.j6crypto.engine;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class EngineConstant {
  public final static String COIN_TOPIC_PREFIX = "coin.";

  public enum TriggerLogicCodes {
    DummyTrigger, BreakSupportResistanceTrigger, CrossValueTrigger
  }

  public enum PmLogicCodes {
    OpenMarketPrice, ReboundMartingale, // outdate
    Rebound
  }

  public enum StopLogicCodes {
    ProfitPercentageTp, ProfitReduceFromHighest, PriceReduceFromHighest
  }
  public enum PmCodes {
    MartingaleDoublePm
  }

}
