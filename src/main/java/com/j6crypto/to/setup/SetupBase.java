package com.j6crypto.to.setup;

import com.j6crypto.engine.entity.EntityBase;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
@Entity
@javax.persistence.Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "logic_code", discriminatorType = DiscriminatorType.STRING)
public class SetupBase extends EntityBase {
  @Transient // keep this transient because duplicate column with DiscriminatorColumn. But this need to for json api
  private String logicCode;
  private int cacheSignalForPeriod = 0;

  public String getLogicCode() {
    return logicCode;
  }

  public void setLogicCode(String logicCode) {
    this.logicCode = logicCode;
  }

  public int getCacheSignalForPeriod() {
    return cacheSignalForPeriod;
  }

  public void setCacheSignalForPeriod(int cacheSignalForPeriod) {
    this.cacheSignalForPeriod = cacheSignalForPeriod;
  }

}
