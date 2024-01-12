package com.apartment.apart.domain.report;

import com.apartment.apart.domain.community.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findAll(Pageable pageable);
    Page<Report> findAll(Specification<Report> spec, Pageable pageable);
}

