package com.example.board.model.entity;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "\"follow\"",
        indexes = { @Index(name= "follow_follower_following_idx", columnList = "follower, following")})
public class FollowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followId;

    @Column
    private ZonedDateTime createdDataTime;

    // TODO: UserEntity userId 가져오기

    @ManyToOne
    @JoinColumn(name ="follower")
    private UserEntity follower;

    @ManyToOne
    @JoinColumn(name ="following")
    private UserEntity following;

    public static FollowEntity of(UserEntity follower, UserEntity following){
        var follow = new FollowEntity();
        follow.setFollower(follower);
        follow.setFollowing(following);
        return follow;
    }

    public Long getFollowId() {
        return followId;
    }

    public void setFollowId(Long followId) {
        this.followId = followId;
    }

    public ZonedDateTime getCreatedDataTime() {
        return createdDataTime;
    }

    public void setCreatedDataTime(ZonedDateTime createdDataTime) {
        this.createdDataTime = createdDataTime;
    }

    public UserEntity getFollower() {
        return follower;
    }

    public void setFollower(UserEntity follower) {
        this.follower = follower;
    }

    public UserEntity getFollowing() {
        return following;
    }

    public void setFollowing(UserEntity following) {
        this.following = following;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowEntity that = (FollowEntity) o;
        return Objects.equals(followId, that.followId) && Objects.equals(createdDataTime, that.createdDataTime) && Objects.equals(follower, that.follower) && Objects.equals(following, that.following);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followId, createdDataTime, follower, following);
    }

    @PrePersist
    private void prePersist(){
        this.createdDataTime = ZonedDateTime.now();
    }

}
