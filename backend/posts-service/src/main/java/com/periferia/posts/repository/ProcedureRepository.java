package com.periferia.posts.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ProcedureRepository {

    private final EntityManager entityManager;

    public ProcedureRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public Long createPost(Long userId, String mensaje) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_create_post")
                .registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_new_post_id", Long.class, ParameterMode.INOUT);

        query.setParameter("p_user_id", userId);
        query.setParameter("p_mensaje", mensaje);
        query.setParameter("p_new_post_id", null);
        query.execute();

        return (Long) query.getOutputParameterValue("p_new_post_id");
    }

    @Transactional
    public Long toggleLike(Long userId, Long postId) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_toggle_like")
                .registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_post_id", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_total_likes", Long.class, ParameterMode.INOUT);

        query.setParameter("p_user_id", userId);
        query.setParameter("p_post_id", postId);
        query.setParameter("p_total_likes", null);
        query.execute();

        return (Long) query.getOutputParameterValue("p_total_likes");
    }
}
