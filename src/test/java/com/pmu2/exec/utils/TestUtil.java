package com.pmu2.exec.utils;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.infrastrure.db.sql.CourseEntity;
import com.pmu2.exec.infrastrure.db.sql.PartantEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class TestUtil {

    // Create a new CourseRecord E
    public static CourseRecord newCourseRecord(String name){
        // Parse date string to LocalDate object
        LocalDate dateParse = LocalDate.parse("2023-09-14");
        // create instance of Random class
        Random rand = new Random();
        // Generate random integers in range 0 to 999
        int randInt1 = rand.nextInt(1000);

        return new CourseRecord(randInt1, name, 9, dateParse, List.of());
    }

    public static CourseRecord newCourseRecordwithParant(String name){
        // Parse date string to LocalDate object
        LocalDate dateParse = LocalDate.parse("2023-09-14");
        // create instance of Random class
        Random rand = new Random();
        // Generate random integers in range 0 to 999
        int randInt1 = rand.nextInt(1000);

        return new CourseRecord(randInt1, name, 9, dateParse, getParticipantReccordListA());
    }

    public static  List<PartantEntity> getParticipantEntityListA() {
        PartantEntity p1 = new PartantEntity("Partant AA", 909);
        PartantEntity p2 = new PartantEntity("Partant AB", 809);

        return List.of(p1, p2);
    }

    public static  List<PartantRecord> getParticipantReccordListA() {
        PartantRecord p1 = new PartantRecord(1,"Partant AA", 909);
        PartantRecord p2 = new PartantRecord(9, "Partant AB", 809);

        return List.of(p1, p2);
    }

    public static CourseEntity newcourseEntity(String name) {
        CourseEntity courseEntity = new CourseEntity();
        courseEntity.setCourseId(1L);
        courseEntity.setName(name);
        courseEntity.setDate(LocalDate.now());
        courseEntity.setPartants(List.of());
        return courseEntity;
    }


}