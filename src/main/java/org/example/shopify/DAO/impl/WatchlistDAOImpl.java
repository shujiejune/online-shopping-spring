package org.example.shopify.DAO.impl;

import org.example.shopify.DAO.WatchlistDAO;
import org.example.shopify.Domain.Watchlist;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.List;

@Repository
public class WatchlistDAOImpl implements WatchlistDAO {
    @PersistenceContext
    private EntityManager em;

    @Override
    public void add(Watchlist item) {
        em.persist(item);
    }

    @Override
    public void remove(Long userId, Long productId) {
        em.createQuery("DELETE FROM Watchlist w WHERE w.user.id=:userId AND w.product.id=:productId")
                .setParameter("userId", userId).setParameter("productId", productId)
                .executeUpdate();
    }

    @Override
    public List<Watchlist> getInStockWatchlist(Long userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Watchlist> cq = cb.createQuery(Watchlist.class);
        Root<Watchlist> watchlist = cq.from(Watchlist.class);

        Join<Object, Object> productJoin = watchlist.join("product");
        Predicate userPredicate = cb.equal(productJoin.get("user").get("id"), userId);
        Predicate stockPredicate = cb.gt(productJoin.get("quantity"), 0);

        cq.where(cb.and(userPredicate, stockPredicate));

        return em.createQuery(cq).getResultList();
    }
}
