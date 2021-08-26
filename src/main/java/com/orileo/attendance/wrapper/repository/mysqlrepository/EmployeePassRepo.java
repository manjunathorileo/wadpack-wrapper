package com.orileo.attendance.wrapper.repository.mysqlrepository;

import com.orileo.attendance.wrapper.entity.mysql.EmployeePass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@EnableJpaRepositories
@Transactional
public interface EmployeePassRepo extends JpaRepository<EmployeePass, Long> {

    EmployeePass findByEmpId(long empId);
}
