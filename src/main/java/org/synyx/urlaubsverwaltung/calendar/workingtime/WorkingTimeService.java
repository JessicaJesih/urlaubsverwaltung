package org.synyx.urlaubsverwaltung.calendar.workingtime;

import org.joda.time.DateMidnight;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;

import org.synyx.urlaubsverwaltung.application.domain.DayLength;
import org.synyx.urlaubsverwaltung.person.Person;

import java.util.List;


/**
 * Service for handling {@link WorkingTime} entities.
 *
 * @author  Aljona Murygina - murygina@synyx.de
 */
@Transactional
public class WorkingTimeService {

    private WorkingTimeDAO workingTimeDAO;

    @Autowired
    public WorkingTimeService(WorkingTimeDAO workingTimeDAO) {

        this.workingTimeDAO = workingTimeDAO;
    }


    public WorkingTimeService() {
    }

    public void touch(List<Integer> workingDays, DateMidnight validFrom, Person person) {

        WorkingTime workingTime = workingTimeDAO.findByPersonAndValidityDate(person, validFrom.toDate());

        /*
         * create a new WorkingTime object if no one existent for the given person and date
         */
        if (workingTime == null) {
            workingTime = new WorkingTime();
            workingTime.setPerson(person);
            workingTime.setValidFrom(validFrom);
        }

        /**
         * else just change the working days of the current working time object
         */
        workingTime.setWorkingDays(workingDays, DayLength.FULL);

        workingTimeDAO.save(workingTime);
    }


    public List<WorkingTime> getByPerson(Person person) {

        return workingTimeDAO.findByPerson(person);
    }


    public WorkingTime getByPersonAndValidityDateEqualsOrMinorDate(Person person, DateMidnight date) {

        return workingTimeDAO.findByPersonAndValidityDateEqualsOrMinorDate(person, date.toDate());
    }


    public WorkingTime getCurrentOne(Person person) {

        return workingTimeDAO.findLastOneByPerson(person);
    }
}