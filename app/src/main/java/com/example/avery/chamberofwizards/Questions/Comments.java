package com.example.avery.chamberofwizards.Questions;

public class Comments {
    String answer_id, comment, comment_by, commenter_image, commenter_name, question_id, comment_image;

    public Comments() {

    }

    public Comments(String answer_id, String comment, String comment_by, String commenter_image, String commenter_name, String question_id, String comment_image) {
        this.answer_id = answer_id;
        this.comment = comment;
        this.comment_by = comment_by;
        this.commenter_image = commenter_image;
        this.commenter_name = commenter_name;
        this.question_id = question_id;
        this.comment_image = comment_image;
    }

    public String getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(String answer_id) {
        this.answer_id = answer_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment_by() {
        return comment_by;
    }

    public void setComment_by(String comment_by) {
        this.comment_by = comment_by;
    }

    public String getCommenter_image() {
        return commenter_image;
    }

    public void setCommenter_image(String commenter_image) {
        this.commenter_image = commenter_image;
    }

    public String getCommenter_name() {
        return commenter_name;
    }

    public void setCommenter_name(String commenter_name) {
        this.commenter_name = commenter_name;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getComment_image() {
        return comment_image;
    }

    public void setComment_image(String comment_image) {
        this.comment_image = comment_image;
    }
}
