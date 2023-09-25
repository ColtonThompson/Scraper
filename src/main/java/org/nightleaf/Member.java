package org.nightleaf;

public class Member {

    private final String name;

    private final String role;

    private final String joinDate;

    public Member(String name, String role, String joinDate) {
        this.name = name;
        this.role = role;
        this.joinDate = joinDate;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getJoinDate() {
        return joinDate;
    }
}
