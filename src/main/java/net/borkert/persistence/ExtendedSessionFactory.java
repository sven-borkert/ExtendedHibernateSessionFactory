package net.borkert.persistence;

import org.hibernate.LockOptions;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExtendedSessionFactory {

  private final SessionFactory sessionFactory;

  public ExtendedSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public <T> T makePersistent(T entity) {
    getSessionFactory().getCurrentSession().saveOrUpdate(entity);
    return entity;
  }

  public void makeTransient(Object entity) {
    getSessionFactory().getCurrentSession().delete(entity);
  }

  public <T, ID extends Serializable> T findById(Class<T> type, ID id) {
    return type.cast(getSessionFactory().getCurrentSession().get(type, id));
  }

  public <T, ID extends Serializable> T findByIdAndLockForUpdate(Class<T> type, ID id) {
    return type.cast(getSessionFactory().getCurrentSession().get(type, id, LockOptions.UPGRADE));
  }

  public <T> List<T> findAll(Class<T> type) {
    DetachedCriteria query = DetachedCriteria.forClass(type);
    return query.getExecutableCriteria(getSessionFactory().getCurrentSession()).list();
  }

  public <T> List<T> findAll(Class<T> type, Order order) {
    DetachedCriteria query = DetachedCriteria.forClass(type);
    query.addOrder(order);
    return query.getExecutableCriteria(getSessionFactory().getCurrentSession()).list();
  }

  public <T> List<T> findAll(Class<T> type, int limit) {
    DetachedCriteria query = DetachedCriteria.forClass(type);
    return query.getExecutableCriteria(getSessionFactory().getCurrentSession()).setMaxResults(limit).list();
  }

  public <T> List<T> findAll(Class<T> type, Order order, int limit) {
    DetachedCriteria query = DetachedCriteria.forClass(type);
    query.addOrder(order);
    return query.getExecutableCriteria(getSessionFactory().getCurrentSession()).setMaxResults(limit).list();
  }

  public <T> List<T> findAll(Class<T> type, Order order, int offset, int limit) {
    DetachedCriteria query = DetachedCriteria.forClass(type);
    query.addOrder(order);
    return query.getExecutableCriteria(getSessionFactory().getCurrentSession()).setFirstResult(offset).setMaxResults(limit).list();
  }

  public <T> List<T> findByQuery(Class<T> type, String queryString) {
    return getSessionFactory().getCurrentSession().createQuery(queryString).list();
  }

  public <T> List<T> findByQuery(Class<T> type, String queryString, int limit) {
    return getSessionFactory().getCurrentSession().createQuery(queryString).setMaxResults(limit).list();
  }

  public <T> List<T> findByQuery(Class<T> type, String queryString, int offset, int limit) {
    return getSessionFactory().getCurrentSession().createQuery(queryString).setMaxResults(limit).setFirstResult(offset).list();
  }

  public <T> List<T> findByCriteria(Class<T> type, DetachedCriteria criteria) {
    return criteria.getExecutableCriteria(getSessionFactory().getCurrentSession()).list();
  }

  public <T> List<T> findByCriteria(Class<T> type, DetachedCriteria criteria, int limit) {
    return criteria.getExecutableCriteria(getSessionFactory().getCurrentSession()).setMaxResults(limit).list();
  }

  public <T> List<T> findByCriteria(Class<T> type, DetachedCriteria criteria, Order order) {
    return criteria.getExecutableCriteria(getSessionFactory().getCurrentSession()).addOrder(order).list();
  }

  public <T> List<T> findByCriteria(Class<T> type, DetachedCriteria criteria, Order order, int limit) {
    return criteria.getExecutableCriteria(getSessionFactory().getCurrentSession()).addOrder(order).setMaxResults(limit).list();
  }

  public <T> List<T> findByCriteria(Class<T> type, DetachedCriteria criteria, Order order, int offset, int limit) {
    return criteria.getExecutableCriteria(getSessionFactory().getCurrentSession()).addOrder(order).setFirstResult(offset).setMaxResults(limit).list();
  }

  public <T> List<T> findByProperty(Class<T> type, String property, Object value) {
    DetachedCriteria query = DetachedCriteria.forClass(type);
    query.add(Restrictions.eq(property, value));
    return query.getExecutableCriteria(getSessionFactory().getCurrentSession()).list();
  }

  public <T> List<T> findByProperty(Class<T> type, String property, Object value, Order order) {
    DetachedCriteria query = DetachedCriteria.forClass(type);
    query.add(Restrictions.eq(property, value));
    query.addOrder(order);
    return query.getExecutableCriteria(getSessionFactory().getCurrentSession()).list();
  }

  public <T> List<T> findByProperty(Class<T> type, String property, Object value, int limit) {
    DetachedCriteria query = DetachedCriteria.forClass(type);
    query.add(Restrictions.eq(property, value));
    return query.getExecutableCriteria(getSessionFactory().getCurrentSession()).setMaxResults(limit).list();
  }

  public <T> List<T> findByProperty(Class<T> type, String property, Object value, Order order, int limit) {
    DetachedCriteria query = DetachedCriteria.forClass(type);
    query.add(Restrictions.eq(property, value));
    query.addOrder(order);
    return query.getExecutableCriteria(getSessionFactory().getCurrentSession()).setMaxResults(limit).list();
  }

  public <T> List<T> findByProperty(Class<T> type, String property, Object value, Order order, int offset, int limit) {
    DetachedCriteria query = DetachedCriteria.forClass(type);
    query.add(Restrictions.eq(property, value));
    query.addOrder(order);
    return query.getExecutableCriteria(getSessionFactory().getCurrentSession()).setMaxResults(limit).setFirstResult(offset).list();
  }

  public <T> T findOneByProperty(Class<T> type, String property, Object value) {
    DetachedCriteria query = DetachedCriteria.forClass(type);
    query.add(Restrictions.eq(property, value));
    List<T> result = query.getExecutableCriteria(getSessionFactory().getCurrentSession()).setFirstResult(0).setMaxResults(1).list();
    return result.size() > 0 ? result.get(0) : null;
  }

  public <T> T findFirstByProperty(Class<T> type, String property, Object value, Order order) {
    DetachedCriteria query = DetachedCriteria.forClass(type);
    query.add(Restrictions.eq(property, value));
    query.addOrder(order);
    List<T> result = query.getExecutableCriteria(getSessionFactory().getCurrentSession()).setFirstResult(0).setMaxResults(1).list();
    return result.size() > 0 ? result.get(0) : null;
  }

  public <T> List<T> load(Class type, int first, int pageSize, String sortField, boolean sortAsc, Map<String, Object> filters) {
    DetachedCriteria query = createFilterCriteria(type, filters);
    if (sortField != null && !sortField.equals("")) {
      if (sortAsc) {
        query.addOrder(Order.asc(sortField));
      } else {
        query.addOrder(Order.desc(sortField));
      }
    }
    return query.getExecutableCriteria(getSessionFactory().getCurrentSession()).setFirstResult(first).setMaxResults(pageSize).list();
  }

  public <T> List<T> load(Class type, int first, int pageSize, String sortField, boolean sortAsc, Map<String, Object> filters, String dateProperty, Date from, Date to) {
    DetachedCriteria query = createFilterCriteria(type, filters);
    if (from != null) {
      query.add(Restrictions.ge(dateProperty, from));
    }
    if (to != null) {
      query.add(Restrictions.le(dateProperty, to));
    }
    if (sortField != null && !sortField.equals("")) {
      if (sortAsc) {
        query.addOrder(Order.asc(sortField));
      } else {
        query.addOrder(Order.desc(sortField));
      }
    }
    return query.getExecutableCriteria(getSessionFactory().getCurrentSession()).setFirstResult(first).setMaxResults(pageSize).list();
  }

  public int loadMaxCount(Class type, Map<String, Object> filters) {
    DetachedCriteria query = createFilterCriteria(type, filters);
    ProjectionList projectList = Projections.projectionList();
    projectList.add(Projections.alias(Projections.rowCount(), "count"));
    query.setProjection(projectList);
    List results = query.getExecutableCriteria(getSessionFactory().getCurrentSession()).list();
    if (results.size() == 0) {
      return 0;
    }
    return ((Long) results.get(0)).intValue();
  }

  public void flush() {
    getSessionFactory().getCurrentSession().flush();
  }

  public void clear() {
    getSessionFactory().getCurrentSession().clear();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  private DetachedCriteria createFilterCriteria(Class type, Map<String, Object> filters) {
    DetachedCriteria query = DetachedCriteria.forClass(type);
    for (String property : filters.keySet()) {
      String[] parts = property.split("\\.");
      if (parts.length > 2) {
        throw new RuntimeException("Not implemented");
      }
      if (!(filters.get(property) instanceof String)) {
        throw new RuntimeException("Not implemented");
      }
      if (getPropertyClassName(type, property).equals("java.lang.String")) {
        if (parts.length == 1) {
          query.add(Restrictions.like(property, "%" + filters.get(property) + "%").ignoreCase());
        }
        if (parts.length == 2) {
          query.createAlias(parts[0], parts[0]).add(Restrictions.like(property, "%" + filters.get(property) + "%").ignoreCase());
        }
      }
      if (getPropertyClassName(type, property).equals("java.lang.Integer")) {
        if (parts.length == 1) {
          query.add(Restrictions.eq(property, Integer.parseInt((String) filters.get(property))));
        }
        if (parts.length == 2) {
          query.createAlias(parts[0], parts[0]).add(Restrictions.eq(property, Integer.parseInt((String) filters.get(property))));
        }
      }
      if (getPropertyClassName(type, property).equals("int")) {
        if (parts.length == 1) {
          query.add(Restrictions.eq(property, Integer.parseInt((String) filters.get(property))));
        }
        if (parts.length == 2) {
          query.createAlias(parts[0], parts[0]).add(Restrictions.eq(property, Integer.parseInt((String) filters.get(property))));
        }
      }
      if (getPropertyClassName(type, property).equals("java.lang.Long")) {
        if (parts.length == 1) {
          query.add(Restrictions.eq(property, Long.parseLong((String) filters.get(property))));
        }
        if (parts.length == 2) {
          query.createAlias(parts[0], parts[0]).add(Restrictions.eq(property, Long.parseLong((String) filters.get(property))));
        }
      }
      if (getPropertyClassName(type, property).equals("long")) {
        if (parts.length == 1) {
          query.add(Restrictions.eq(property, Long.parseLong((String) filters.get(property))));
        }
        if (parts.length == 2) {
          query.createAlias(parts[0], parts[0]).add(Restrictions.eq(property, Long.parseLong((String) filters.get(property))));
        }
      }
    }
    return query;
  }

  private Class getPropertyClass(Class c, String property) {
    try {
      Method m = c.getMethod("get" + firstCharToUpper(property));
      return m.getReturnType();
    } catch (NoSuchMethodException e) {
      throw new ExtendedSessionFactoryException(e);
    }
  }

  private String getPropertyClassName(Class c, String property) {
    Class result = getPropertyClass(c, property);
    return result != null ? result.getName() : null;
  }

  private String firstCharToUpper(String s) {
    final StringBuilder result = new StringBuilder(s.length());
    String[] words = s.split("\\s");
    for (int i = 0, l = words.length; i < l; ++i) {
      if (i > 0) result.append(" ");
      result.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1));
    }
    return result.toString();
  }
}
