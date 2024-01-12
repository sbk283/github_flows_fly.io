package com.apartment.apart.domain.vote;

import com.apartment.apart.domain.user.SiteUser;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;

    public List<Vote> findAll(String kw, String status) {

        return switch (status) {
            case "total" -> this.voteRepository.findByKeyword(kw);
            case "inProgress" -> this.voteRepository.findByKeywordAndInProgress(kw, LocalDate.now());
            case "closed" -> this.voteRepository.findByKeywordAndClosed(kw, LocalDate.now());
            case "intended" -> this.voteRepository.findByKeywordAndIntended(kw, LocalDate.now());
            default -> this.voteRepository.findAll();
        };
    }

    public void save(@Valid VoteForm voteForm, SiteUser siteUser) {
        Vote vote = Vote.builder()
                .user(siteUser)
                .title(voteForm.getTitle())
                .content(voteForm.getContent())
                .startDate(LocalDate.parse(voteForm.getStart()))
                .endDate(LocalDate.parse(voteForm.getEnd()))
                .build();

        this.voteRepository.save(vote);
    }

    public Vote findById(Long id) {
        Optional<Vote> ov = this.voteRepository.findById(id);
        return ov.get();
    }

    public void delete(Vote vote) {
        this.voteRepository.delete(vote);
    }

    public Page<Vote> getPageList(int page, String kw, String status) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Vote> spec = createSpecification(kw, status);
        return this.voteRepository.findAll(spec, pageable);
    }

    private Specification<Vote> createSpecification(String kw, String status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (kw != null && !kw.isEmpty()) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("title"), "%" + kw + "%"),
                        criteriaBuilder.like(root.get("content"), "%" + kw + "%")
                ));
            }
            LocalDate today = LocalDate.now();
            if ("inProgress".equals(status)) {
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), today),
                        criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), today)
                ));
            } else if ("closed".equals(status)) {
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.lessThan(root.get("endDate"), today)
                ));
            } else if ("intended".equals(status)) {
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.greaterThan(root.get("startDate"), today)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
