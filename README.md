ExtendedHibernateSessionFactory
==================

A convenience wrapper for Hibernate SessionFactory

## Examples

### Fetch one
    TestEntity e = esf.findById(TestEntity.class, id);
    TestEntity e = esf.findByIdAndLockForUpdate(TestEntity.class, id);

### Persist / Delete
    public <T> T makePersistent(T entity)
    public void makeTransient(Object entity)

### Fetch many
    List<TestEntity> entityList = esf.findAll(TestEntity.class);
    List<TestEntity> entityList = esf.findByQuery(TestEntity.class, "from TestEntity l where l.name = 'Foo'");
    List<TestEntity> entityList = esf.findAll(TestEntity.class, Order.desc("id"), <limit>);
    List<TestEntity> entityList = esf.findAll(TestEntity.class, Order.desc("id"), <offset>, <limit>);
    List<TestEntity> entityList = esf.findByQuery(TestEntity.class, "from TestEntity e order by e.id", <offset>, <limit>);
    List<TestEntity> entityList = esf.findByCriteria(TestEntity.class, query);
    List<TestEntity> entityList = esf.findByCriteria(TestEntity.class, query, <limit>);
    List<TestEntity> entityList = esf.findByCriteria(TestEntity.class, query, Order.asc("name"));
    List<TestEntity> entityList = esf.findByCriteria(TestEntity.class, query, Order.asc("name"), <limit>);
    List<TestEntity> entityList = esf.findByCriteria(TestEntity.class, query, Order.asc("name"), <offset>, <limit>);
    List<TestEntity> entityList = esf.findByProperty(TestEntity.class, "name", "Test 1");
    List<TestEntity> entityList = esf.findByProperty(TestEntity.class, "name", "Test 1", Order.asc("id"));
    List<TestEntity> entityList = esf.findByProperty(TestEntity.class, "name", "Test 1", <limit>);
    List<TestEntity> entityList = esf.findByProperty(TestEntity.class, "name", "Test 1", Order.asc("id"), <limit>);
    List<TestEntity> entityList = esf.findByProperty(TestEntity.class, "name", "Test 1", Order.desc("id"), <offset>, <limit>);

### Fetch by single property
    TestEntity entity = esf.findOneByProperty(TestEntity.class, "name", "Test 2");
    TestEntity entity = esf.findFirstByProperty(TestEntity.class, "name", "Test 2", Order.desc("id"));

### 'load' methods for some lazy loading JSF tables
    public <T> List<T> load(Class type, int first, int pageSize, String sortField, boolean sortAsc, Map<String, String> filters)
    public <T> List<T> load(Class type, int first, int pageSize, String sortField, boolean sortAsc, Map<String, String> filters, String dateProperty, Date from, Date to)
    public int loadMaxCount(Class type, Map<String, String> filters)