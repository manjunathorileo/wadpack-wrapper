package com.orileo.attendance.wrapper.repository.mssqlrepository;

import com.orileo.attendance.wrapper.entity.mssql.RealBioAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface AttendanceMsSqlRepository extends JpaRepository<RealBioAttendance,Object> {

    @Query("SELECT rb from RealBioAttendance rb WHERE CONVERT(date, rb.transactionDate) = :logDate")
    List<RealBioAttendance> findByLogDate(@Param("logDate") Date logDate);

    @Query("SELECT rb from RealBioAttendance rb WHERE CONVERT(date, rb.transactionDate) = :logDate AND rb.userId = :userId ")
    List<RealBioAttendance> findByUserIdAndLogDate(@Param("userId") String userId,@Param("logDate") Date logDate);
}