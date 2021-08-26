package com.orileo.attendance.wrapper.service;

import com.orileo.attendance.wrapper.entity.mssql.RealBioAttendance;

import java.util.Date;
import java.util.List;

public interface RealBioService {
    List<RealBioAttendance> getPunchedAttendance();

    List<RealBioAttendance> getPunchedAttendance(Date loggedDate);

    List<RealBioAttendance> getPunchedAttendanceByUserId(String userId,Date loggedDate);

}