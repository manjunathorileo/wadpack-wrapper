package com.orileo.attendance.wrapper.service;

import com.orileo.attendance.wrapper.entity.mysql.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    List<Employee> getEmployees();
    Optional<Employee> getEmployee(long id);
    Employee getByRefId(String referenceId);
}