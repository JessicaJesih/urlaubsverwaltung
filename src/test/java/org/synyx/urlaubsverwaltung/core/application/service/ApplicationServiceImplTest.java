/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synyx.urlaubsverwaltung.core.application.service;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.synyx.urlaubsverwaltung.core.application.dao.ApplicationDAO;
import org.synyx.urlaubsverwaltung.core.application.domain.Application;
import org.synyx.urlaubsverwaltung.core.application.domain.ApplicationStatus;
import org.synyx.urlaubsverwaltung.core.application.domain.Comment;
import org.synyx.urlaubsverwaltung.core.application.domain.VacationType;
import org.synyx.urlaubsverwaltung.core.calendar.JollydayCalendar;
import org.synyx.urlaubsverwaltung.core.calendar.OwnCalendarService;
import org.synyx.urlaubsverwaltung.core.calendar.workingtime.WorkingTimeService;
import org.synyx.urlaubsverwaltung.core.mail.MailService;
import org.synyx.urlaubsverwaltung.core.person.Person;
import org.synyx.urlaubsverwaltung.security.CryptoUtil;

import java.security.NoSuchAlgorithmException;


/**
 * Unit test for serivce {@link ApplicationServiceImpl}.
 *
 * @author  Aljona Murygina - murygina@synyx.de
 */
public class ApplicationServiceImplTest {

    private ApplicationServiceImpl instance;
    private ApplicationDAO applicationDAO;
    private MailService mailService;
    private OwnCalendarService calendarService;
    private CommentService commentService;
    private Application application;
    private Person person;

    @Before
    public void setUp() {

        applicationDAO = Mockito.mock(ApplicationDAO.class);
        mailService = Mockito.mock(MailService.class);
        commentService = Mockito.mock(CommentService.class);

        WorkingTimeService workingTimeService = Mockito.mock(WorkingTimeService.class);
        calendarService = new OwnCalendarService(new JollydayCalendar(), workingTimeService);

        instance = new ApplicationServiceImpl(applicationDAO, mailService, calendarService, commentService);

        // touch person that is needed for tests
        person = new Person();
        person.setLastName("Testperson");

        // touch application that is needed for tests
        application = new Application();
        application.setPerson(person);
    }


    /**
     * Test of getApplicationById method, of class ApplicationServiceImpl.
     */
    @Test
    public void testGetApplicationById() {

        instance.getApplicationById(1234);
        Mockito.verify(applicationDAO).findOne(1234);
    }


    /**
     * Test of save method, of class ApplicationServiceImpl.
     */
    @Test
    public void testSave() {

        instance.save(application);
        Mockito.verify(applicationDAO).save(application);
    }


    /**
     * Test of allow method, of class ApplicationServiceImpl.
     */
    @Test
    public void testAllow() throws NoSuchAlgorithmException {

        // set private key for boss
        person.setPrivateKey(CryptoUtil.generateKeyPair().getPrivate().getEncoded());
        application.setApplicationDate(DateMidnight.now());
        application.setVacationType(VacationType.HOLIDAY);

        application.setStatus(ApplicationStatus.WAITING);

        Comment comment = new Comment();

        instance.allow(application, person, comment);

        Assert.assertEquals(ApplicationStatus.ALLOWED, application.getStatus());
        Assert.assertEquals(person, application.getBoss());

        Mockito.verify(commentService).saveComment(comment, person, application);

        Mockito.verify(mailService).sendAllowedNotification(application, comment);
    }


    @Test
    public void testAllowNoRep() throws NoSuchAlgorithmException {

        // set private key for boss
        person.setPrivateKey(CryptoUtil.generateKeyPair().getPrivate().getEncoded());
        application.setApplicationDate(DateMidnight.now());
        application.setVacationType(VacationType.HOLIDAY);
        application.setStatus(ApplicationStatus.WAITING);

        Comment comment = new Comment();

        instance.allow(application, person, comment);

        Mockito.verify(mailService, Mockito.never()).notifyRepresentative(application);
    }


    @Test
    public void testAllowWithRep() throws NoSuchAlgorithmException {

        // set private key for boss
        person.setPrivateKey(CryptoUtil.generateKeyPair().getPrivate().getEncoded());
        application.setApplicationDate(DateMidnight.now());
        application.setVacationType(VacationType.HOLIDAY);
        application.setStatus(ApplicationStatus.WAITING);
        application.setRep(new Person());

        Comment comment = new Comment();

        instance.allow(application, person, comment);

        Mockito.verify(mailService).notifyRepresentative(application);
    }


    /**
     * Test of reject method, of class ApplicationServiceImpl.
     */
    @Test
    public void testReject() {

        Person boss = new Person();

        DateMidnight startDate = new DateMidnight(2012, DateTimeConstants.DECEMBER, 21);
        DateMidnight endDate = new DateMidnight(2013, DateTimeConstants.JANUARY, 5);

        application.setStartDate(startDate);
        application.setEndDate(endDate);
        application.setStatus(ApplicationStatus.WAITING);

        instance.reject(application, boss);

        Assert.assertEquals(ApplicationStatus.REJECTED, application.getStatus());

        Assert.assertNotNull(application.getBoss());
        Assert.assertEquals(boss, application.getBoss());
    }


    /**
     * Test of cancel method, of class ApplicationServiceImpl.
     */
    @Test
    public void testCancel() {

        DateMidnight startDate = new DateMidnight(2012, DateTimeConstants.DECEMBER, 21);
        DateMidnight endDate = new DateMidnight(2013, DateTimeConstants.JANUARY, 5);

        application.setStatus(ApplicationStatus.WAITING);
        application.setStartDate(startDate);
        application.setEndDate(endDate);

        instance.cancel(application);

        Assert.assertEquals(ApplicationStatus.CANCELLED, application.getStatus());
    }


    /**
     * Test of signApplicationByUser method, of class ApplicationServiceImpl.
     */
    @Test
    public void testSignApplicationByUser() throws Exception {

        // person needs some info: private key, last name
        person.setPrivateKey(CryptoUtil.generateKeyPair().getPrivate().getEncoded());

        // application needs data
        application.setPerson(person);
        application.setVacationType(VacationType.SPECIALLEAVE);
        application.setApplicationDate(new DateMidnight(2011, 11, 1));

        // execute method
        instance.signApplicationByUser(application, person);

        // signature of person should be filled, signature of boss not
        Assert.assertNotNull(application.getSignaturePerson());
        Assert.assertEquals(null, application.getSignatureBoss());
    }


    /**
     * Test of signApplicationByBoss method, of class ApplicationServiceImpl.
     */
    @Test
    public void testSignApplicationByBoss() throws Exception {

        // person needs some info: private key, last name
        person.setPrivateKey(CryptoUtil.generateKeyPair().getPrivate().getEncoded());

        // application needs data
        application.setPerson(person);
        application.setVacationType(VacationType.HOLIDAY);
        application.setApplicationDate(new DateMidnight(2011, 12, 21));

        // execute method
        instance.signApplicationByBoss(application, person);

        // signature of boss should be filled, signature of person not
        Assert.assertNotNull(application.getSignatureBoss());
        Assert.assertEquals(null, application.getSignaturePerson());
    }
}