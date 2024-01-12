package com.apartment.apart.domain.user;

import com.apartment.apart.domain.notice.Notice;
import com.apartment.apart.global.jpa.BaseEntity;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Delete;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String nickname, String password, String phone, String email, int apartDong, int apartHo, boolean checkedAdmin, boolean checkedWithdrawal) {
        SiteUser user = SiteUser.builder()
                .userId(username)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .phone(phone)
                .email(email)
                .apartDong(apartDong)
                .apartHo(apartHo)
                .checkedAdmin(false)
                .checkedWithdrawal(false)
                .build();
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUserId(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new RuntimeException("siteuser not found");
        }
    }

    @Transactional
    public SiteUser whenSocialLogin(String providerTypeCode, String username, String nickname) {
        Optional<SiteUser> opUser = userRepository.findByUserId(username);

        if (opUser.isPresent()) return opUser.get();

        return create(username, nickname, "", "", "", 0, 0,false,true); // 최초 로그인 시 딱 한번 실행
    }

    private Optional<SiteUser> findByUserId(String username) {
        return userRepository.findByUserId(username);
    }

    public List<SiteUser> getAllUsers() {
        return userRepository.findAll();
    }

    public void modify(SiteUser siteUser, String nickname, String password, String phone,
                       String email, int apartDong, int apartHo) {
        Optional<SiteUser> updateUser = this.userRepository.findByUserId(siteUser.getUserId());
        SiteUser modifyUser = SiteUser.builder()
                .id(updateUser.get().getId())
                .userId(siteUser.getUserId())
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .phone(phone)
                .email(email)
                .apartDong(apartDong)
                .apartHo(apartHo)
                .createDate(siteUser.getCreateDate())
                .checkedAdmin(siteUser.isCheckedAdmin())
                .checkedWithdrawal(siteUser.isCheckedWithdrawal())
                .build();
        this.userRepository.save(modifyUser);
    }

    public Page<SiteUser> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<SiteUser> spec = search(kw);
        return this.userRepository.findAll(spec, pageable);
    }

    private Specification<SiteUser> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<SiteUser> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                return cb.or(
                        cb.like(q.get("nickname"), "%" + kw + "%"),
                        cb.like(q.get("userId"), "%" + kw + "%")
                );
            };
        };
    }
    public void updateCheckedUserStatus(String userId) {
        Optional<SiteUser> optionalUser = userRepository.findByUserId(userId);
        SiteUser deleteuser = optionalUser.get();
        deleteuser.setPassword(null);
        deleteuser.setCheckedWithdrawal(true);
        this.userRepository.save(deleteuser);
    }


}