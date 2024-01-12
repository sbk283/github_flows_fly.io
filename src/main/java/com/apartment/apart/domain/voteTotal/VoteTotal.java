package com.apartment.apart.domain.voteTotal;

import com.apartment.apart.domain.user.SiteUser;
import com.apartment.apart.domain.vote.Vote;
import com.apartment.apart.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class VoteTotal extends BaseEntity {

    @ManyToOne
    private SiteUser voter;
    @ManyToOne
    private Vote vote;

    private Boolean agree;


}
