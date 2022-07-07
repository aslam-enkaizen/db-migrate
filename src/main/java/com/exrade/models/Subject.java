package com.exrade.models;

import java.util.List;

/**
 * @author Rhidoy
 * @created 6/21/22
 */
public interface Subject {
    /**
     * Get all {@link Role}s held by this subject.  Ordering is not important.
     *
     * @return a non-null list of roles
     */
    List<? extends Role> getRoles();

    /**
     * Get all {@link Permission}s held by this subject.  Ordering is not important.
     *
     * @return a non-null list of permissions
     */
    List<? extends Permission> getPermissions();

    /**
     * Gets a unique identifier for the subject, such as a user name.  This is never used by Deadbolt itself,
     * and is present to provide an easy way of getting a useful piece of user information in, for example,
     * dynamic checks without the need to cast the Subject.
     *
     * @return an identifier, such as a user name or UUID.  May be null.
     */
    String getIdentifier();
}
