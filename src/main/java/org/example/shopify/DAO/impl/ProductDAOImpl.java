package org.example.shopify.DAO.impl;

import org.example.shopify.DAO.ProductDAO;
import org.example.shopify.Domain.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductDAOImpl implements ProductDAO {
    private final SessionFactory sessionFactory;

    public ProductDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    };

    @Override
    public Optional<Product> getProductById(Long id) {
        return Optional.ofNullable(getSession().find(Product.class, id));
    }

    @Override
    public List<Product> getInStockProducts() {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> product = cq.from(Product.class);

        cq.select(product).where(cb.gt(product.get("quantity"), 0));

        return getSession().createQuery(cq).getResultList();
    }

    @Override
    public List<Product> getPaginatedProducts(int page, int size) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Product", Product.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public long getTotalProductsCount() {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT count(p) FROM Product p", Long.class)
                .uniqueResult();
    }

    @Override
    public void saveOrUpdateProduct(Product product) {
        if (product.getId() == null) {
            getSession().persist(product);
        } else  {
            getSession().merge(product);
        }
    }
}
