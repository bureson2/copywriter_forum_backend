package cz.cvut.kbss.ear.copyto.security;

import cz.cvut.kbss.ear.copyto.model.users.User;
import cz.cvut.kbss.ear.copyto.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.copyto.security.model.UserDetails;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;


public class SecurityUtils {

    /**
     * Gets the currently authenticated user.
     *
     * @return Current user
     */
    public static User getCurrentUser() {
        final SecurityContext context = SecurityContextHolder.getContext();
        assert context != null;
        final UserDetails userDetails = (UserDetails) context.getAuthentication().getDetails();
        return userDetails.getUser();
    }

    /**
     * Gets details of the currently authenticated user.
     *
     * @return Currently authenticated user details or null, if no one is currently authenticated
     */
    public static UserDetails getCurrentUserDetails() {
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null && context.getAuthentication().getDetails() instanceof UserDetails) {
            return (UserDetails) context.getAuthentication().getDetails();
        } else {
            return null;
        }
    }

    /**
     * Creates an authentication token based on the specified user details and sets it to the current thread's security
     * context.
     *
     * @param userDetails Details of the user to set as current
     * @return The generated authentication token
     */
    public static AuthenticationToken setCurrentUser(UserDetails userDetails) {
        final AuthenticationToken token = new AuthenticationToken(userDetails.getAuthorities(), userDetails);
        token.setAuthenticated(true);

        final SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(token);
        SecurityContextHolder.setContext(context);
        return token;
    }

    /**
     * Checks whether the current authentication token represents an anonymous user.
     *
     * @return Whether current authentication is anonymous
     */
    public static boolean isAuthenticatedAnonymously() {
        return getCurrentUserDetails() == null;
    }
}
