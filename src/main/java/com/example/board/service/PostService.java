package com.example.board.service;

import com.example.board.model.Post;
import com.example.board.model.PostPatchRequestBody;
import com.example.board.model.PostPostRequestBody;
import com.example.board.model.entity.PostEntity;
import com.example.board.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    private static final List<Post> posts = new ArrayList<>();


    public List<Post> getPosts() {
        var postEntities = postRepository.findAll();
        return postEntities.stream().map(Post::from).toList();

    }

    public Post getPostByPostId(Long postId){
        var postEntity = postRepository.findById(postId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post Not Found")
                );
        return Post.from(postEntity);
    }

    public Post createPost(PostPostRequestBody postPostRequestBody) {
        var postEntity = new PostEntity();
        postEntity.setBody(postPostRequestBody.body());
        var savedPostEntity = postRepository.save(postEntity);

        return Post.from(savedPostEntity);
    }

    public Post updatePost(Long postId, PostPatchRequestBody postPatchRequestBody) {

        var postEntity = postRepository.findById(postId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post Not Found")
                );

        postEntity.setBody(postPatchRequestBody.body());
        var updatedPostEntity = postRepository.save(postEntity);
        return Post.from(updatedPostEntity);
    }

    public void deletePost(Long postId) {
        var postEntity = postRepository.findById(postId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post Not Found")
                );

        postRepository.delete(postEntity);
    }
}
