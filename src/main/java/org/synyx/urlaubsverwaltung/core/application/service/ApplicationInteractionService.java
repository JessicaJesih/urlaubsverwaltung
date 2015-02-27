package org.synyx.urlaubsverwaltung.core.application.service;

import org.synyx.urlaubsverwaltung.core.application.domain.Application;
import org.synyx.urlaubsverwaltung.core.application.domain.Comment;
import org.synyx.urlaubsverwaltung.core.person.Person;


/**
 * Provides interactions with applications for leave, i.e. apply for leave, allow, cancel etc.
 *
 * @author  Aljona Murygina - murygina@synyx.de
 */
public interface ApplicationInteractionService {

    /**
     * Sets the application's state to waiting and calculates the number of used vacation days. The bosses gets an email
     * that a new application for leave has been created and the person of the application for leave gets an email as
     * confirmation.
     *
     * @param  application  for leave
     * @param  applier  of the application
     *
     * @return  the saved application for leave
     */
    Application apply(Application application, Person applier);


    /**
     * Sets the application's state to allowed (only by boss) and informs the person of the application for leave that
     * his vacation has been allowed.
     *
     * @param  application  for leave
     * @param  boss  that allowed the application for leave
     * @param  comment  giving further information to allowing of application for leave
     *
     * @return  the allowed application for leave
     */
    Application allow(Application application, Person boss, Comment comment);


    /**
     * Sets the application's state to rejected (only by boss) and informs the person of the application for leave that
     * his vacation has been rejected.
     *
     * @param  application  for leave
     * @param  boss  that rejected the application for leave
     * @param  comment  giving further information to rejecting of application for leave
     *
     * @return  the rejected application for leave
     */
    Application reject(Application application, Person boss, Comment comment);


    /**
     * Sets the application's state to cancelled.
     *
     * @param  application  for leave
     * @param  canceller  executes the application's cancellation
     * @param  comment  giving further information to cancellation of application for leave
     *
     * @return  the cancelled application for leave
     */
    Application cancel(Application application, Person canceller, Comment comment);
}
