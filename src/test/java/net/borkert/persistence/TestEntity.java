package net.borkert.persistence;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "test")
public class TestEntity {

  @Id
  @SequenceGenerator(name="seq_test", sequenceName="seq_test", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_test")
  private long id;

  @Column(length = 20)
  private String name;

  @Temporal(value = TemporalType.TIMESTAMP)
  private Date timestamp;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

}
