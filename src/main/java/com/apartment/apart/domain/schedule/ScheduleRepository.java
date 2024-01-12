package com.apartment.apart.domain.schedule;

import com.apartment.apart.domain.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    List<Schedule> findByUser(SiteUser siteUser);

    List<Schedule> findByTargetDong(int targetDong);

    @Query("SELECT s FROM Schedule s WHERE s.targetDong = :targetDong OR s.targetDong = 100")
    List<Schedule> findByTargetDongAndTotal(@Param("targetDong") int targetDong);
}
