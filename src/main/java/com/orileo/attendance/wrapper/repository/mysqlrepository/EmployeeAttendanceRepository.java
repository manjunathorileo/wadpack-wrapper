package com.orileo.attendance.wrapper.repository.mysqlrepository;

import com.orileo.attendance.wrapper.entity.mysql.Employee;
import com.orileo.attendance.wrapper.entity.mysql.EmployeeAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EmployeeAttendanceRepository extends JpaRepository<EmployeeAttendance,Long> {

    List<EmployeeAttendance> findByMarkedOnAndEmployee(Date todayDate, Employee employee/*, AttendanceStatus attendanceStatus*/);

   }
