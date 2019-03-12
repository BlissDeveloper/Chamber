package com.example.avery.chamberofwizards.Questions;

public class PostedQuestions {
    String asker, course, date, question, questions_image, time, asker_image, asker_id;
    int category_code, number_of_answers;

    public PostedQuestions() {

    }

    public PostedQuestions(String asker, String course, String date, String question, String questions_image, String time, String asker_image, String asker_id, int category_code, int number_of_answers) {
        this.asker = asker;
        this.course = course;
        this.date = date;
        this.question = question;
        this.questions_image = questions_image;
        this.time = time;
        this.asker_image = asker_image;
        this.asker_id = asker_id;
        this.category_code = category_code;
        this.number_of_answers = number_of_answers;
    }

    public String getAsker() {
        return asker;
    }

    public void setAsker(String asker) {
        this.asker = asker;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestions_image() {
        return questions_image;
    }

    public void setQuestions_image(String questions_image) {
        this.questions_image = questions_image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAsker_image() {
        return asker_image;
    }

    public void setAsker_image(String asker_image) {
        this.asker_image = asker_image;
    }

    public String getAsker_id() {
        return asker_id;
    }

    public void setAsker_id(String asker_id) {
        this.asker_id = asker_id;
    }

    public int getCategory_code() {
        return category_code;
    }

    public void setCategory_code(int category_code) {
        this.category_code = category_code;
    }

    public int getNumber_of_answers() {
        return number_of_answers;
    }

    public void setNumber_of_answers(int number_of_answers) {
        this.number_of_answers = number_of_answers;
    }
}
