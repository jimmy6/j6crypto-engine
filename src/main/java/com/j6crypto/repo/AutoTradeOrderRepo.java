package com.j6crypto.repo;

import com.j6crypto.logic.entity.state.AutoTradeOrder;
import com.j6crypto.to.Trade;
import com.j6crypto.to.setup.AutoTradeOrderSetup;
import com.j6crypto.to.setup.AutoTradeOrderSetup.Status;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public interface AutoTradeOrderRepo extends JpaRepository<AutoTradeOrder, Integer> {

  List<AutoTradeOrder> findByMsIdAndStatusIn(Integer msId, List<Status> asList);

  @Query(value = "select distinct(msId) from AutoTradeOrder where status in (?1)")
  Set<Integer> findDistinctMsIdByStatusIn(Set<Status> statuses);

  @Modifying
  @Query("Update AutoTradeOrder set status=:status where id=:atoId and clientId=:clientId and status not in (:statusNotIn)")
  int updateStatus(Integer atoId, int clientId, Status status, Set<Status> statusNotIn);

//  @EntityGraph(attributePaths = {"triggerStates", "pmStates", "stopStates"})
//  public AutoTradeOrder getOneFullLoadById(Integer autoTradeOrderId);
}
