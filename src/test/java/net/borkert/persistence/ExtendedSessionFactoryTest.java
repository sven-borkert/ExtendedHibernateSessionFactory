package net.borkert.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/net/borkert/persistence/ExtendedSessionFactoryTest.xml"})
public class ExtendedSessionFactoryTest {

  @Resource
  private SessionFactory sessionFactory;

  @Resource
  private PlatformTransactionManager transactionManager;

  private TransactionTemplate transactionTemplate;
  private ExtendedSessionFactory esf;

  @Before
  public void createExtendedSessionFactoryAndTransactionTemplate() {
    Assert.notNull(transactionManager, "The 'transactionManager' argument must not be null.");
    transactionTemplate = new TransactionTemplate(transactionManager);
    esf = new ExtendedSessionFactory(getSessionFactory());
  }

  @Test
  public void testSessionFactoryGetter() {
    ExtendedSessionFactory esf = new ExtendedSessionFactory(getSessionFactory());
    assertTrue(esf.getSessionFactory() == getSessionFactory());
  }

  @Test
  @Transactional
  public void testMakePersistent() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
  }

  @Test
  @Transactional
  public void testFindById() {
    TestEntity l = esf.findById(TestEntity.class, (long) 1);
    assertNull(l);
    long id1 = createTestObjectAndReturnId("Test 1");
    long id2 = createTestObjectAndReturnId("Test 2");
    TestEntity l1 = esf.findById(TestEntity.class, id1);
    assertEquals(id1, l1.getId());
    assertEquals("Test 1", l1.getName());
    TestEntity l2 = esf.findById(TestEntity.class, id2);
    assertEquals(id2, l2.getId());
    assertEquals("Test 2", l2.getName());
  }

  @Test
  @Transactional
  public void testFindByIdAndLockForUpdate() {
    TestEntity l = esf.findByIdAndLockForUpdate(TestEntity.class, (long) 1);
    assertNull(l);
    long id1 = createTestObjectAndReturnId("Test 1");
    long id2 = createTestObjectAndReturnId("Test 2");
    TestEntity l1 = esf.findByIdAndLockForUpdate(TestEntity.class, id1);
    assertEquals(id1, l1.getId());
    assertEquals("Test 1", l1.getName());
    TestEntity l2 = esf.findByIdAndLockForUpdate(TestEntity.class, id2);
    assertEquals(id2, l2.getId());
    assertEquals("Test 2", l2.getName());
  }

  @Test
  @Transactional
  public void testFindAll() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
    List<TestEntity> entityList = esf.findAll(TestEntity.class);
    assertTrue(2 == entityList.size());
    createTestObjectAndReturnId("Test 3");
    entityList = esf.findAll(TestEntity.class);
    assertTrue(3 == entityList.size());
  }

  @Test
  @Transactional
  public void testFindAllWithLimit() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
    createTestObjectAndReturnId("Test 3");
    List<TestEntity> entityList = esf.findAll(TestEntity.class, 2);
    assertTrue(2 == entityList.size());
    entityList = esf.findAll(TestEntity.class);
    assertTrue(3 == entityList.size());
  }

  @Test
  @Transactional
  public void testFindAllOrdered() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
    createTestObjectAndReturnId("Test 3");
    List<TestEntity> entityList;
    entityList = esf.findAll(TestEntity.class, Order.asc("id"));
    assertTrue(entityList.get(0).getId() < entityList.get(2).getId());
    entityList = esf.findAll(TestEntity.class, Order.asc("id"), 3);
    assertTrue(entityList.get(0).getId() < entityList.get(2).getId());
    entityList = esf.findAll(TestEntity.class, Order.desc("id"), 3);
    assertTrue(entityList.get(0).getId() > entityList.get(2).getId());
    assertTrue(entityList.get(0).getId() > entityList.get(1).getId());
    entityList = esf.findAll(TestEntity.class, Order.desc("id"), 2);
    assertTrue(2 == entityList.size());
    assertTrue(entityList.get(0).getId() > entityList.get(1).getId());
    assertTrue(entityList.get(0).getId() > entityList.get(1).getId());
    entityList = esf.findAll(TestEntity.class, Order.desc("id"), 1, 2);
    assertTrue(2 == entityList.size());
    assertTrue(entityList.get(0).getId() > entityList.get(1).getId());
  }

  @Test
  @Transactional
  public void testFindByQuery() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
    createTestObjectAndReturnId("Test 3");
    List<TestEntity> entityList = esf.findByQuery(TestEntity.class, "from TestEntity");
    assertTrue(3 == entityList.size());
    entityList = esf.findByQuery(TestEntity.class, "from TestEntity l where l.name = 'Test 1'");
    assertTrue(1 == entityList.size());
  }

  @Test
  @Transactional
  public void testFindByQueryWithLimit() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
    createTestObjectAndReturnId("Test 3");
    List<TestEntity> entityList = esf.findByQuery(TestEntity.class, "from TestEntity", 2);
    assertTrue(2 == entityList.size());
    entityList = esf.findByQuery(TestEntity.class, "from TestEntity", 1);
    assertTrue(1 == entityList.size());
  }

  @Test
  @Transactional
  public void testFindByQueryWithOffsetAndLimit() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
    createTestObjectAndReturnId("Test 3");
    List<TestEntity> entityList = esf.findByQuery(TestEntity.class, "from TestEntity e order by e.id", 1, 1);
    assertTrue(1 == entityList.size());
    assertEquals("Test 2", entityList.get(0).getName());
  }

  @Test
  @Transactional
  public void testFindByCriteria() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
    createTestObjectAndReturnId("Test 3");
    DetachedCriteria query = DetachedCriteria.forClass(TestEntity.class);
    query.add(Restrictions.eq("name", "Test 1"));
    List<TestEntity> entityList = esf.findByCriteria(TestEntity.class, query);
    assertTrue(1 == entityList.size());
    assertEquals("Test 1", entityList.get(0).getName());
  }

  @Test
  @Transactional
  public void testFindByCriteriaWithLimit() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
    createTestObjectAndReturnId("Test 3");
    DetachedCriteria query = DetachedCriteria.forClass(TestEntity.class);
    query.add(Restrictions.like("name", "Test%"));
    List<TestEntity> entityList = esf.findByCriteria(TestEntity.class, query, 2);
    assertTrue(2 == entityList.size());
  }

  @Test
  @Transactional
  public void testFindByCriteriaOrdered() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
    createTestObjectAndReturnId("Test 3");
    DetachedCriteria query = DetachedCriteria.forClass(TestEntity.class);
    query.add(Restrictions.like("name", "Test%"));
    List<TestEntity> entityList = esf.findByCriteria(TestEntity.class, query, Order.asc("name"));
    assertTrue(3 == entityList.size());
    assertEquals("Test 1", entityList.get(0).getName());
    assertEquals("Test 3", entityList.get(2).getName());
  }

  @Test
  @Transactional
  public void testFindByCriteriaOrderedWithLimit() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
    createTestObjectAndReturnId("Test 3");
    DetachedCriteria query = DetachedCriteria.forClass(TestEntity.class);
    query.add(Restrictions.like("name", "Test%"));
    List<TestEntity> entityList = esf.findByCriteria(TestEntity.class, query, Order.asc("name"), 2);
    assertTrue(2 == entityList.size());
    assertEquals("Test 1", entityList.get(0).getName());
    assertEquals("Test 2", entityList.get(1).getName());
  }

  @Test
  @Transactional
  public void testFindByCriteriaOrderedWithLimitAndOffset() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
    createTestObjectAndReturnId("Test 3");
    DetachedCriteria query = DetachedCriteria.forClass(TestEntity.class);
    query.add(Restrictions.like("name", "Test%"));
    List<TestEntity> entityList = esf.findByCriteria(TestEntity.class, query, Order.asc("name"), 1, 2);
    assertTrue(2 == entityList.size());
    assertEquals("Test 2", entityList.get(0).getName());
    assertEquals("Test 3", entityList.get(1).getName());
  }

  @Test
  @Transactional
  public void testFindByProperty() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
    createTestObjectAndReturnId("Test 3");
    List<TestEntity> entityList = esf.findByProperty(TestEntity.class, "name", "Test 1");
    assertTrue(1 == entityList.size());
    assertEquals("Test 1", entityList.get(0).getName());
  }

  @Test
  @Transactional
  public void testFindByPropertyOrdered() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 1");
    List<TestEntity> entityList = esf.findByProperty(TestEntity.class, "name", "Test 1", Order.asc("id"));
    assertTrue(3 == entityList.size());
    assertTrue(entityList.get(0).getId() < entityList.get(1).getId());
    assertTrue(entityList.get(1).getId() < entityList.get(2).getId());
  }

  @Test
  @Transactional
  public void testFindByPropertyWithLimit() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 1");
    List<TestEntity> entityList = esf.findByProperty(TestEntity.class, "name", "Test 1", 2);
    assertTrue(2 == entityList.size());
  }

  @Test
  @Transactional
  public void testFindByPropertyOrderedWithLimit() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 1");
    List<TestEntity> entityList = esf.findByProperty(TestEntity.class, "name", "Test 1", Order.asc("id"), 2);
    assertTrue(2 == entityList.size());
    assertTrue(entityList.get(0).getId() < entityList.get(1).getId());
  }

  @Test
  @Transactional
  public void testFindByPropertyOrderedWithLimitAndOffet() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 1");
    List<TestEntity> entityList = esf.findByProperty(TestEntity.class, "name", "Test 1", Order.desc("id"), 1, 2);
    assertTrue(2 == entityList.size());
    assertTrue(entityList.get(0).getId() > entityList.get(1).getId());
  }

  @Test
  @Transactional
  public void testFindOneByProperty() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
    createTestObjectAndReturnId("Test 2");
    createTestObjectAndReturnId("Test 3");
    TestEntity entity = esf.findOneByProperty(TestEntity.class, "name", "Test 2");
    assertNotNull(entity);
    assertEquals("Test 2", entity.getName());
  }

  @Test
  @Transactional
  public void testFindFirstByProperty() {
    long id1 = createTestObjectAndReturnId("Test 2");
    long id2 = createTestObjectAndReturnId("Test 2");
    long id3 = createTestObjectAndReturnId("Test 2");
    long id4 = createTestObjectAndReturnId("Test 2");
    long maxId = id1;
    if (id2 > id1) maxId = id2;
    if (id3 > id2) maxId = id3;
    if (id4 > id3) maxId = id4;
    TestEntity entity = esf.findFirstByProperty(TestEntity.class, "name", "Test 2", Order.desc("id"));
    assertNotNull(entity);
    assertEquals(maxId, entity.getId());
  }

  @Test
  @Transactional
  public void testLoad() throws InterruptedException {
    Date d1 = new Date();
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
    Date d2 = new Date();
    Thread.sleep(1000);
    long id = createTestObjectAndReturnId("Test 3");
    createTestObjectAndReturnId("Test 4");
    Map<String, String> filters = new HashMap<>();
    List<TestEntity> entityList = esf.load(TestEntity.class, 0, 2, "id", true, filters);
    assertNotNull(entityList);
    assertEquals(2, entityList.size());
    filters.put("name", "Test 1");
    entityList = esf.load(TestEntity.class, 0, 2, "id", true, filters);
    assertNotNull(entityList);
    assertEquals(1, entityList.size());
    filters.clear();
    filters.put("id", Long.toString(id));
    entityList = esf.load(TestEntity.class, 0, 2, "id", true, filters);
    assertNotNull(entityList);
    assertEquals(1, entityList.size());
    assertEquals("Test 3", entityList.get(0).getName());
    filters.clear();
    entityList = esf.load(TestEntity.class, 0, 4, "id", true, filters, "timestamp", d1, d2);
    assertNotNull(entityList);
    assertEquals(2, entityList.size());
    int max = esf.loadMaxCount(TestEntity.class,filters);
    assertEquals(4,max);
    filters.put("name", "Test 1");
    max = esf.loadMaxCount(TestEntity.class,filters);
    assertEquals(1,max);
  }

  @Test
  @Transactional
  public void testFindOneByPropertyReturnsNullWhenNothingFound() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
    createTestObjectAndReturnId("Test 2");
    createTestObjectAndReturnId("Test 3");
    TestEntity entity = esf.findOneByProperty(TestEntity.class, "name", "Test x");
    assertNull(entity);
  }

  @Test
  @Transactional
  public void testDelete() {
    createTestObjectAndReturnId("Test 1");
    createTestObjectAndReturnId("Test 2");
    createTestObjectAndReturnId("Test 3");
    List<TestEntity> entityList = esf.findByQuery(TestEntity.class, "from TestEntity");
    assertTrue(3 == entityList.size());
    entityList = esf.findByQuery(TestEntity.class, "from TestEntity l where l.name = 'Test 1'");
    assertTrue(1 == entityList.size());
    esf.makeTransient(entityList.get(0));
    entityList = esf.findByQuery(TestEntity.class, "from TestEntity");
    assertTrue(2 == entityList.size());
  }

  private long createTestObjectAndReturnId(String name) {
    TestEntity l = new TestEntity();
    l.setName(name);
    l.setTimestamp(new Date());
    assertTrue(l.getId() == 0);
    esf.makePersistent(l);
    assertTrue(l.getId() > 0);
    return l.getId();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    System.out.println("setSessionFactory");
    this.sessionFactory = sessionFactory;
  }

  public ExtendedSessionFactory getEsf() {
    return esf;
  }

  public void setEsf(ExtendedSessionFactory esf) {
    this.esf = esf;
  }

  public PlatformTransactionManager getTransactionManager() {
    return transactionManager;
  }

  public void setTransactionManager(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
    this.transactionTemplate = transactionTemplate;
  }
}
