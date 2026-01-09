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
    public void saveOrUpdateProduct(Product product) {
        if (product.getId() == null) {
            em.persist(product);
        } else  {
            em.merge(product);
        }
    }
}
