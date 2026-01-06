package org.example.shopify.DAO.impl;

import org.example.shopify.DAO.ProductDAO;
import org.example.shopify.Domain.Order;
import org.example.shopify.Domain.OrderItem;
import org.example.shopify.Domain.OrderStatus;
import org.example.shopify.Domain.Product;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductDAOImpl implements ProductDAO {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Product> getProductById(Long id) {
        return Optional.ofNullable(em.find(Product.class, id));
    }

    @Override
    public List<Product> getAllProducts() {
        return em.createQuery("FROM Product", Product.class).getResultList();
    }

    @Override
    public List<Product> getInStockProducts() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> product = cq.from(Product.class);

        cq.select(product).where(cb.gt(product.get("quantity"), 0));

        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<Product> getProductsByUserId(Long userId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<OrderItem> orderItem = cq.from(OrderItem.class);

        Join<OrderItem, Order> orderJoin = orderItem.join("order");
        cq.select(orderItem.get("product")).distinct(true);

        Predicate userPredicate = cb.equal(orderJoin.get("user").get("id"), userId);
        Predicate notCancelledPredicate = cb.notEqual(orderJoin.get("orderStatus"), OrderStatus.Cancelled);

        cq.where(cb.and(userPredicate, notCancelledPredicate));
        cq.orderBy(cb.desc(orderItem.get("id")));

        return em.createQuery(cq).setMaxResults(limit).getResultList();
    }

    @Override
    public void saveOrUpdateProduct(Product product) {
        if (product.getId() == null) {
            em.persist(product);
        } else  {
            em.merge(product);
        }
    }
}
