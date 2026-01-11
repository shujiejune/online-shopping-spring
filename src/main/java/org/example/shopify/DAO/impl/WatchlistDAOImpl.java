package org.example.shopify.DAO.impl;

import org.example.shopify.DAO.WatchlistDAO;
import org.example.shopify.Domain.Watchlist;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.List;

@Repository
public class WatchlistDAOImpl implements WatchlistDAO {
    private final SessionFactory sessionFactory;

    public WatchlistDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    };

    @Override
    public void add(Watchlist item) {
        getSession().persist(item);
    }

    @Override
    public void remove(Long userId, Long productId) {
        getSession().createQuery("DELETE FROM Watchlist w WHERE w.user.id=:userId AND w.product.id=:productId")
                .setParameter("userId", userId).setParameter("productId", productId)
                .executeUpdate();
    }

    @Override
    public List<Watchlist> getInStockWatchlist(Long userId) {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Watchlist> cq = cb.createQuery(Watchlist.class);
        Root<Watchlist> watchlist = cq.from(Watchlist.class);

        Join<Object, Object> productJoin = watchlist.join("product");
        Predicate userPredicate = cb.equal(watchlist.get("user").get("id"), userId);
        Predicate stockPredicate = cb.gt(productJoin.get("quantity"), 0);

        cq.where(cb.and(userPredicate, stockPredicate));

        return getSession().createQuery(cq).getResultList();
    }
}
