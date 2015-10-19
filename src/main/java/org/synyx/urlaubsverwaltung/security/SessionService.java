package org.synyx.urlaubsverwaltung.security;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import org.synyx.urlaubsverwaltung.core.department.DepartmentService;
import org.synyx.urlaubsverwaltung.core.person.Person;
import org.synyx.urlaubsverwaltung.core.person.PersonService;
import org.synyx.urlaubsverwaltung.core.person.Role;

import java.util.Optional;


/**
 * @author  Aljona Murygina - murygina@synyx.de
 */
@Service
public class SessionService {

    private final PersonService personService;
    private final DepartmentService departmentService;

    @Autowired
    public SessionService(PersonService personService, DepartmentService departmentService) {

        this.personService = personService;
        this.departmentService = departmentService;
    }

    /**
     * This method allows to get the signed in user.
     *
     * @return  user that is signed in
     */
    public Person getSignedInUser() {

        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Person> person = personService.getPersonByLogin(user);

        if (!person.isPresent()) {
            throw new IllegalStateException("Can not get the person for the signed in user with username = " + user);
        }

        return person.get();
    }


    /**
     * Check if the given signed in user is allowed to access the data of the given person.
     *
     * @param  signedInUser  to check the permissions
     * @param  person  which data should be accessed
     *
     * @return  {@code true} if the given user may access the data of the given person, else {@code false}
     */
    public boolean isSignedInUserAllowedToAccessPersonData(Person signedInUser, Person person) {

        boolean isOwnData = person.getId().equals(signedInUser.getId());
        boolean isOffice = signedInUser.hasRole(Role.OFFICE);
        boolean isBoss = signedInUser.hasRole(Role.BOSS);
        boolean isDepartmentHeadOfPerson = departmentService.isDepartmentHeadOfPerson(signedInUser, person);

        return isOwnData || isOffice || isBoss || isDepartmentHeadOfPerson;
    }
}
