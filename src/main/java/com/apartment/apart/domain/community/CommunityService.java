package com.apartment.apart.domain.community;

import com.apartment.apart.domain.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final CommunityRepository communityRepository;

    @Value("${custom.fileDirPath}")
    private String fileDirPath;

    public Page<Community> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Community> spec = search(kw);
        return this.communityRepository.findAll(spec, pageable);
    }

    public Community getCommunity(Long id) {
        Optional<Community> community = this.communityRepository.findById(id);
        if (community.isPresent()) {
            return community.get();
        } else {
            throw new RuntimeException("error");
        }
    }

    public void create(String title, String content, SiteUser user, MultipartFile thumbnail) {
        String thumbnailRelPath = "community/" + UUID.randomUUID().toString() + ".jpg";
        File thumbnailFile = new File(fileDirPath + "/" + thumbnailRelPath);

        try {
            thumbnail.transferTo(thumbnailFile);
        } catch (IOException e) {
            throw  new RuntimeException(e);
        }

        Community community = Community.builder()
                .title(title)
                .content(content)
                .user(user)
                .thumbnailImg(thumbnailRelPath)
                .build();
        this.communityRepository.save(community);
    }

    public void modify(Community community, String title, String content) {
        Community modifiyCommunity = community.toBuilder()
                .title(title)
                .content(content)
                .build();

        this.communityRepository.save(modifiyCommunity);
    }

    public void delete(Community community) {
        this.communityRepository.delete(community);
    }

    private Specification<Community> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Community> communityRoot, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Community, SiteUser> userJoin = communityRoot.join("user", JoinType.LEFT);

                return cb.or(
                        cb.like(communityRoot.get("title"), "%" + kw + "%"), // 제목
                        cb.like(communityRoot.get("content"), "%" + kw + "%"), // 내용
                        cb.like(userJoin.get("nickname"), "%" + kw + "%") // 질문 작성자
                );
            }
        };
    }

    public void like(Community community, SiteUser siteUser) {
        community.getLikeCount().add(siteUser);
        this.communityRepository.save(community);
    }

    public void cancelLike(Community community, SiteUser siteUser) {
        if (community.getLikeCount().contains(siteUser)) {
            community.getLikeCount().remove(siteUser);
            this.communityRepository.save(community);
        } else {
            throw new IllegalStateException("이미 추천을 취소한 상태이거나, 추천을 하지 않은 경우입니다.");
        }
    }
}