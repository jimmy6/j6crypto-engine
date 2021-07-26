package com.j6crypto.engine;

import com.j6crypto.to.Trade;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public abstract class TradePlatform {

	public abstract void openMarket(Integer clientId, String code, Trade trade);
}
