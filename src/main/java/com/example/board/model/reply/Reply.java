package com.example.board.model.reply;

import com.example.board.model.entity.ReplyEntity;
import com.example.board.model.post.Post;
import com.example.board.model.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Reply
        (
        Long replyId,
        String body,
        User user,
        Post post,
        ZonedDateTime createdDataTime,
        ZonedDateTime updatedDataTime,
        ZonedDateTime deletedDataTime
        )
{
    public static Reply from(ReplyEntity replyEntity){
        return new Reply(
                replyEntity.getReplyId(),
                replyEntity.getBody(),
                User.from(replyEntity.getUser()),
                Post.from(replyEntity.getPost()),
                replyEntity.getCreatedDataTime(),
                replyEntity.getUpdatedDataTime(),
                replyEntity.getDeletedDataTime()
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


