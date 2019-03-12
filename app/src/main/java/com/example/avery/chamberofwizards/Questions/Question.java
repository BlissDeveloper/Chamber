package com.example.avery.chamberofwizards.Questions;

public class Question {
   /*
                0. Respondent
                1. Date
                2. Time
                3. is_best_answer
                4. Upvotes
                5. Respondent Image
                6. Respondent Course
                7. Answer
                8. Badge
                9. best_answer_rating
                 */

    String respondent, date, time, respondent_image, respondent_course, answer, badge, answer_image, respondent_id, question_id, answer_id;
    int number_of_upvotes, number_of_answers;
    float best_answer_rating;
    int is_best_answer;

    public Question() {

    }

    public Question(String respondent, String date, String time, String respondent_image, String respondent_course, String answer, String badge, String answer_image, int number_of_upvotes, float best_answer_rating, int is_best_answer, int number_of_answers, String respondent_id, String question_id, String answer_id) {
        this.respondent = respondent;
        this.date = date;
        this.time = time;
        this.respondent_image = respondent_image;
        this.respondent_course = respondent_course;
        this.answer = answer;
        this.badge = badge;
        this.answer_image = answer_image;
        this.number_of_upvotes = number_of_upvotes;
        this.best_answer_rating = best_answer_rating;
        this.is_best_answer = is_best_answer;
        this.number_of_answers = number_of_answers;
        this.question_id = question_id;
        this.answer_id = answer_id;
    }

    public String getRespondent() {
        return respondent;
    }

    public void setRespondent(String respondent) {
        this.respondent = respondent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRespondent_image() {
        return respondent_image;
    }

    public void setRespondent_image(String respondent_image) {
        this.respondent_image = respondent_image;
    }

    public String getRespondent_course() {
        return respondent_course;
    }

    public void setRespondent_course(String respondent_course) {
        this.respondent_course = respondent_course;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getAnswer_image() {
        return answer_image;
    }

    public void setAnswer_image(String answer_image) {
        this.answer_image = answer_image;
    }

    public int getNumber_of_upvotes() {
        return number_of_upvotes;
    }

    public void setNumber_of_upvotes(int number_of_upvotes) {
        this.number_of_upvotes = number_of_upvotes;
    }

    public float getBest_answer_rating() {
        return best_answer_rating;
    }

    public void setBest_answer_rating(float best_answer_rating) {
        this.best_answer_rating = best_answer_rating;
    }

    public int isIs_best_answer() {
        return is_best_answer;
    }

    public void setIs_best_answer(int is_best_answer) {
        this.is_best_answer = is_best_answer;
    }

    public void setNumber_of_answers(int number_of_answers) {
        this.number_of_answers = number_of_answers;
    }

    public int getNumber_of_answers() {
        return number_of_answers;
    }

    public String getRespondent_id() {
        return respondent_id;
    }

    public void setRespondent_id(String respondent_id) {
        this.respondent_id = respondent_id;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(String answer_id) {
        this.answer_id = answer_id;
    }
}
