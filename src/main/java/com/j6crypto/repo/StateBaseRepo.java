package com.j6crypto.repo;

import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.to.setup.SetupBase;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public interface StateBaseRepo extends JpaRepository<SetupBase, Integer> {

}
