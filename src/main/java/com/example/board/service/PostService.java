package com.example.board.service;

import com.example.board.exception.post.PostNotFoundException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.LikeEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.post.PostPatchRequestBody;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.model.entity.PostEntity;
import com.example.board.repository.LikeRepository;
import com.example.board.repository.PostRepository;
import com.example.board.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    private static final List<Post> posts = new ArrayList<>();


    public List<Post> getPosts() {
        var postEntities = postRepository.findAll();
        return postEntities.stream().map(Post::from).toList();

    }

    public Post getPostByPostId(Long postId){
        var postEntity = postRepository.findById(postId)
                .orElseThrow(
                        () -> new PostNotFoundException(postId)
                );
        return Post.from(postEntity);
    }

    public Post createPost(PostPostRequestBody postPostRequestBody, UserEntity currentUser) {

        var postEntity = postRepository.save(
                PostEntity.of(postPostRequestBody.body(), currentUser)
        );

        return Post.from(postEntity);
    }

    public Post updatePost(Long postId, PostPatchRequestBody postPatchRequestBody, UserEntity currentUser) {

        var postEntity = postRepository.findById(postId)
                .orElseThrow(
                        () -> new PostNotFoundException(postId)
                );

        if(!postEntity.getUser().equals(currentUser)){
            throw new UserNotAllowedException();
        }

        postEntity.setBody(postPatchRequestBody.body());
        var updatedPostEntity = postRepository.save(postEntity);
        return Post.from(updatedPostEntity);
    }

    public void deletePost(Long postId,UserEntity currentUser) {
        var postEntity = postRepository.findById(postId)
                .orElseThrow(
                        () -> new PostNotFoundException(postId)
                );

        if(!postEntity.getUser().equals(currentUser)){
            throw new UserNotAllowedException();
        }

        postRepository.delete(postEntity);
    }

    public List<Post> getPostByUsername(String username) {
        var userEntity = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException(username)
        );

        var postEntities = postRepository.findByUser(userEntity);
        return postEntities.stream().map(Post::from).toList();
    }


    @Transactional
    public Post toggleLike(Long postId, UserEntity currentUser) {
        var postEntity = postRepository.findById(postId)
                .orElseThrow(
                        () -> new PostNotFoundException(postId)
                );

        var likeEntity = likeRepository.findByUserAndPost(currentUser, postEntity);
        if (likeEntity.isPresent()){
            likeRepository.delete(likeEntity.get());
            postEntity.setLikesCount(Math.max(0, postEntity.getLikesCount() -1));
        }else{
            likeRepository.save(LikeEntity.of(currentUser, postEntity));
            postEntity.setLikesCount(postEntity.getLikesCount() + 1);

        }

        return Post.from(postRepository.save(postEntity));
    }
}
