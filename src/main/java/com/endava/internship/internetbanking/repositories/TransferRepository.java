package com.endava.internship.internetbanking.repositories;

import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        Predicate destinationAccountIdCriteria = criteriaBuilder.equal(from.get("sourceAccount"), accountId);
        Predicate sourceAccountIdCriteria = criteriaBuilder.equal(from.get("destinationAccount"), accountId);
        query.where(criteriaBuilder.or(sourceAccountIdCriteria, destinationAccountIdCriteria));
        return entityManager.createQuery(query).getResultList();
    }

    public List<Transfer> findByAccount(Account account) {
        return Optional.ofNullable(account)
                .flatMap(a -> Optional.ofNullable(a.getId()))
                .map(this::findByAccountId)
                .orElse(emptyList());
    }

    public List<Transfer> findAllBefore(LocalDateTime dateTime) {
        return findBefore(dateTime).getResultList();
    }

    public List<Transfer> findAllAfter(LocalDateTime dateTime) {
        return findAfter(dateTime).getResultList();
    }

    public List<Transfer> findAllBefore(Set<Account> accounts, LocalDateTime dateTime) {
        return findBefore(accounts, dateTime).getResultList();
    }

    public List<Transfer> findAllAfter(Set<Account> accounts, LocalDateTime dateTime) {
        return findAfter(accounts, dateTime).getResultList();
    }

    private TypedQuery<Transfer> findBefore(Set<Account> accounts, LocalDateTime dateTime) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = criteriaBuilder.createQuery(Transfer.class);
        Root<Transfer> from = query.from(Transfer.class);

        Predicate queryCriteria = criteriaBuilder.and(
                criteriaBuilder.or(
                        from.get("sourceAccount").in(accounts),
                        from.get("destinationAccount").in(accounts)),
                criteriaBuilder.lessThanOrEqualTo(from.get("dateTime"), Timestamp.valueOf(dateTime)));

        query.where(queryCriteria).orderBy(criteriaBuilder.desc(from.get("dateTime")));

        return entityManager.createQuery(query);
    }

    private TypedQuery<Transfer> findAfter(Set<Account> accounts, LocalDateTime dateTime) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = criteriaBuilder.createQuery(Transfer.class);
        Root<Transfer> from = query.from(Transfer.class);

        Predicate queryCriteria = criteriaBuilder.and(
                criteriaBuilder.or(
                        from.get("sourceAccount").in(accounts),
                        from.get("destinationAccount").in(accounts)),
                criteriaBuilder.greaterThanOrEqualTo(from.get("dateTime"), Timestamp.valueOf(dateTime)));

        query.where(queryCriteria).orderBy(criteriaBuilder.desc(from.get("dateTime")));

        return entityManager.createQuery(query);
    }

    private TypedQuery<Transfer> findBefore(LocalDateTime dateTime) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = criteriaBuilder.createQuery(Transfer.class);
        Root<Transfer> from = query.from(Transfer.class);

        Predicate queryCriteria = criteriaBuilder.lessThanOrEqualTo(from.get("dateTime"), Timestamp.valueOf(dateTime));

        query.where(queryCriteria).orderBy(criteriaBuilder.desc(from.get("dateTime")));
        return entityManager.createQuery(query);
    }

    private TypedQuery<Transfer> findAfter(LocalDateTime dateTime) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = criteriaBuilder.createQuery(Transfer.class);
        Root<Transfer> from = query.from(Transfer.class);

        Predicate queryCriteria = criteriaBuilder.greaterThanOrEqualTo(from.get("dateTime"), dateTime);

        query.where(queryCriteria).orderBy(criteriaBuilder.desc(from.get("dateTime")));
        return entityManager.createQuery(query);
    }
}
