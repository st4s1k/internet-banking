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

import static java.util.Collections.emptyList;
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

    public Optional<Transfer> update(Transfer transfer) {
        entityManager.merge(transfer);
        return findById(transfer.getId());
    }

    public Optional<Transfer> remove(Transfer transfer) {
        Optional<Transfer> transferToBeRemoved = findById(transfer.getId());
        transferToBeRemoved.ifPresent(_transfer -> entityManager.remove(
                entityManager.contains(_transfer) ? _transfer : entityManager.merge(_transfer)));
        return transferToBeRemoved;
    }

    public List<Transfer> findAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = criteriaBuilder.createQuery(Transfer.class);
        Root<Transfer> from = query.from(Transfer.class);
        query.select(from);
        return entityManager.createQuery(query).getResultList();
    }

    public Optional<Transfer> findById(Long transferId) {
        return Optional.ofNullable(entityManager.find(Transfer.class, transferId));
    }

    public Optional<Transfer> find(Transfer transfer) {
        return Optional.ofNullable(transfer)
                .flatMap(t -> Optional.ofNullable(t.getId()))
                .flatMap(this::findById);
    }

    public List<Transfer> findByAccountId(Long accountId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = criteriaBuilder.createQuery(Transfer.class);
        Root<Transfer> from = query.from(Transfer.class);
        Predicate accountIdCriteria = criteriaBuilder.equal(from.get("account_id"), accountId);
        query.where(accountIdCriteria);
        return entityManager.createQuery(query).getResultList();
    }

    public List<Transfer> findByAccount(Account account) {
        return Optional.ofNullable(account)
                .flatMap(a -> Optional.ofNullable(a.getId()))
                .map(this::findByAccountId)
                .orElse(emptyList());
    }
}
