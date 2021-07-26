package com.j6crypto.repo;

import com.j6crypto.to.Trade;
import org.springframework.data.repository.CrudRepository;
/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public interface TradeRepo extends CrudRepository<Trade, Integer> {

}
