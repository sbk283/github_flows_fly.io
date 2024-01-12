package com.apartment.apart.domain.community;

import com.apartment.apart.domain.notice.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    Page<Community> findAll(Pageable pageable);
    Page<Community> findAll(Specification<Community> spec, Pageable pageable);
}
