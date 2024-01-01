package org.example.lecturereview;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ProductReviewsService {
    private final HashMap<Integer, List<String>> productIdToReviews;

    private ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private Lock readLock = rwLock.readLock();
    private Lock writeLock = rwLock.writeLock();


    public ProductReviewsService() {
        this.productIdToReviews = new HashMap<>();
    }

    public void addProduct(int productId) {
        Lock lock = getLockForAddProduct();
        lock.lock();

        try {
            if(!productIdToReviews.containsKey(productId)) {
                productIdToReviews.put(productId, new ArrayList<>());
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeProduct(int productId) {
        Lock lock = getLockForRemoveProduct();
        lock.lock();

        try {
            if(productIdToReviews.containsKey(productId)) {
                productIdToReviews.remove(productId);
            }
        } finally {
            lock.unlock();
        }
    }

    public void addProductReviews(int productId, String review) {
        Lock lock = getLockForAddProductReview();
        lock.lock();

        try {
            if(!productIdToReviews.containsKey(productId)) {
                productIdToReviews.put(productId, new ArrayList<>());
            }
            productIdToReviews.get(productId).add(review);
        } finally {
            lock.unlock();
        }
    }

    public List<String> getAllProductReviews(int productId) {
        Lock lock = getLockForGetAllProductReviews();
        lock.lock();
        try {
            if(productIdToReviews.containsKey(productId)) {
                return Collections.unmodifiableList(productIdToReviews.get(productId));
            }
        } finally {
            lock.unlock();
        }

        return Collections.emptyList();
    }

    public Optional<String> getLatestReview(int productId) {
        Lock lock = getLockForGetLatestReview();
        lock.lock();

        try {
            if(productIdToReviews.containsKey(productId) && !productIdToReviews.get(productId).isEmpty()) {
                List<String> reviews = productIdToReviews.get(productId);
                return Optional.of(reviews.get(reviews.size() - 1));
            }
        } finally {
            lock.unlock();
        }

        return Optional.empty();
    }

    public Set<Integer> getAllProductIdsWithReviews() {
        Lock lock = getLockForGetAllProductIdsWithReviews();
        lock.lock();

        try {
            Set<Integer> productsWithReviews = new HashSet<>();
            for (Map.Entry<Integer, List<String>> productEntry: productIdToReviews.entrySet()) {
                if(!productEntry.getValue().isEmpty()) {
                    productsWithReviews.add(productEntry.getKey());
                }
            }
            return productsWithReviews;
        } finally {
            lock.unlock();
        }
    }

    Lock getLockForAddProduct() {
        // Add code here
        return writeLock;
    }

    Lock getLockForRemoveProduct() {
        // add code here
        return writeLock;
    }

    Lock getLockForAddProductReview() {
        // add code here
        return writeLock;
    }

    Lock getLockForGetAllProductReviews() {
        // add code here
        return readLock;
    }

    Lock getLockForGetLatestReview() {
        // add code here
        return readLock;
    }

    Lock getLockForGetAllProductIdsWithReviews() {
        // add code here
        return readLock;
    }
}
