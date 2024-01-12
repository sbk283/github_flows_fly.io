package com.apartment.apart.domain.notice;


import com.apartment.apart.domain.user.SiteUser;
import com.apartment.apart.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cache;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Notice extends BaseEntity {
    @ManyToOne
    private SiteUser user;

    @Column(columnDefinition = "TEXT")
    private String title;

    private String content;
}