package com.codeahoy.alluvium.server.user;

/**
 * Class for representing user identity. Encapsulate logic to validate identities.
 *
 * Optimization Note: For optimization and reduce GC, this class can be removed in favor of {@link String} which uses a
 * pool instead of creating a new object each time.
 *
 * Created by umermansoor on 2017-03-05.
 */
public class UserId {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserId userId = (UserId) o;

        return id.equals(userId.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    private final String id;

    /**
     * Private constructor.
     * @param s
     */
    private UserId(String s) {
        this.id = s;
    }

    /**
     * Static factory method. Returns a new instance of user identity.
     * @param id
     * @return
     * @throw {@link IllegalArgumentException} is id is null or empty.
     */
    public static UserId newInstance(String id) {
        // Check to ensure id is valid. We only care about it being non-null.
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("user identity should be at least one character or more");
        }

        return new UserId(id);
    }

    @Override
    public String toString() {
        return "UserId{" +
                "id='" + id + '\'' +
                '}';
    }
}
