package org.example.shopify.DAO.impl;

import org.example.shopify.DAO.CartDAO;
import org.example.shopify.Domain.CartItem;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CartDAOImpl implements CartDAO {
    private final SessionFactory sessionFactory;

    public CartDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Optional<CartItem> getCartItemById(Long itemId) {
        return Optional.ofNullable(getSession().get(CartItem.class, itemId));
    }

    @Override
    public List<CartItem> getCartByUserId(Long userId) {
        String hql = "FROM CartItem c WHERE c.user.id = :userId";
        return getSession().createQuery(hql, CartItem.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public Optional<CartItem> findExistingItem(Long userId, Long productId) {
        String hql = "FROM CartItem c WHERE c.user.id = :userId AND c.product.id = :productId";
        CartItem item = getSession().createQuery(hql, CartItem.class)
                .setParameter("userId", userId)
                .setParameter("productId", productId)
                .uniqueResult();
        return Optional.ofNullable(item);
    }

    @Override
    public void addItemToCart(CartItem cartItem) {
        getSession().save(cartItem);
    }

    @Override
    public void updateQuantity(Long itemId, Integer quantity) {
        CartItem item = getSession().get(CartItem.class, itemId);
        if (item != null) {
            item.setQuantity(quantity);
        }
    }

    @Override
    public void removeItem(Long itemId) {
        CartItem item = getSession().get(CartItem.class, itemId);
        if (item != null) {
            getSession().delete(item);
        }
    }

    @Override
    public void clearCart(Long userId) {
        String hql = "DELETE FROM CartItem c WHERE c.user.id = :userId";
        getSession().createQuery(hql)
                .setParameter("userId", userId)
                .executeUpdate();
    }
}
