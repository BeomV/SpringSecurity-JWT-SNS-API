package com.example.board.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "\"like\"",
        indexes = { @Index(name= "like_userId_postId_idx", columnList = "userId, postId", unique = true)})
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @Column
    private ZonedDateTime createdDataTime;

    // TODO: UserEntity userId 가져오기

    @ManyToOne
    @JoinColumn(name ="userId")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name ="postId")
    private PostEntity post;

    public static LikeEntity of(UserEntity user, PostEntity post){
        var like = new LikeEntity();
        like.setUser(user);
        like.setPost(post);
        return like;
    }

    public Long getLikeId() {
        return likeId;
    }

    public void setLikeId(Long likeId) {
        this.likeId = likeId;
    }

    public ZonedDateTime getCreatedDataTime() {
        return createdDataTime;
    }

    public void setCreatedDataTime(ZonedDateTime createdDataTime) {
        this.createdDataTime = createdDataTime;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public PostEntity getPost() {
        return post;
    }

    public void setPost(PostEntity post) {
        this.post = post;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeEntity that = (LikeEntity) o;
        return Objects.equals(likeId, that.likeId) && Objects.equals(createdDataTime, that.createdDataTime) && Objects.equals(user, that.user) && Objects.equals(post, that.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(likeId, createdDataTime, user, post);
    }

    @PrePersist
    private void prePersist(){
        this.createdDataTime = ZonedDateTime.now();
    }

}
