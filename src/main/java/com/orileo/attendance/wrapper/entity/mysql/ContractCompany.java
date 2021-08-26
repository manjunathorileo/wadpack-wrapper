package com.orileo.attendance.wrapper.entity.mysql;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "contract_company")
public class ContractCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "location")
    private String Location;

    @Column(name = "address")
    private String Address;

    @Column(name = "phone_number")
    private long phoneNumber;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "contract_start_date")
    private Date contractStartDate;

    @Column(name = "contract_end_date")
    private Date contractEndDate;

    @Column(name = "work_order_number")
    private String workOrderNumber;

    @Column(name = "date_of_on_boarding")
    private Date dateOfOnBoarding;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_termination")
    private Date dateOfTermination;

    @Column(name = "payment_applicable")
    private boolean paymentApplicable;

    @Column(name = "stay_time")
    private long stayTime;

    @Column(name = "rate_per_hour")
    private long ratePerHour;

}
