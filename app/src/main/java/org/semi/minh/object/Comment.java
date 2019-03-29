package org.semi.minh.object;

public class Comment {
    private String id;
    private String userName;
    private int userAvatar;
    private String userComment;
    private int rating;
    private String userCommentTime;

    public Comment() {
    }

    public Comment(String id, String userName, int userAvatar, String userComment, int rating, String userCommentTime) {
        this.id = id;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.userComment = userComment;
        this.rating = rating;
        this.userCommentTime = userCommentTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(int userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUserCommentTime() {
        return userCommentTime;
    }

    public void setUserCommentTime(String userCommentTime) {
        this.userCommentTime = userCommentTime;
    }
}
