package org.synyx.urlaubsverwaltung.calendar.workingtime;

import org.apache.commons.collections.CollectionUtils;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import org.springframework.data.jpa.domain.AbstractPersistable;

import org.synyx.urlaubsverwaltung.application.domain.DayLength;
import org.synyx.urlaubsverwaltung.calendar.Day;
import org.synyx.urlaubsverwaltung.person.Person;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;


/**
 * Entity representing the working time of a person.
 *
 * @author  Aljona Murygina - murygina@synyx.de
 */
@Entity
public class WorkingTime extends AbstractPersistable<Integer> {

    @OneToOne
    private Person person;

    @Enumerated(EnumType.STRING)
    private DayLength monday;

    @Enumerated(EnumType.STRING)
    private DayLength tuesday;

    @Enumerated(EnumType.STRING)
    private DayLength wednesday;

    @Enumerated(EnumType.STRING)
    private DayLength thursday;

    @Enumerated(EnumType.STRING)
    private DayLength friday;

    @Enumerated(EnumType.STRING)
    private DayLength saturday;

    @Enumerated(EnumType.STRING)
    private DayLength sunday;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date validFrom;

    public WorkingTime() {

        setAllDayLengthsToZero();
    }

    public void setWorkingDays(List<Integer> workingDays, DayLength dayLength) {

        setAllDayLengthsToZero();

        if (!CollectionUtils.isEmpty(workingDays)) {
            for (Integer dayOfWeek : workingDays) {
                setDayLengthForWeekDay(dayOfWeek, dayLength);
            }
        }
    }


    public boolean hasWorkingDays(List<Integer> workingDays) {

        for (Day day : Day.values()) {
            int dayOfWeek = day.getDayOfWeek();

            DayLength dayLength = getDayLengthForWeekDay(dayOfWeek);

            if (dayLength == DayLength.FULL) {
                // has to be in the given list
                if (!workingDays.contains(dayOfWeek)) {
                    return false;
                }
            } else {
                // must not be in the given list
                if (workingDays.contains(dayOfWeek)) {
                    return false;
                }
            }
        }

        return true;
    }


    private void setAllDayLengthsToZero() {

        this.monday = DayLength.ZERO;
        this.tuesday = DayLength.ZERO;
        this.wednesday = DayLength.ZERO;
        this.thursday = DayLength.ZERO;
        this.friday = DayLength.ZERO;
        this.saturday = DayLength.ZERO;
        this.sunday = DayLength.ZERO;
    }


    public void setPerson(Person person) {

        this.person = person;
    }


    public DayLength getDayLengthForWeekDay(int weekDay) {

        switch (weekDay) {
            case 1:
                return this.monday;

            case 2:
                return this.tuesday;

            case 3:
                return this.wednesday;

            case 4:
                return this.thursday;

            case 5:
                return this.friday;

            case 6:
                return this.saturday;

            case 7:
                return this.sunday;

            default:
                return null;
        }
    }


    public void setDayLengthForWeekDay(int weekDay, DayLength dayLength) {

        switch (weekDay) {
            case 1:
                this.monday = dayLength;
                break;

            case 2:
                this.tuesday = dayLength;
                break;

            case 3:
                this.wednesday = dayLength;
                break;

            case 4:
                this.thursday = dayLength;
                break;

            case 5:
                this.friday = dayLength;
                break;

            case 6:
                this.saturday = dayLength;
                break;

            case 7:
                this.sunday = dayLength;
                break;
        }
    }


    public DateMidnight getValidFrom() {

        if (this.validFrom == null) {
            return null;
        }

        return new DateTime(this.validFrom).toDateMidnight();
    }


    public void setValidFrom(DateMidnight validFrom) {

        if (validFrom == null) {
            this.validFrom = null;
        } else {
            this.validFrom = validFrom.toDate();
        }
    }
}
