package cz.cvut.kbss.ear.copyto.util;

import cz.cvut.kbss.ear.copyto.enums.Role;

public final class Constants {

    /**
     * Default user role.
     */
    public static final Role DEFAULT_ROLE = Role.USER;

    private Constants() {
        throw new AssertionError();
    }
}
