package com.apartment.apart.domain.voteTotal;

import com.apartment.apart.domain.user.SiteUser;
import com.apartment.apart.domain.vote.Vote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class VoteTotalService {

    private final VoteTotalRepository voteTotalRepository;

    public void vote(SiteUser siteUser, Vote vote, Boolean voteValueBoolean) {
        if ((vote.getEndDate().isAfter(LocalDate.now()) || vote.getEndDate().isEqual(LocalDate.now()))
                && (vote.getStartDate().isBefore(LocalDate.now()) || vote.getStartDate().isEqual(LocalDate.now()))) {
            VoteTotal voteTotal = VoteTotal.builder()
                    .voter(siteUser)
                    .vote(vote)
                    .agree(voteValueBoolean)
                    .build();
            this.voteTotalRepository.save(voteTotal);
        }
    }
}
