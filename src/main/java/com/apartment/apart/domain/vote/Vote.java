package com.apartment.apart.domain.vote;


import com.apartment.apart.domain.user.SiteUser;
import com.apartment.apart.domain.voteTotal.VoteTotal;
import com.apartment.apart.global.jpa.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Vote extends BaseEntity {
    @ManyToOne
    private SiteUser user;

    private String title;

    private String content;

    private LocalDate startDate;

    private LocalDate endDate;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<VoteTotal> voteTotalList;

    public boolean isVoted(Long id) {
        boolean isChecked = false;
        for (VoteTotal item : voteTotalList) {
            if (Objects.equals(item.getVoter().getId(), id)) {
                isChecked = true;
            }
        }
        return isChecked;
    }
}