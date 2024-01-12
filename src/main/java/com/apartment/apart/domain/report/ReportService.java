package com.apartment.apart.domain.report;

import com.apartment.apart.domain.user.SiteUser;
import com.apartment.apart.domain.user.UserRepository;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public Page<Report> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Report> spec = search(kw);
        return this.reportRepository.findAll(spec, pageable);
    }

    public Report getReport(Long id, String username) {

        Optional<Report> report = this.reportRepository.findById(id);
        if (!report.isPresent()) {
            throw new RuntimeException("Report not found");
        }
        Report r = report.get();

        Optional<SiteUser> currentUserOpt = userRepository.findByUserId(username);
        if (!currentUserOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        SiteUser currentUser = currentUserOpt.get();

        // 비밀글인 경우, 현재 사용자가 글 작성자이거나 관리자인지 확인
        if (r.isSecret() && !r.getUser().getUserId().equals(username) && !currentUser.isCheckedAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        return r;
    }

    public void create(String title, String content, SiteUser user, boolean isSecret) {
        Report report = Report.builder()
                .title(title)
                .content(content)
                .user(user)
                .isSecret(isSecret)  // 비밀글 설정
                .build();
        this.reportRepository.save(report);
    }

    public void modify(Report Report, String title, String content) {
        Report modifiyReport = Report.toBuilder()
                .title(title)
                .content(content)
                .build();

        this.reportRepository.save(modifiyReport);
    }

    public void delete(Report report) {
        this.reportRepository.delete(report);
    }

    private Specification<Report> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Report> reportRoot, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Report, SiteUser> userJoin = reportRoot.join("user", JoinType.LEFT);

                return cb.or(
                        cb.like(reportRoot.get("title"), "%" + kw + "%"), // 제목
                        cb.like(reportRoot.get("content"), "%" + kw + "%"), // 내용
                        cb.like(userJoin.get("nickname"), "%" + kw + "%") // 질문 작성자
                );
            }
        };
    }
}