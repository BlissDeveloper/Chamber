package com.example.avery.chamberofwizards.Questions;

public class Replies {
    String answer_id, comment_id, question_id, replier, replier_course, replier_image, replier_name, reply, reply_id, reply_image;

    public Replies() {

    }

    public Replies(String answer_id, String comment_id, String question_id, String replier, String replier_course, String replier_image, String replier_name, String reply, String reply_id, String reply_image) {
        this.answer_id = answer_id;
        this.comment_id = comment_id;
        this.question_id = question_id;
        this.replier = replier;
        this.replier_course = replier_course;
        this.replier_image = replier_image;
        this.replier_name = replier_name;
        this.reply = reply;
        this.reply_id = reply_id;
        this.reply_image = reply_image;
    }

    public String getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(String answer_id) {
        this.answer_id = answer_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getReplier() {
        return replier;
    }

    public void setReplier(String replier) {
        this.replier = replier;
    }

    public String getReplier_course() {
        return replier_course;
    }

    public void setReplier_course(String replier_course) {
        this.replier_course = replier_course;
    }

    public String getReplier_image() {
        return replier_image;
    }

    public void setReplier_image(String replier_image) {
        this.replier_image = replier_image;
    }

    public String getReplier_name() {
        return replier_name;
    }

    public void setReplier_name(String replier_name) {
        this.replier_name = replier_name;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }

    public String getReply_image() {
        return reply_image;
    }

    public void setReply_image(String reply_image) {
        this.reply_image = reply_image;
    }
}
