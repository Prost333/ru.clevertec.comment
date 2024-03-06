package ru.clevertec.comment.proxy;

import jakarta.persistence.EntityNotFoundException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import ru.clevertec.comment.cache.Cache;
import ru.clevertec.comment.cache.CacheFactory;
import ru.clevertec.comment.dao.CommentDao;
import ru.clevertec.comment.dto.CommentMapper;
import ru.clevertec.comment.dto.CommentReq;
import ru.clevertec.comment.dto.CommentResp;
import ru.clevertec.comment.entity.Comment;

import java.util.Optional;

@Aspect
@Component
@ConditionalOnBean(CacheFactory.class)
public class CacheComment {
    private final Cache<Object, Object> cache;

    private final CommentDao commentRepository;

    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    public CacheComment(CacheFactory factoryCache, CommentDao commentRepository) {
        this.cache = factoryCache.getCacheAlgorithm();
        this.commentRepository = commentRepository;
    }

    @Around("@annotation(ru.clevertec.comment.proxy.Cache) && execution(* ru.clevertec.comment.service.imp.CommentServiceImp.findById(..))")
    public Object cacheGet(ProceedingJoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();

        Long id = (Long) args[0];
        if (cache.get(id) != null) {
            return cache.get(id);
        }
        CommentResp result;
        try {
            result = (CommentResp) joinPoint.proceed();
        } catch (EntityNotFoundException exception) {
            throw exception;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        cache.put(id, result);
        return result;
    }

    @AfterReturning(pointcut = "@annotation(ru.clevertec.comment.proxy.Cache) && execution(* ru.clevertec.comment.service.imp.CommentServiceImp.save(..))", returning = "id")
    public void cacheCreate(Long id) {

        Optional<Comment> optionalComment = commentRepository.findById(id);
        optionalComment.ifPresent(comment -> cache.put(id, commentMapper.toResponse(comment)));
    }

    @AfterReturning(pointcut = "@annotation(ru.clevertec.comment.proxy.Cache) && execution(* ru.clevertec.comment.service.imp.CommentServiceImp.update(..)) && args(id, commentRequest)", argNames = "id, commentRequest")
    public void cacheUpdate(Long id, CommentReq commentRequest) throws EntityNotFoundException {

        Comment comment = commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment not found with id " + id));
        cache.put(id, commentMapper.toResponse(comment));
    }

    @AfterReturning(pointcut = "@annotation(ru.clevertec.comment.proxy.Cache) && execution(* ru.clevertec.comment.service.imp.CommentServiceImp.delete(..)) && args(id)", argNames = "id")
    public void cacheDelete(Long id) {

        cache.remove(id);
    }
}