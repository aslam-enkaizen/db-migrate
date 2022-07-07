package com.exrade.user;

import org.apache.commons.lang3.LocaleUtils;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author Rhidoy
 * @created 6/22/22
 */
public class AuthUser  implements AuthUserIdentity, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final long NO_EXPIRATION = -1L;

    public long expires() {
        return NO_EXPIRATION;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result
                + ((getProvider() == null) ? 0 : getProvider().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AuthUserIdentity other = (AuthUserIdentity) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        if (getProvider() == null) {
            if (other.getProvider() != null)
                return false;
        } else if (!getProvider().equals(other.getProvider()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return getId() + "@" + getProvider();
    }

    public static Locale getLocaleFromString(final String locale) {
        if (locale != null && !locale.isEmpty()) {
            try {
                return LocaleUtils.toLocale(locale);
            } catch (final IllegalArgumentException iae) {
                try {
                    return LocaleUtils.toLocale(locale.replace('-', '_'));
                } catch (final IllegalArgumentException iae2) {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public static <T extends AuthUserIdentity & NameIdentity> String toString(final T identity) {
        final StringBuilder sb = new StringBuilder();
        if (identity.getName() != null) {
            sb.append(identity.getName());
            sb.append(" ");
        }
        if(identity instanceof EmailIdentity) {
            final EmailIdentity i2 = (EmailIdentity) identity;
            if (i2.getEmail() != null) {
                sb.append("(");
                sb.append(i2.getEmail());
                sb.append(") ");
            }
        }
        if (sb.length() == 0) {
            sb.append(identity.getId());
        }
        sb.append(" @ ");
        sb.append(identity.getProvider());

        return sb.toString();
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getProvider() {
        return null;
    }
}
