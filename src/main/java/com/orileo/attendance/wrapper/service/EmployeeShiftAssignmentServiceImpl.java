package com.orileo.attendance.wrapper.service;

import com.orileo.attendance.wrapper.entity.mysql.EmployeeShiftAssignment;
import com.orileo.attendance.wrapper.repository.mysqlrepository.EmployeeShiftAssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class EmployeeShiftAssignmentServiceImpl implements EmployeeShiftAssignmentService {

    private final EmployeeShiftAssignmentRepository employeeShiftAssignmentRepository;

    public EmployeeShiftAssignmentServiceImpl(EmployeeShiftAssignmentRepository employeeShiftAssignmentRepository){
        this.employeeShiftAssignmentRepository = employeeShiftAssignmentRepository;
    }

    @Override
    public Optional<EmployeeShiftAssignment> getCurrentShiftByEmployeeId(long employeeId, Date todayDate) {
        return employeeShiftAssignmentRepository.getCurrentShiftByEmployeeId(employeeId,todayDate);
    }
}
