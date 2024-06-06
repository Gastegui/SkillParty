package com.example.securingweb.DTO;

public class DeleteRatingNotification {
    private Long ratingId;

    public DeleteRatingNotification(Long ratingId) {
        this.ratingId = ratingId;
    }

    public Long getRatingId() {
        return ratingId;
    }

    public void setRatingId(Long ratingId) {
        this.ratingId = ratingId;
    }
}