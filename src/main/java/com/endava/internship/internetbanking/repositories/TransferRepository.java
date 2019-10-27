package com.endava.internship.internetbanking.repositories;

import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyList;

@Repository
public class TransferRepository {

    private final AccountRepository accountRepository;

    private final EntityManager entityManager;

    @Autowired
    public TransferRepository(AccountRepository accountRepository,
                              EntityManager entityManager) {
        this.accountRepository = accountRepository;
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
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = cb.createQuery(Transfer.class);
        Root<Transfer> transferFields = query.from(Transfer.class);

        query.select(transferFields);

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

    public List<Transfer> findByAccount(Account account) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = cb.createQuery(Transfer.class);
        Root<Transfer> transferFields = query.from(Transfer.class);

        query.select(transferFields)
                .where(cb.or(
                        cb.equal(transferFields.get("sourceAccount"), account),
                        cb.equal(transferFields.get("destinationAccount"), account)));

        return entityManager.createQuery(query).getResultList();
    }

    public List<Transfer> findByAccountId(Long accountId) {
        return Optional.ofNullable(accountId)
                .flatMap(accountRepository::findById)
                .map(this::findByAccount)
                .orElse(emptyList());
    }

    private List<Transfer> findAllBefore(LocalDateTime dateTime) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = cb.createQuery(Transfer.class);
        Root<Transfer> transferFields = query.from(Transfer.class);

        query.select(transferFields)
                .where(cb.lessThanOrEqualTo(transferFields.get("dateTime"), dateTime))
                .orderBy(cb.desc(transferFields.get("dateTime")));

        return entityManager.createQuery(query).getResultList();
    }

    private List<Transfer> findAllAfter(LocalDateTime dateTime) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = cb.createQuery(Transfer.class);
        Root<Transfer> transferFields = query.from(Transfer.class);

        query.select(transferFields)
                .where(cb.greaterThanOrEqualTo(transferFields.get("dateTime"), dateTime))
                .orderBy(cb.desc(transferFields.get("dateTime")));

        return entityManager.createQuery(query).getResultList();
    }

    private List<Transfer> findAllBefore(Account account, LocalDateTime dateTime) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = cb.createQuery(Transfer.class);
        Root<Transfer> transferFields = query.from(Transfer.class);

        query.select(transferFields)
                .where(cb.lessThanOrEqualTo(transferFields.get("dateTime"), dateTime),
                        cb.or(cb.equal(transferFields.get("sourceAccount"), account),
                                cb.equal(transferFields.get("destinationAccount"), account)))
                .orderBy(cb.desc(transferFields.get("dateTime")));

        return entityManager.createQuery(query).getResultList();
    }

    private List<Transfer> findAllAfter(Account account, LocalDateTime dateTime) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = cb.createQuery(Transfer.class);
        Root<Transfer> transferFields = query.from(Transfer.class);

        query.select(transferFields)
                .where(cb.greaterThanOrEqualTo(transferFields.get("dateTime"), dateTime),
                        cb.or(cb.equal(transferFields.get("sourceAccount"), account),
                                cb.equal(transferFields.get("destinationAccount"), account)))
                .orderBy(cb.desc(transferFields.get("dateTime")));

        return entityManager.createQuery(query).getResultList();
    }

    private List<Transfer> findAllBefore(Set<Account> accounts, LocalDateTime dateTime) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = cb.createQuery(Transfer.class);
        Root<Transfer> transferFields = query.from(Transfer.class);

        query.select(transferFields)
                .where(cb.lessThanOrEqualTo(transferFields.get("dateTime"), dateTime),
                        cb.or(transferFields.get("sourceAccount").in(accounts),
                                transferFields.get("destinationAccount").in(accounts)))
                .orderBy(cb.desc(transferFields.get("dateTime")));

        return entityManager.createQuery(query).getResultList();
    }

    private List<Transfer> findAllAfter(Set<Account> accounts, LocalDateTime dateTime) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> query = cb.createQuery(Transfer.class);
        Root<Transfer> transferFields = query.from(Transfer.class);

        query.select(transferFields)
                .where(cb.greaterThanOrEqualTo(transferFields.get("dateTime"), dateTime),
                        cb.or(transferFields.get("sourceAccount").in(accounts),
                                transferFields.get("destinationAccount").in(accounts)))
                .orderBy(cb.desc(transferFields.get("dateTime")));

        return entityManager.createQuery(query).getResultList();
    }
}
