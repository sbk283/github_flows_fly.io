package com.apartment.apart.domain.communityReply;

import com.apartment.apart.DataNotException;
import com.apartment.apart.domain.community.Community;
import com.apartment.apart.domain.user.SiteUser;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class CommunityReplyService {
    private final CommunityReplyRepository communityReplyRepository;

    @Transactional
    public CommunityReply create(Community community, String content, SiteUser user) {

        CommunityReply createCommunityReply = CommunityReply.builder()
                .content(content)
                .community(community)
                .user(user)
                .build();
        this.communityReplyRepository.save(createCommunityReply);
        return createCommunityReply;
    }

    public CommunityReply getCommunityReply(Long id) {
        return this.communityReplyRepository.findById(id)
                .orElseThrow(() -> new DataNotException("답변을 찾을 수 없습니다."));
    }

    public void modify(CommunityReply communityReply, String content) {
        CommunityReply modifyCommunityReply = communityReply.toBuilder()
                .content(content)
                .build();
        this.communityReplyRepository.save(modifyCommunityReply);
    }
    public void delete(CommunityReply communityReply) {
        this.communityReplyRepository.delete(communityReply);
    }
    public void like(CommunityReply communityReply, SiteUser siteUser) {
        communityReply.getLikeCount().add(siteUser);
        this.communityReplyRepository.save(communityReply);
    }

    public void cancelLike(CommunityReply communityReply, SiteUser siteUser) {
        if (communityReply.getLikeCount().contains(siteUser)) {
            communityReply.getLikeCount().remove(siteUser);
            this.communityReplyRepository.save(communityReply);
        } else {
            throw new IllegalStateException("이미 추천을 취소한 상태이거나, 추천을 하지 않은 경우입니다.");
        }
    }
}
