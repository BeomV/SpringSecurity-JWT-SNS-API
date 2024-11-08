package com.example.board.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "post")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostEntity that = (PostEntity) o;
        return Objects.equals(postId, that.postId) && Objects.equals(body, that.body) && Objects.equals(createdDataTime, that.createdDataTime) && Objects.equals(updatedDataTime, that.updatedDataTime) && Objects.equals(deletedDataTime, that.deletedDataTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, body, createdDataTime, updatedDataTime, deletedDataTime);
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
