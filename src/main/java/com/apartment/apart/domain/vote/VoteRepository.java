package com.apartment.apart.domain.vote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    Page<Vote> findAll(Pageable pageable);
    Page<Vote> findAll(Specification<Vote> spec, Pageable pageable);
    @Query("SELECT v FROM Vote v WHERE v.title LIKE %:kw% OR v.content LIKE %:kw%")
    List<Vote> findByKeyword(@Param("kw") String kw);
    @Query("SELECT v FROM Vote v WHERE (v.title LIKE %:kw% OR v.content LIKE %:kw%) " +
            "AND (v.startDate <= :today AND v.endDate >= :today)")
    List<Vote> findByKeywordAndInProgress(@Param("kw") String kw, @Param("today") LocalDate today);
    @Query("SELECT v FROM Vote v WHERE (v.title LIKE %:kw% OR v.content LIKE %:kw%) AND v.endDate < :today")
    List<Vote> findByKeywordAndClosed(@Param("kw") String kw, @Param("today") LocalDate today);
    @Query("SELECT v FROM Vote v WHERE (v.title LIKE %:kw% OR v.content LIKE %:kw%) AND v.startDate > :today")
    List<Vote> findByKeywordAndIntended(@Param("kw") String kw, @Param("today") LocalDate today);
}

