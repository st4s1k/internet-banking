package com.endava.internship.internetbanking.repositories;

import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

@Repository
@Transactional(REQUIRES_NEW)
public class TransferRepository {

    private final EntityManager entityManager;

    @Autowired
    public TransferRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Transfer> save(Transfer transfer) {
        entityManager.persist(transfer);
        entityManager.flush();
        return Optional.of(transfer).filter(a -> a.getId() != null);
    }

    public Optional<Transfer> remove(Transfer transfer) {
        Optional<Transfer> transferToBeRemoved = findById(transfer.getId());
        transferToBeRemoved.ifPresent(_transfer -> entityManager.remove(
                entityManager.contains(_transfer) ? _transfer : entityManager.merge(_transfer)));
        return transferToBeRemoved;
    }

    public Optional<Transfer> findById(Long transferId) {
        return Optional.ofNullable(entityManager.find(Transfer.class, transferId));
    }

    public List<Transfer> findByUser(Account account) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = criteriaBuilder.createQuery(Transfer.class);
        Root<Transfer> from = query.from(Transfer.class);
        Predicate userIdCriteria = criteriaBuilder.equal(from.get("account_id"), account.getId());
        query.where(userIdCriteria);
        return entityManager.createQuery(query).getResultList();
    }

    public List<Transfer> findByUserId(Long accountId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = criteriaBuilder.createQuery(Transfer.class);
        Root<Transfer> from = query.from(Transfer.class);
        Predicate userIdCriteria = criteriaBuilder.equal(from.get("account_id"), accountId);
        query.where(userIdCriteria);
        return entityManager.createQuery(query).getResultList();
    }

    public List<Transfer> findAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = criteriaBuilder.createQuery(Transfer.class);
        Root<Transfer> from = query.from(Transfer.class);
        query.select(from);
        return entityManager.createQuery(query).getResultList();
    }

    public Optional<Transfer> update(Transfer transfer) {
        entityManager.merge(transfer);
        return findById(transfer.getId());
    }
}
