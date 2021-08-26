package com.orileo.attendance.wrapper.repository.mysqlrepository;

import com.orileo.attendance.wrapper.entity.mysql.PermanentContractAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface PermanentContractAttendanceRepo extends JpaRepository<PermanentContractAttendance,Long> {

    @Query("SELECT p FROM PermanentContractAttendance p where p.empId = :employeeId and p.markedOn =:startDate")
    PermanentContractAttendance getEmployeeAttendanceByEmployeeId(@Param("startDate") Date startDate, @Param("employeeId") long employeeId);

    @Query("SELECT e FROM PermanentContractAttendance e where e.markedOn between :startDate and :endDate")
    List<PermanentContractAttendance> getEmployeeAttendanceBetweenDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT e FROM PermanentContractAttendance e where e.empId = :employeeId and e.markedOn between :startDate and :endDate ORDER BY e.markedOn ASC ")
    List<PermanentContractAttendance> getEmployeeAttendanceBetweenDateByEmployeeId(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("employeeId") Long employeeId);

    List<PermanentContractAttendance> findByMarkedOn(Date markedOn);
}
