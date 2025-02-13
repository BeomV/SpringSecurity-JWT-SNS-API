package com.example.board.model.post;

import com.example.board.model.entity.PostEntity;
import com.example.board.model.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Post
        (
        Long postId,
        String body,
        Long repliesCount,
        Long likesCount,
        User user,
        ZonedDateTime createdDataTime,
        ZonedDateTime updatedDataTime,
        ZonedDateTime deletedDataTime

        )
{


    public static Post from(PostEntity postEntity){
        return new Post(
                postEntity.getPostId(),
                postEntity.getBody(),
                postEntity.getRepliesCount(),
                postEntity.getLikesCount(),
                User.from(postEntity.getUser()),
                postEntity.getCreatedDataTime(),
                postEntity.getUpdatedDataTime(),
                postEntity.getDeletedDataTime()
        );
    }
}

/*public class Post {

    private Long replyId;

    private String body;

    private ZonedDateTime createdDateTime;

    public Post(Long replyId, String body, ZonedDateTime createdDateTime) {
        this.replyId = replyId;
        this.body = body;
        this.createdDateTime = createdDateTime;
    }

    public Long getPostId() {
        return replyId;
    }

    public void setPostId(Long replyId) {
        this.replyId = replyId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(replyId, post.replyId) && Objects.equals(body, post.body) && Objects.equals(createdDateTime, post.createdDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(replyId, body, createdDateTime);
    }

    @Override
    public String toString() {
        return "Post{" +
                "replyId=" + replyId +
                ", body='" + body + '\'' +
                ", createdDateTime=" + createdDateTime +
                '}';
    }
}*/


