package com.example.avery.chamberofwizards.Events;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Events.EventsFragments.DatePickerFragment;
import com.example.avery.chamberofwizards.Events.EventsFragments.TimePickerFragment;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAnnouncementActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener {

    //XML Views
    private TextView txtEventTitle;
    private TextView txtEventDate;
    private TextView txtEventLocation;
    private Button btnAnnounceEvent;
    private Spinner spinnerAudience;
    private Toolbar postEventToolbar;
    private TextView txtEventEndDate;
    private TextView txtEventTimeStart;
    private TextView txtEventTimeEnd;
    private String databaseDateFormat;
    private EditText txtEventDescription;

    private CollectionReference eventsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference usersRef;
    String username;
    String user_image;

    private String selectedAudience;
    private String selectedMonth;
    private String selectedDay;
    private String acronymAudience;

    private final int START_DATE = 0;
    private final int END_DATE = 1;

    private final int START_TIME = 2;
    private final int END_TIME = 6;

    private int flag;
    private int timeFlag;

    private String endDate, startDate;
    Date dateAndTime;
    long timeInMillis;
    String timeInMillisString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_announcement);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        eventsRef = FirebaseFirestore.getInstance().collection("Events");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        txtEventTitle = findViewById(R.id.txtEventTitle);
        txtEventDate = findViewById(R.id.txtEventDate);
        txtEventLocation = findViewById(R.id.txtEventLocation);
        btnAnnounceEvent = findViewById(R.id.btnAnnounceEvent);
        spinnerAudience = findViewById(R.id.spinnerAudience);
        txtEventEndDate = findViewById(R.id.txtEventEndDate);
        txtEventTimeStart = findViewById(R.id.txtEventStartTime);
        txtEventTimeEnd = findViewById(R.id.txtEventEndTime);
        txtEventDescription = findViewById(R.id.txtEventDescription);

        postEventToolbar = findViewById(R.id.postAnnouncementToolbar);
        setSupportActionBar(postEventToolbar);
        getSupportActionBar().setTitle("Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.event_audiences, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAudience.setAdapter(adapter);

        spinnerAudience.setOnItemSelectedListener(this);

        /*
        txtEventDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                isFocused = true;
               if(isFocused) {
                   DialogFragment datePicker = new DatePickerFragment();
                   flag = START_DATE;
                   datePicker.show(getFragmentManager(), "date picker");
                   isFocused = false;
               }
            }
        });

        txtEventEndDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View view, boolean b) {
               b = true;
               if(b) {
                   DialogFragment datePicker = new DatePickerFragment();
                   flag = END_DATE;
                   datePicker.show(getFragmentManager(), "date picker");
                   b = false;
               }

           }
       });

         txtEventTimeStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View view, boolean b) {
               b = true;
               if(b) {
                   DialogFragment timePickerStart = new TimePickerFragment();
                   timeFlag = START_TIME;
                   timePickerStart.show(getFragmentManager(), "time picker");
                   b = false;
               }
           }
       });

       txtEventTimeEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View view, boolean b) {
               b = true;
               if(b) {
                   DialogFragment timePickerEnd = new TimePickerFragment();
                   timeFlag = END_TIME;
                   timePickerEnd.show(getFragmentManager(), "time picker");
                   b = false;
               }
               else {

               }

           }
       });

         */

        txtEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                flag = START_DATE;
                datePicker.show(getFragmentManager(), "date picker");
            }
        });

        txtEventEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                flag = END_DATE;
                datePicker.show(getFragmentManager(), "date picker");
            }
        });

        txtEventTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePickerStart = new TimePickerFragment();
                timeFlag = START_TIME;
                timePickerStart.show(getFragmentManager(), "time picker");
            }
        });

        txtEventTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePickerEnd = new TimePickerFragment();
                timeFlag = END_TIME;
                timePickerEnd.show(getFragmentManager(), "time picker");
            }
        });


        btnAnnounceEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAValidTitle() && isAValidEvenDate() && isAValidEventLocation() && isValidSelectedAudience() && isValidDescription()) {
                    //All fields are valid
                    saveToDatabase();
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

        DialogFragment dialogFragment = new DatePickerFragment();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat selectedDateFormat = new SimpleDateFormat("MMM dd, YYYY");
        SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyy/MM/dd");

        String currentDateString = selectedDateFormat.format(calendar.getTime()); //DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());

        //Getting the month and day selected for display purposes
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
        SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("dd");
        selectedMonth = simpleDateFormat.format(calendar.getTime()).toUpperCase();
        selectedDay = simpleDateFormatDay.format(calendar.getTime());


        if (flag == START_DATE) {
            txtEventDate.setText(currentDateString);
            startDate = databaseFormat.format(calendar.getTime());

            txtEventDate.setTextColor(Color.GRAY);
            btnAnnounceEvent.setEnabled(true);

            if (!TextUtils.isEmpty(txtEventEndDate.getText().toString())) {
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM-dd");

                try {
                    Date start = simpleDateFormat2.parse(startDate);
                    Date end = simpleDateFormat2.parse(endDate);

                    if (start.after(end)) {
                        txtEventDate.setTextColor(Color.RED);
                        btnAnnounceEvent.setEnabled(false);
                        Toast.makeText(this, "The start date must be before or on the end date", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (flag == END_DATE) {

            txtEventEndDate.setTextColor(Color.GRAY);
            btnAnnounceEvent.setEnabled(true);

            //Toast.makeText(this, "End date selected", Toast.LENGTH_SHORT).show();

            txtEventEndDate.setText(currentDateString);
            endDate = databaseFormat.format(calendar.getTime());

            if (!TextUtils.isEmpty(txtEventDate.getText().toString())) {
                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM-dd");
                try {
                    Date end = simpleDateFormat1.parse(endDate);
                    Date start = simpleDateFormat1.parse(startDate);

                    if (end.before(start)) {
                        //Mali
                        // Toast.makeText(this, "Before", Toast.LENGTH_SHORT).show();
                        txtEventEndDate.setTextColor(Color.RED);
                        btnAnnounceEvent.setEnabled(false);
                        Toast.makeText(this, "The end date must be on or after the start date.", Toast.LENGTH_LONG).show();
                    }
                } catch (ParseException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public boolean isEndDateValid(String startD, String endD) {
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM-dd");
        try {
            Date end = simpleDateFormat1.parse(startD);
            Date start = simpleDateFormat1.parse(endD);

            if (end.before(start)) {
                return false;
            } else if (end.before(start) || end.equals(start)) {
                return true;
            }

        } catch (ParseException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedAudience = adapterView.getItemAtPosition(i).toString();

       /*
        0. CS
        1. BS Bio
        2. BS ES
        3. BS FS
        4. BS M
        5. BSM AS
        6. BSM BA
        7. BSM CS
        */

        switch (adapterView.getSelectedItemPosition()) {
            case 1:
                selectedAudience = "College of Science";
                acronymAudience = "CS";
                break;
            case 2:
                selectedAudience = "BS Bio";
                acronymAudience = "B";
                break;
            case 3:
                selectedAudience = "BS Envi Sci";
                acronymAudience = "ES";
                break;
            case 4:
                selectedAudience = "BS Food Sci";
                acronymAudience = "FS";
                break;
            case 5:
                selectedAudience = "BS Math";
                acronymAudience = "M";
                break;
            case 6:
                selectedAudience = "BS Math AS";
                acronymAudience = "BSMAS";
                break;
            case 7:
                selectedAudience = "BS Math BA";
                acronymAudience = "BSMBA";
                break;
            case 8:
                selectedAudience = "BS Math CS";
                acronymAudience = "BSMCS";
                break;
            default:
                selectedAudience = null;
                break;
        }
        // Toast.makeText(this, selectedAudience, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public boolean isAValidTitle() {
        String title = txtEventTitle.getText().toString();

        if (!TextUtils.isEmpty(title)) {
            //May laman
            return true;
        } else {
            txtEventTitle.setError("Please enter a title for your event");
            return false;
        }
    }

    public boolean isAValidEvenDate() {
        String date = txtEventDate.getText().toString();

        if (!TextUtils.isEmpty(date)) {
            return true;
        } else {
            txtEventDate.setError("Please select a date for your event");
            return false;
        }
    }

    public boolean isAValidEventLocation() {
        String location = txtEventLocation.getText().toString();

        if (!TextUtils.isEmpty(location)) {
            return true;
        } else {
            txtEventLocation.setError("Please enter your event location");
            return false;
        }
    }

    public boolean isValidSelectedAudience() {
        if (!TextUtils.isEmpty(selectedAudience)) {
            return true;
        } else {
            Toast.makeText(this, "Please select an audience for the event", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean isValidDescription() {
        String eventDescription = txtEventDescription.getText().toString();
        if (!TextUtils.isEmpty(eventDescription)) {
            return true;
        } else {
            txtEventDescription.setError("Description is required.");
            return false;
        }
    }

    public String getCurrentUsername() {
        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username = dataSnapshot.child("username").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return username;
    }

    public String getCurrentUserImage() {
        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user_image = dataSnapshot.child("profile_image").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return user_image;
    }

    public void saveToDatabase() {
        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String image = dataSnapshot.child("profile_image").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();

                    String eventTitle, eventDate, eventLocation, eventDescription, eventEndDate, eventEndTime, eventStartTime;
                    String eventStartDateDBFormat, eventEndDateDBFormat;
                    Date eStart, eEnd;
                    String saveCurrentDate, saveCurrentTime;
                    String datePlusTime;

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-YYYY");
                    SimpleDateFormat millisFormat = new SimpleDateFormat("yyyy/MM/dd");

                    eventTitle = txtEventTitle.getText().toString();
                    eventDate = txtEventDate.getText().toString();
                    eventLocation = txtEventLocation.getText().toString();
                    eventDescription = txtEventDescription.getText().toString();
                    eventEndDate = txtEventEndDate.getText().toString();
                    eventEndTime = txtEventTimeEnd.getText().toString();
                    eventStartTime = txtEventTimeStart.getText().toString();

                    datePlusTime = startDate;
                    try {
                        dateAndTime = millisFormat.parse(datePlusTime);
                        timeInMillis = dateAndTime.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Calendar callForDate = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd", Locale.US);
                    saveCurrentDate = currentDate.format(callForDate.getTime());

                    //Getting current Time
                    Calendar callForTime = Calendar.getInstance();
                    SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                    saveCurrentTime = currentTime.format(callForTime.getTime());

                    timeInMillisString = String.valueOf(timeInMillis);

                    HashMap<String, Object> eventsMap = new HashMap<>();

                    if (txtEventDate.getText().toString().equals(txtEventEndDate.getText().toString())) {
                       //Same Day
                        //Universal
                       if(acronymAudience.equals("CS")) {
                           eventsMap.put("event_title", eventTitle);
                           eventsMap.put("event_start_date", startDate); //MM-dd-YYYY format
                           eventsMap.put("event_end_date", endDate); //MM-dd-YYYY format
                           eventsMap.put("event_start_time", eventStartTime);
                           eventsMap.put("event_end_time", eventEndTime);
                           eventsMap.put("event_location", eventLocation);
                           eventsMap.put("event_audience", selectedAudience);
                           eventsMap.put("event_description", eventDescription);
                           eventsMap.put("event_date_announced", saveCurrentDate);
                           eventsMap.put("event_time_announced", saveCurrentTime);
                           eventsMap.put("event_start_layout", txtEventDate.getText().toString()); //Display purposes
                           eventsMap.put("event_end_layout", null); //Display purposes
                           eventsMap.put("selected_month_display", selectedMonth);
                           eventsMap.put("selected_day_display", selectedDay);
                           eventsMap.put("announcer", username);
                           eventsMap.put("announcer_image", image);
                           eventsMap.put("date_time_millis", timeInMillisString);
                           eventsMap.put("acro_audience", acronymAudience);
                           eventsMap.put("universal", true);
                       }
                       else {
                           eventsMap.put("event_title", eventTitle);
                           eventsMap.put("event_start_date", startDate); //MM-dd-YYYY format
                           eventsMap.put("event_end_date", endDate); //MM-dd-YYYY format
                           eventsMap.put("event_start_time", eventStartTime);
                           eventsMap.put("event_end_time", eventEndTime);
                           eventsMap.put("event_location", eventLocation);
                           eventsMap.put("event_audience", selectedAudience);
                           eventsMap.put("event_description", eventDescription);
                           eventsMap.put("event_date_announced", saveCurrentDate);
                           eventsMap.put("event_time_announced", saveCurrentTime);
                           eventsMap.put("event_start_layout", txtEventDate.getText().toString()); //Display purposes
                           eventsMap.put("event_end_layout", null); //Display purposes
                           eventsMap.put("selected_month_display", selectedMonth);
                           eventsMap.put("selected_day_display", selectedDay);
                           eventsMap.put("announcer", username);
                           eventsMap.put("announcer_image", image);
                           eventsMap.put("date_time_millis", timeInMillisString);
                           eventsMap.put("acro_audience", acronymAudience);
                           eventsMap.put("universal", false);
                       }
                    } else {
                       if(acronymAudience.equals("CS")) {
                           eventsMap.put("event_title", eventTitle);
                           eventsMap.put("event_start_date", startDate); //MM-dd-YYYY format
                           eventsMap.put("event_end_date", endDate); //MM-dd-YYYY format
                           eventsMap.put("event_start_time", eventStartTime);
                           eventsMap.put("event_end_time", eventEndTime);
                           eventsMap.put("event_location", eventLocation);
                           eventsMap.put("event_audience", selectedAudience);
                           eventsMap.put("event_description", eventDescription);
                           eventsMap.put("event_date_announced", saveCurrentDate);
                           eventsMap.put("event_time_announced", saveCurrentTime);
                           eventsMap.put("event_start_layout", txtEventDate.getText().toString()); //Display purposes
                           eventsMap.put("event_end_layout", txtEventEndDate.getText().toString()); //Display purposes
                           eventsMap.put("selected_month_display", selectedMonth);
                           eventsMap.put("selected_day_display", selectedDay);
                           eventsMap.put("announcer", username);
                           eventsMap.put("announcer_image", image);
                           eventsMap.put("date_time_millis", timeInMillisString);
                           eventsMap.put("acro_audience", acronymAudience);
                           eventsMap.put("universal", true);
                       }
                       else {
                           eventsMap.put("event_title", eventTitle);
                           eventsMap.put("event_start_date", startDate); //MM-dd-YYYY format
                           eventsMap.put("event_end_date", endDate); //MM-dd-YYYY format
                           eventsMap.put("event_start_time", eventStartTime);
                           eventsMap.put("event_end_time", eventEndTime);
                           eventsMap.put("event_location", eventLocation);
                           eventsMap.put("event_audience", selectedAudience);
                           eventsMap.put("event_description", eventDescription);
                           eventsMap.put("event_date_announced", saveCurrentDate);
                           eventsMap.put("event_time_announced", saveCurrentTime);
                           eventsMap.put("event_start_layout", txtEventDate.getText().toString()); //Display purposes
                           eventsMap.put("event_end_layout", txtEventEndDate.getText().toString()); //Display purposes
                           eventsMap.put("selected_month_display", selectedMonth);
                           eventsMap.put("selected_day_display", selectedDay);
                           eventsMap.put("announcer", username);
                           eventsMap.put("announcer_image", image);
                           eventsMap.put("date_time_millis", timeInMillisString);
                           eventsMap.put("acro_audience", acronymAudience);
                           eventsMap.put("universal", false);
                       }
                    }

                    eventsRef.document().set(eventsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PostAnnouncementActivity.this, "Event added to the database", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PostAnnouncementActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(PostAnnouncementActivity.this, "User not in the database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");

        String selectedTime = simpleDateFormat.format(calendar.getTime());

        if (timeFlag == START_TIME) {
            txtEventTimeStart.setText(selectedTime);
        } else if (timeFlag == END_TIME) {
            txtEventTimeEnd.setText(selectedTime);
        }
    }


}
