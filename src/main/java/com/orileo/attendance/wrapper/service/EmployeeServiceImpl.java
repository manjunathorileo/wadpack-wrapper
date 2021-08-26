package com.orileo.attendance.wrapper.service;

import com.orileo.attendance.wrapper.entity.mysql.Employee;
import com.orileo.attendance.wrapper.repository.mysqlrepository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployee(long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Employee getByRefId(String referenceId) {
        Employee employee = employeeRepository.findByEmployeeCode(referenceId);
        return employee;
    }
}
