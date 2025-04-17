package com.example.springservice.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter

@Embeddable
public class FollowId implements Serializable {
    @Column(name = "follower_id")
    private Integer followerId;
    @Column(name = "following_id")
    private Integer followingId;

    // equals & hashCode ต้องมี
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowId that = (FollowId) o;
        return Objects.equals(followerId, that.followerId) &&
                Objects.equals(followerId, that.followingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, followingId);
    }
}
