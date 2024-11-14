package com.example.board.controller;

import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.post.PostPatchRequestBody;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.model.reply.Reply;
import com.example.board.model.reply.ReplyPatchRequestBody;
import com.example.board.model.reply.ReplyPostRequestBody;
import com.example.board.service.PostService;
import com.example.board.service.ReplyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts/{postId}/replies")
public class ReplyController {

    private final ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    @GetMapping
    public ResponseEntity<List<Reply>> getRepliesByPostId(@PathVariable Long postId){
        var replies = replyService.getRepliesByPostId(postId);

        return ResponseEntity.ok(replies);
    }


    @PostMapping
    public ResponseEntity<Reply> createReply(
            @PathVariable Long postId
            , @RequestBody ReplyPostRequestBody replyPostRequestBody, Authentication authentication){
        var reply = replyService.createReply(postId, replyPostRequestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(reply);
    }

    @PatchMapping("/{replyId}")
    public ResponseEntity<Reply> updateReply(@PathVariable Long postId, @PathVariable Long replyId, @RequestBody ReplyPatchRequestBody replyPatchRequestBody, Authentication authentication){
        var reply = replyService.updateReply(postId, replyId, replyPatchRequestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(reply);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long postId, @PathVariable Long replyId, Authentication authentication){
        replyService.deleteReply(postId,replyId,(UserEntity) authentication.getPrincipal());
        return ResponseEntity.noContent().build();
    }


}
