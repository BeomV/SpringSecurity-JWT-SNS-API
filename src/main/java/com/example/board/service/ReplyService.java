package com.example.board.service;

import com.example.board.exception.post.PostNotFoundException;
import com.example.board.exception.reply.ReplyNotFoundException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.PostEntity;
import com.example.board.model.entity.ReplyEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.post.PostPatchRequestBody;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.model.reply.Reply;
import com.example.board.model.reply.ReplyPatchRequestBody;
import com.example.board.model.reply.ReplyPostRequestBody;
import com.example.board.repository.PostRepository;
import com.example.board.repository.ReplyRepository;
import com.example.board.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReplyService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;

    public ReplyService(PostRepository postRepository, UserRepository userRepository, ReplyRepository replyRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.replyRepository = replyRepository;
    }


    public List<Reply> getRepliesByPostId(Long postId){
        var postEntity = postRepository.findById(postId)
                .orElseThrow(
                        () -> new PostNotFoundException(postId)
                );
        var replyEntities = replyRepository.findByPost(postEntity);
        return replyEntities.stream().map(Reply::from).toList();
    }

    @Transactional
    public Reply createReply(Long postId, ReplyPostRequestBody replyPostRequestBody, UserEntity currentUser) {
        var postEntity = postRepository.findById(postId)
                .orElseThrow(
                        () -> new PostNotFoundException(postId)
                );
        var replyEntity = replyRepository.save(ReplyEntity.of(replyPostRequestBody.body(), currentUser, postEntity));

        postEntity.setRepliesCount(postEntity.getRepliesCount() + 1);

        return Reply.from(replyEntity);
    }


    public Reply updateReply(Long postId, Long replyId, ReplyPatchRequestBody replyPatchRequestBody, UserEntity currentUser) {
        postRepository.findById(postId)
                .orElseThrow(
                        () -> new PostNotFoundException(postId)
                );

        var replyEntity = replyRepository.findById(replyId).orElseThrow(()->new ReplyNotFoundException(replyId));

        if(!replyEntity.getUser().equals(currentUser)){
            throw new UserNotAllowedException();
        }

        replyEntity.setBody(replyPatchRequestBody.body());
        return Reply.from(replyRepository.save(replyEntity));



    }

    @Transactional
    public void deleteReply(Long postId, Long replyId, UserEntity currentUser) {
        var postEntity = postRepository.findById(postId)
                .orElseThrow(
                        () -> new PostNotFoundException(postId)
                );

        var replyEntity = replyRepository.findById(replyId).orElseThrow(()->new ReplyNotFoundException(replyId));

        if(!replyEntity.getUser().equals(currentUser)){
            throw new UserNotAllowedException();
        }

        replyRepository.delete(replyEntity);

        postEntity.setRepliesCount(Math.max(0, postEntity.getRepliesCount() - 1));
        postRepository.save(postEntity);
    }





}
