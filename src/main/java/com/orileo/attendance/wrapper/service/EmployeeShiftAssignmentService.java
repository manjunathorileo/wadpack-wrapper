package com.orileo.attendance.wrapper.service;


import com.orileo.attendance.wrapper.entity.mysql.EmployeeShiftAssignment;

import java.util.Date;
import java.util.Optional;

public interface EmployeeShiftAssignmentService {
    Optional<EmployeeShiftAssignment> getCurrentShiftByEmployeeId(long employeeId, Date todayDate);
}
