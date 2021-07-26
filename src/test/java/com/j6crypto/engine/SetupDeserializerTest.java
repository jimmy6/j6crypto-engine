package com.j6crypto.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.logic.entity.state.ProfitReduceFromHighestState;
import com.j6crypto.logic.entity.state.ReboundMartingaleState;
import com.j6crypto.to.setup.AutoTradeOrderSetup;
import com.j6crypto.to.setup.ProfitReduceFromHighestSetup;
import com.j6crypto.to.setup.ReboundMartingaleSetup;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class SetupDeserializerTest {
  @Test
  public void testDeserializer() throws IOException {
    AutoTradeOrderSetup autoTradeOrder = new AutoTradeOrderSetup();
    ReboundMartingaleState reboundMartingaleState = new ReboundMartingaleState(
      new ReboundMartingaleSetup(5, false, new BigDecimal("0.5"), new BigDecimal("0.2")));
    autoTradeOrder.getPmStates().add(reboundMartingaleState);
    reboundMartingaleState.setLogicCode(EngineConstant.PmLogicCodes.ReboundMartingale.name());

    ProfitReduceFromHighestState profitReduceFromHighestState =
      new ProfitReduceFromHighestState(new ProfitReduceFromHighestSetup(new BigDecimal("0.2")));
    profitReduceFromHighestState.setLogicCode(EngineConstant.StopLogicCodes.ProfitReduceFromHighest.name());
    autoTradeOrder.getStopStates().add(profitReduceFromHighestState);

    String json = new ObjectMapper().writeValueAsString(autoTradeOrder);
    AutoTradeOrderSetup atoRestore = new ObjectMapper()
      .readerFor(AutoTradeOrderSetup.class)
      .readValue(json);

    Assertions.assertEquals(reboundMartingaleState.toString(), ((ReboundMartingaleState) atoRestore.getPmStates().get(0)).toString());
    Assertions.assertEquals(profitReduceFromHighestState.toString(), ((ProfitReduceFromHighestState) atoRestore.getStopStates().get(0)).toString());
    Assertions.assertEquals(profitReduceFromHighestState.getLogicCode(), ((ProfitReduceFromHighestState) atoRestore.getStopStates().get(0)).getLogicCode());
    Assertions.assertEquals(autoTradeOrder.toString(), atoRestore.toString());

  }
}
