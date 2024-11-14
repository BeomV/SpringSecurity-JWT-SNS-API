package com.example.board.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "reply",
        indexes = { @Index(name= "reply_userId_idx", columnList = "userId"),
                @Index(name= "reply_postId_idx", columnList = "replyId")})
@SQLDelete(sql = "UPDATE \"reply\" SET deleteddatatime = CURRENT_TIMESTAMP WHERE replyId =?")
@SQLRestriction("deleteddatatime IS NULL")
public class ReplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column
    private ZonedDateTime createdDataTime;

    @Column
    private ZonedDateTime updatedDataTime;

    @Column
    private ZonedDateTime deletedDataTime;

    // TODO: UserEntity userId 가져오기

    @ManyToOne
    @JoinColumn(name ="userId")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name ="postId")
    private PostEntity post;

    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
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
        ReplyEntity that = (ReplyEntity) o;
        return Objects.equals(replyId, that.replyId) && Objects.equals(body, that.body) && Objects.equals(createdDataTime, that.createdDataTime) && Objects.equals(updatedDataTime, that.updatedDataTime) && Objects.equals(deletedDataTime, that.deletedDataTime) && Objects.equals(user, that.user) && Objects.equals(post, that.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(replyId, body, createdDataTime, updatedDataTime, deletedDataTime, user, post);
    }

    public static ReplyEntity of(String body, UserEntity user, PostEntity post){
        var reply = new ReplyEntity();
        reply.setBody(body);
        reply.setUser(user);
        reply.setPost(post);
        return reply;
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
