package com.j6crypto.engine.entity;

import javax.persistence.Entity;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
//@Entity
public class ErrorLog {
  private int clientId;
  private LocalDateTime date;
  private String error;
}
