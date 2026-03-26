package com.example.service;

import com.example.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional(Transactional.TxType.SUPPORTS)
  public PageResponse<User> getUsers(int page, int size, String name) {
    int safePage = Math.max(page, 1);
    int safeSize = Math.max(size, 1);
    String keyword = name == null ? "" : name.trim();

    StringBuilder dataJpql = new StringBuilder("select u from User u");
    StringBuilder countJpql = new StringBuilder("select count(u) from User u");
    Map<String, Object> params = new HashMap<>();

    if (!keyword.isEmpty()) {
      dataJpql.append(" where lower(u.name) like :name");
      countJpql.append(" where lower(u.name) like :name");
      params.put("name", "%" + keyword.toLowerCase() + "%");
    }

    dataJpql.append(" order by u.id desc");

    TypedQuery<User> dataQuery = entityManager.createQuery(dataJpql.toString(), User.class);
    TypedQuery<Long> countQuery = entityManager.createQuery(countJpql.toString(), Long.class);

    for (Map.Entry<String, Object> entry : params.entrySet()) {
      dataQuery.setParameter(entry.getKey(), entry.getValue());
      countQuery.setParameter(entry.getKey(), entry.getValue());
    }

    dataQuery.setFirstResult((safePage - 1) * safeSize);
    dataQuery.setMaxResults(safeSize);

    List<User> records = dataQuery.getResultList();
    long total = countQuery.getSingleResult();
    return new PageResponse<>(safePage, safeSize, total, records);
  }

  public User createUser(User user) {
    user.setId(null);
    entityManager.persist(user);
    entityManager.flush();
    return user;
  }

  public static class PageResponse<T> {
    private final int page;
    private final int size;
    private final long total;
    private final List<T> records;

    public PageResponse(int page, int size, long total, List<T> records) {
      this.page = page;
      this.size = size;
      this.total = total;
      this.records = records;
    }

    public int getPage() {
      return page;
    }

    public int getSize() {
      return size;
    }

    public long getTotal() {
      return total;
    }

    public List<T> getRecords() {
      return records;
    }
  }
}
