package cz.cvut.kbss.ear.copyto.enums;

public enum Role {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    GUEST("ROLE_GUEST"),
    CLIENT("ROLE_CLIENT"),
    COPYWRITER("ROLE_COPYWRITER");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
