package edu.sru.schedule.scheduleadvisement.excelreader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;

import edu.sru.schedule.scheduleadvisement.domain.Timeslot;

// FAILS -- still working on 

public class TimeServiceTest {

    private TimeService timeService;

    @BeforeEach
    public void setUp() {
        timeService = new TimeService();
    }

    @Test
    public void testGetTimeslotByAttribute() {

        Date date = new Date();
        String startTime = "1:00";
        String endTime = "3:00";
        int length = 15;
        Timeslot timeslot = new Timeslot(date, startTime, endTime, length);
        timeService.addTimeslot(timeslot);


        Timeslot result = timeService.getTimeslotByAttribute(date, startTime, endTime, length);

        assertNotNull(result);
        assertEquals(timeslot, result);
    }

    @Test
    public void testAddTimeslot() {
        Timeslot timeslot = new Timeslot(new Date(), "1:00", "3:00", 60);
        
        timeService.addTimeslot(timeslot);

        assertTrue(timeService.getTimeslotList().contains(timeslot));
    }

    @Test
    public void testDeleteTimeslotById() {

        Timeslot timeslot = new Timeslot(new Date(), "1:00", "3:00", 60);
        timeService.addTimeslot(timeslot);

        timeService.deleteTimeslotById(timeslot.getId());

        assertNull(timeService.getById(timeslot.getId()));
    }

    @Test
    public void testUpdateTimeslot() {

        Date date = new Date();
        String startTime = "1:00";
        String endTime = "3:00";
        int length = 15;
        Timeslot timeslot = new Timeslot(date, startTime, endTime, length);
        timeService.addTimeslot(timeslot);

        String newStartTime = "2:00";
        String newEndTime = "5:00";
        int newLength = 15;
        timeService.updateTimeslot(timeslot.getId(), date, newStartTime, newEndTime, newLength);


        Timeslot updatedTimeslot = timeService.getById(timeslot.getId());

        assertNotNull(updatedTimeslot);
        assertEquals(newStartTime, updatedTimeslot.getStartTime());
        assertEquals(newEndTime, updatedTimeslot.getEndTime());
        assertEquals(newLength, updatedTimeslot.getLength());
    }
}
