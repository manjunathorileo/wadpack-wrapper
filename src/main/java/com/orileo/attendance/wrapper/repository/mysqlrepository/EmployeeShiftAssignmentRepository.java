package com.orileo.attendance.wrapper.repository.mysqlrepository;

import com.orileo.attendance.wrapper.entity.mysql.EmployeeShiftAssignment;
import com.orileo.attendance.wrapper.entity.mysql.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@EnableJpaRepositories
@Transactional
public interface EmployeeShiftAssignmentRepository extends JpaRepository<EmployeeShiftAssignment, Long> {

    Optional<EmployeeShiftAssignment> findByShift(Shift shift);

    @Query("SELECT shift FROM EmployeeShiftAssignment shift WHERE (:todayDate BETWEEN shift.fromDate AND shift.toDate) AND shift.employee.id =:employeeId")
    Optional<EmployeeShiftAssignment> getCurrentShiftByEmployeeId(@Param("employeeId") long employeeId, @Param("todayDate") Date todayDate);
}
