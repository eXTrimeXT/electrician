package com.extrime.electrician.service;

import com.extrime.electrician.dao.ReviewDAO;
import com.extrime.electrician.model.Review;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewDAO reviewDAO;

    /**
     * Получение времени последнего отзыва
     */
    public LocalDateTime getLatestReviewTime() {
        return reviewDAO.findLatestReviewTime().orElse(null);
    }

    /**
     * Получение последнего отзыва
     */
    public Review getLatestReview() {
        return reviewDAO.findLatestReview().orElse(null);
    }
}