package com.orileo.attendance.wrapper.entity.mysql;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

public class RealBioDto {
    public Date fromDate;
    public Date toDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="yyyy-MM-dd")
    public Date startDate;
}
