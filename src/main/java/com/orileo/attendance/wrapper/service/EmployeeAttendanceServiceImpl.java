package com.orileo.attendance.wrapper.service;


import com.orileo.attendance.wrapper.entity.mysql.AttendanceStatus;
import com.orileo.attendance.wrapper.entity.mysql.Employee;
import com.orileo.attendance.wrapper.entity.mysql.EmployeeAttendance;
import com.orileo.attendance.wrapper.repository.mysqlrepository.EmployeeAttendanceRepository;
import com.orileo.attendance.wrapper.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class EmployeeAttendanceServiceImpl implements EmployeeAttendanceService {

    @Autowired
    private EmployeeAttendanceRepository employeeAttendanceRepository;

    @Override
    public EmployeeAttendance createEmployeeAttendance(EmployeeAttendance employeeAttendance) {
        return employeeAttendanceRepository.save(employeeAttendance);
    }

    @Override
    public EmployeeAttendance getTodayMarkedEmployeeAttendance(Date todayDate, Employee employee/*, AttendanceStatus attendanceStatus*/) {
        List<EmployeeAttendance> employeeAttendances = employeeAttendanceRepository.findByMarkedOnAndEmployee(todayDate, employee/*,AttendanceStatus.PRESENT*/);
        Collections.reverse(employeeAttendances);
        if (employeeAttendances.isEmpty()) {
            return null;
        }

        if (employeeAttendances.size() > 1) {
            employeeAttendanceRepository.delete(employeeAttendances.get(0));
            return employeeAttendances.get(1);
        } else {
            return employeeAttendances.get(0);
        }
    }

    @Override
    public List<EmployeeAttendance> getAllEmployeeAttendance() {
        return employeeAttendanceRepository.findAll();
    }
}