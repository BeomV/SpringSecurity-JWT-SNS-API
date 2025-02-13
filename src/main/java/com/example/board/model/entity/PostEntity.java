package com.example.board.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "post",
        indexes = { @Index(name= "post_userId_idx", columnList = "userId")})
@SQLDelete(sql = "UPDATE \"post\" SET deleteddatatime = CURRENT_TIMESTAMP WHERE postid =?")
@SQLRestriction("deleteddatatime IS NULL")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column
    private ZonedDateTime createdDataTime;

    @Column
    private ZonedDateTime updatedDataTime;

    @Column
    private ZonedDateTime deletedDataTime;

    @Column
    private Long repliesCount = 0L;

    @Column
    private Long likesCount = 0L;

    // TODO: UserEntity userId 가져오기

    @ManyToOne
    @JoinColumn(name ="userId")
    private UserEntity user;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ZonedDateTime getCreatedDataTime() {
        return createdDataTime;
    }

    public void setCreatedDataTime(ZonedDateTime createdDataTime) {
        this.createdDataTime = createdDataTime;
    }

    public ZonedDateTime getUpdatedDataTime() {
        return updatedDataTime;
    }

    public void setUpdatedDataTime(ZonedDateTime updatedDataTime) {
        this.updatedDataTime = updatedDataTime;
    }

    public ZonedDateTime getDeletedDataTime() {
        return deletedDataTime;
    }

    public void setDeletedDataTime(ZonedDateTime deletedDataTime) {
        this.deletedDataTime = deletedDataTime;
    }

    public Long getRepliesCount() {
        return repliesCount;
    }

    public void setRepliesCount(Long repliesCount) {
        this.repliesCount = repliesCount;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Long likesCount) {
        this.likesCount = likesCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostEntity that = (PostEntity) o;
        return Objects.equals(postId, that.postId) && Objects.equals(body, that.body) && Objects.equals(createdDataTime, that.createdDataTime) && Objects.equals(updatedDataTime, that.updatedDataTime) && Objects.equals(deletedDataTime, that.deletedDataTime) && Objects.equals(repliesCount, that.repliesCount) && Objects.equals(likesCount, that.likesCount) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, body, createdDataTime, updatedDataTime, deletedDataTime, repliesCount, likesCount, user);
    }

    public static PostEntity of(String body, UserEntity user){
        var post = new PostEntity();
        post.setBody(body);
        post.setUser(user);
        return post;
    }

    @PrePersist
    private void prePersist(){
        this.createdDataTime = ZonedDateTime.now();
        this.updatedDataTime = ZonedDateTime.now();
    }

    @PreUpdate
    private void preUpdate(){
        this.updatedDataTime = ZonedDateTime.now();
    }
}
