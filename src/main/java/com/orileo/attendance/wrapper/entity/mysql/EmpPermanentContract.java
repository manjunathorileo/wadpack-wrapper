package com.orileo.attendance.wrapper.entity.mysql;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "emp_permanent_contract")
public class EmpPermanentContract {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "employee_code",length = 25)
    private String employeeCode;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "epf")
    private boolean epf;

    @Column(name = "date_of_joining")
    private Date dateOfJoining;

    @Column(name ="date_of_leaving")
    private Date dateOfLeaving;

    @Column(name = "job_title",length = 50)
    private String jobTitle;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "age",length = 4)
    private String age;

    @Column(name = "blood_group",length = 40)
    private String bloodGroup;

    @Column(name = "adhar_number",length = 50, unique = true)
    private String adharNumber;

    @Column(name = "phone_number",length = 15)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type")
    private EmployeeType employeeType;

    @Column(name = "contract_company")
    private String contractCompany;

    private boolean status;
}
