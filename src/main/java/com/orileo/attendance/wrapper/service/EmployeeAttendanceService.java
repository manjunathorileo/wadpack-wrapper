package com.orileo.attendance.wrapper.service;

import com.orileo.attendance.wrapper.entity.mysql.AttendanceStatus;
import com.orileo.attendance.wrapper.entity.mysql.Employee;
import com.orileo.attendance.wrapper.entity.mysql.EmployeeAttendance;

import java.util.Date;
import java.util.List;

public interface EmployeeAttendanceService {

    EmployeeAttendance createEmployeeAttendance(EmployeeAttendance employeeAttendance);

    EmployeeAttendance getTodayMarkedEmployeeAttendance(Date todayDate, Employee employee/*, AttendanceStatus attendanceStatus*/);

    List<EmployeeAttendance> getAllEmployeeAttendance();
}
