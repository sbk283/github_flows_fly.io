package com.apartment.apart.domain.schedule;

import com.apartment.apart.domain.user.SiteUser;
import com.apartment.apart.global.jpa.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule extends BaseEntity {
    @ManyToOne
    private SiteUser user;

    private String title;

    private int targetDong;

    private LocalDate startDate;

    private LocalDate endDate;

}
