package com.kwendatech.android.jumangapi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.*;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.*;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity implements OnDateChangedListener, OnValueChangeListener {

    private AdView mAdView;
    private RelativeLayout layName, infoHolder;
    private LinearLayout buttnHolder;
    private Button leftButton, rightButton;
    private DatePicker monthPick;
    private NumberPicker yrPick, dtPick, wkdyPick;
    private TextView wkdyDispl, furtherInfo, list;
    private int d, m, y, wd, quant, wkdyHold, dayHold, yearHold;
    private Calendar cal;
    private String tag, ya, ending, falls, times, curItt, empty, colon;
    private RelativeLayout.LayoutParams infoHolderParams;
    private NinePatchDrawable leftSelected, rightSelected;
    private ArrayList<Integer> monthTracker;
    private StringBuilder lister;

    private FrameLayout fragHolder;
    private PolicyFragment policyFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private URL privacyUrl;
    private boolean european;
    private ConsentForm consentForm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        layName = (RelativeLayout) findViewById(R.id.lay_name);
        leftButton = (Button) findViewById(R.id.left_button);
        rightButton = (Button) findViewById(R.id.right_button);
        buttnHolder = (LinearLayout) findViewById(R.id.butn_holder);
        infoHolder = (RelativeLayout) findViewById(R.id.info_holder);
        monthPick = (DatePicker) findViewById(R.id.month_pick);
        yrPick = (NumberPicker) findViewById(R.id.yrpic);
        dtPick = (NumberPicker) findViewById(R.id.dtpic);
        wkdyPick = (NumberPicker) findViewById(R.id.wkdypic);
        wkdyDispl = (TextView) findViewById(R.id.wkdy_disp);
        furtherInfo = (TextView) findViewById(R.id.further_info);
        list = (TextView) findViewById(R.id.list);
        fragHolder = (FrameLayout) findViewById(R.id.frag_holder_frame);
        leftSelected = (NinePatchDrawable) getResources().getDrawable(R.drawable.leftselected); // This version of getDrawable() is depricated, but will otherwise not be backwards compatible.
        rightSelected = (NinePatchDrawable) getResources().getDrawable(R.drawable.rightselected); // This version of getDrawable() is depricated, but will otherwise not be backwards compatible.

        policyFragment = new PolicyFragment();

        empty = getResources().getString(R.string.empty);




        // We will use the tag attribute of the layout to determine which layout the system has selected
        LayoutInflater inflater = getLayoutInflater(); // We need the inflater to use the View object in order to get the tag.
        View view = inflater.inflate(R.layout.activity_main, null); // Here is the view object we need in order to get the tag.
        tag = (String) view.getTag(); // Set the tag to that of the layout selected by the system.

        switch (tag) { // Check the tag to determine which layout has been selected by the system.
            case "extra_small_layout" : // This is the very small screen size layout.
            case "small_layout" : // This is the small screen size layout.
                // Initialize the weekday picker group to GONE
                wkdyPick.setVisibility(View.GONE);
                dtPick.setVisibility(View.GONE);
                yrPick.setVisibility(View.GONE);
                break;
            case "land_small_layout" : // This is the very small landscape screen size layout.
                // Initialize the weekday picker group to GONE
                yrPick.setVisibility(View.GONE);
                dtPick.setVisibility(View.GONE);
                wkdyPick.setVisibility(View.GONE);
                infoHolderParams = (RelativeLayout.LayoutParams) infoHolder.getLayoutParams(); // Get the layout parameters of the RelativeLayout that holds the text views.
                infoHolderParams.addRule(RelativeLayout.RIGHT_OF, R.id.month_pick); // Initialize the infoHolder to the right of month_pick.
                break;
            case "land_big_layout" : // This is the normal phone size landscape layout.
                buttnHolder.setVisibility(View.GONE);
                leftButton.setVisibility(View.GONE);
                rightButton.setVisibility(View.GONE);

            case "tablet_layout" : // This is the layout for tablets.

                break;
            case "big_layout" : // This is the normal screen size layout.

                break;
        }

        fragHolder.bringToFront(); // We have to bring the FrameLayout that holds the privacy policy fragment to the front because the translationZ XML attribute is only for api >21.


        mAdView = (AdView) findViewById(R.id.adView);

        // Initialize the privacy policy URL.
        privacyUrl = null;
        try {
            privacyUrl = new URL("https://juma-ngapi.flycricket.io/privacy.html"); // The URL where the privacy policy lives.
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "URL Malformed!", Toast.LENGTH_LONG).show();
        }



        // Use a calendar object to get the current date to use for initializing spinners
        cal = Calendar.getInstance();
        d = cal.get(Calendar.DAY_OF_MONTH);
        m = cal.get(Calendar.MONTH);
        y = cal.get(Calendar.YEAR);
        wd = cal.get(Calendar.DAY_OF_WEEK);

        // These should be initialized to the current date at onCreate().
        wkdyHold = wd;
        dayHold = d;
        yearHold = y;


        if (wkdyPick.getVisibility() == View.VISIBLE) { // Before we set the spinners we must make sure they are visible, or else the program will crash!

            // Set the weekday spinner
            wkdyPick.setMinValue(0); // This spinner will use the weekdays_array, which has 7 items.
            wkdyPick.setMaxValue(6);
            wkdyPick.setValue(wd - 1); // Initialize to the current weekday, accounting for the zero.
            wkdyPick.setDisplayedValues(getResources().getStringArray(R.array.weekdays_array)); // Make the spinner display the string values of the weekdays_array.

            // Set the day spinner
            dtPick.setMinValue(1); // The lowest possible date is 1.
            dtPick.setMaxValue(31); // The fact that not all months have 31 days is irrelevant here because we will simply iterate through the year to find which weekdays fall on this day of the month.
            dtPick.setValue(d); // Initialize to the current date.

            // Set the year spinner
            yrPick.setMinValue(1); // The lowest year this program can handle is year 1.
            yrPick.setMaxValue(100000); // The highest year this program can handle is year 9999.
            yrPick.setValue(y); // Initialize to the current year.
        } // End if statement

        // Register the onDateChangedListener by initializing the DatePicker with the current date
        monthPick.init(y, m, d, this);

        //Register the spinner listeners
        wkdyPick.setOnValueChangedListener(this);
        dtPick.setOnValueChangedListener(this);
        yrPick.setOnValueChangedListener(this);


        calculate(d, m, y, wd); // Call calculate() at startup so the sentence and the list will initialize with the values of the current date.


//**************************************************************************************************
     /*   // Geography appears as in EEA for test devices.
        ConsentInformation.getInstance(getApplicationContext()).
                setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
    */
//**************************************************************************************************



        european = ConsentInformation.getInstance(this).isRequestLocationInEeaOrUnknown();

        checkConsentNow();


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

    } //end onCreate


    // Called by the spinner listeners.
    public void onValueChange(NumberPicker p1, int p2, int p3) {
        try {
            // Retrieve the information that the user inputted into the spinners
            int day = dtPick.getValue();
            int yea = yrPick.getValue();
            int wee = wkdyPick.getValue() + 1; // We must add 1 here because the displayed value indexes are offset by 1.

            // calculate() requires that we pass it a date with the correct month. So we must determine a month that will work for the day, year, and weekday we are calculating for
            Calendar checkCal = Calendar.getInstance(); // Create a calendar object to use for this.
            int wkdyCheck; // We will need a variable to hold the weekday to check against.
            for (int m = 0; m < 12; m++) { // Use a for loop to iterate through all 12 months to find a correct one.
                checkCal.set(yea, m, day); // Set the calendar with the month of the current iteration.
                wkdyCheck = checkCal.get(checkCal.DAY_OF_WEEK); // Determine the weekday of the current iteration for checking against.
                if (wkdyCheck == wee) { // The weekday of the iteration matches the inputted weekday.
                    int mon = m; // The month of this iteration is correct, so we will be able to pass it to calculate().
                    calculate(day, mon, yea, wee); // Call calculate() and pass it the information.
                    break;
                }
            }// end for loop

            makeDarkGrey(); // Set the background to the colour of the picker being used.

            // These variables will now be used to hold the selected date while the picker group is invisible, so it can be reset to those values when it reappears.
            wkdyHold = wee;
            dayHold = day;
            yearHold = yea;

        } catch (Exception e) {
        }
    }

    // Called when the date on the DatePicker is changed.
    public void onDateChanged(DatePicker monthPick, int ye, int mo, int da) {

        int wkdy; // Create a variable to hold the weekday

        // Use a calendar object to determine the weekday
        Calendar calen = Calendar.getInstance();
        calen.set(ye, mo, da);
        wkdy = calen.get(calen.DAY_OF_WEEK);

        calculate(da, mo, ye, wkdy); // Call calculate() and pass it the information.
        makeLightGrey(); // Set the background to the colour of the picker being used.

    }


    public void calculate(int day, int month, int year, int weekdy) {
        try {
            Resources res = getResources();

           String loc = Locale.getDefault().getDisplayLanguage(); // Get the language of the device.


            // Determine whether to use "ya" in the Swahili sentence
            if (day == 1) {
                ya = res.getString(R.string.empty); // The "ya" is not used when the date is 1.
            } else{
                ya = "ya ";
            }

            //Determine which ending to put after the date in the English sentence
            switch (day) {
                case 1: case 21: case 31:
                    ending = "st";
                    break;
                case 2: case 22:
                    ending = "nd";
                    break;
                case 3: case 23:
                    ending = "rd";
                    break;
                default:
                    ending = "th";
                    break;
            }

            String [] week = res.getStringArray(R.array.weekdays_array); // Create an array and set it using the weekdays_array resource.
            String wkdyWord = week[weekdy - 1]; // Create a String set with the weekday of the date being calculated.
            wkdyDispl.setText(wkdyWord); // Set the weekday display with the calculated weekday.


            // Find all the occurences of the weekday matches in the selected year.
            String [] months = res.getStringArray(R.array.months_array); // Create an array and set it using the months_array resource.
            monthTracker = new ArrayList<Integer>(); // We will use an ArrayList for the monthTracker so it can dynamically change in size.
            Calendar countCal = Calendar.getInstance();
            Calendar checkerCal = Calendar.getInstance(); // We will use this calculator to check month lengths.
            quant = 0; // Set quant to 0 incase it has already been set.
            lister = new StringBuilder(); // Use a stringBuilder to make a multiline string for the list.
            boolean match = false; // We can use this to check if there is any match.
            int wkdyCount; // Create a variable to hold the weekday.
            for (int m = 0; m < 12; m++) { // Iterate through all 12 months to find the matches.
                checkerCal.set(year, m, 1); // Set the checkerCal with a low date that every month has.
                int length = checkerCal.getActualMaximum(Calendar.DAY_OF_MONTH); // Get the number of days in the month.
                if (day > length) { // If the date entered is more than the number of days in that month, skip that month and return directly to the top of the loop.
                    continue;
                }
                countCal.set(year, m, day); // Set the countCal using the month of the current iteration.
                wkdyCount = countCal.get(countCal.DAY_OF_WEEK);
                if (wkdyCount == weekdy) { // Check for a match.
                    // We will need to determine the language being used in order to create the dates on the list in the correct format for that language.
                    switch (loc) {
                        case "English" :
                             curItt = months[m] + " " + day + ", " + year; // Make a string of the date.
                            break;
                        case "Kiswahili" :
                            curItt = day + " " + months[m] + " " + year;
                            break;
                    }
                    lister.append(curItt + "\n"); // Add the date to the list.
                    monthTracker.add(m); // Add the month to the list for tracking, to figure out the tense later..
                    quant++; // Add to the quantity to get the count of matching dates.
                    match = true;
                }// end if
            }// end for loop
            if (match == false) {
                colon = ".";
            } else {
                colon = ":";
            }

            // Determine whether to use "time" or "times in English.
            if (quant == 1) {
                times = res.getString(R.string.time); // We use a resource here in case we need it for other languages in later releases.
            } else {
                times = res.getString(R.string.times);
            }

            // Determine the tense (past, present, or future)
            String tense = "present"; // We use "present" for initialization because we have to choose one, so we will default to the present tense if there is an error.
            if (year < y) { // The year is (and therefore all dates on the list are) in the past.
                tense = "past";
            }
            else if (year == y) {
                if (monthTracker.size() < 1) { // Make sure the length of the monthTracker is not 0, otherwise it will become -1 when checked below, and cause an error!!
                    tense = "present";
                } else { // If the length of monthTracker is 1 or more, it is safe to do these tests.
                    if (monthTracker.get(monthTracker.size() - 1) < m) { // The last month on the list is in the past.
                        tense = "past";
                    } else if (monthTracker.get(monthTracker.size() - 1) == m) { // The last month on the list is the current month.
                        if (day < d) { // The last date on the list is in the past.
                            tense = "past";
                        } else if (day == d) { // The last day on the list is today.
                            tense = "present";
                        } else if (day > d) { // The last day on the list is in the future.
                            tense = "future";
                        }
                    } else if (monthTracker.get(monthTracker.size() - 1) > m) { // The last month on the list is in the future.
                        tense = "future";
                    }
                }
            } else if (year > y) { // The year is in the future.
                tense = "future";
            }
            // Use the tense to set the tense of the sentence
            switch (tense) {
                case "past" :
                  falls =  res.getString(R.string.fell);
                    break;
                case "present" :
                  falls =  res.getString(R.string.falls);
                    break;
                case "future" :
                  falls =  res.getString(R.string.will_fall);
                    break;
                default:
                  falls =  res.getString(R.string.falls); // As mentioned before, we will default to the present tense if there is an error.
                    break;
            }

            // Build the sentence for each language
            switch (loc) { // We use a switch to determine the language
                case "English" :
                    furtherInfo.setText(String.format(res.getString(R.string.sentence), empty, day, ending, falls, wkdyWord, quant, times, year, colon)); // Set the sentence with the formatted string, passing the correct variables for the language (i.e. "empty" where necessary).
                    break;
                case "Kiswahili" :
                    furtherInfo.setText(String.format(res.getString(R.string.sentence), ya, day, empty, falls, wkdyWord, quant, empty, year, colon)); // Set the sentence with the formatted string, passing the correct variables for the language (i.e. "empty" where necessary).
                    break;
            }


            list.setText(lister); // Set the text of the list.


        }catch (Exception e) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.exception_message), Toast.LENGTH_LONG).show(); // This message will display in the unlikely event that no date is available for calculation.
        }
    }




    public void buttonLeft(View view) {
        // The left button has been touched, so we will hide the weekday picker group
        monthPick.setVisibility(View.VISIBLE);
        wkdyPick.setVisibility(View.GONE);
        dtPick.setVisibility(View.GONE);
        yrPick.setVisibility(View.GONE);
        makeLightGrey(); // Set the background colour to match the month picker
        leftButton.setBackground(leftSelected);
        rightButton.setBackgroundColor(getColour(this, R.color.blueGrey600)); // This version of getColor() is depricated, but will otherwise not be backwards compatible.
        monthGroupCalc(); // We should display the information for the selected date on this picker when it appears.
    }
    public void buttonRight(View view) {
        // The right button has been touched, so we will hide the month picker.
        wkdyPick.setVisibility(View.VISIBLE);
        dtPick.setVisibility(View.VISIBLE);
        yrPick.setVisibility(View.VISIBLE);
        monthPick.setVisibility(View.GONE);
        makeDarkGrey(); // Set the background colour to match the weekday picker group.
        rightButton.setBackground(rightSelected);
        leftButton.setBackgroundColor(getColour(this, R.color.blueGrey500)); // This version of getColor() is depricated, but will otherwise not be backwards compatible.
        spinnerSet(); // Now that the weekday picker group is visible we must set them to the correct values.
        weekdayGroupCalc(); // We should display the information for the selected date on this picker when it appears.
    }

    public void landButtonLeft(View view) {
        // The left button has been touched, so we will hide the weekday picker group
        monthPick.setVisibility(View.VISIBLE);
        wkdyPick.setVisibility(View.GONE);
        dtPick.setVisibility(View.GONE);
        yrPick.setVisibility(View.GONE);
        infoHolderParams.addRule(RelativeLayout.RIGHT_OF, R.id.month_pick); // Move the text to the right of the picker.
        infoHolderParams.removeRule(RelativeLayout.LEFT_OF);
        infoHolder.setLayoutParams(infoHolderParams); // Apply the changes to the layout.
        mAdView.bringToFront(); // Make sure the banner ad is not overlapped.
        makeLightGrey(); // Set the background colour to match the month picker.
        leftButton.setBackground(leftSelected);
        rightButton.setBackgroundColor(getColour(this, R.color.blueGrey600)); // This version of getColor() is depricated, but will otherwise not be backwards compatible.
        monthGroupCalc(); // We should display the information for the selected date on this picker when it appears.
    }
    public void landButtonRight(View view) {
        // The right button has been touched, so we will hide the month picker.
        wkdyPick.setVisibility(View.VISIBLE);
        dtPick.setVisibility(View.VISIBLE);
        yrPick.setVisibility(View.VISIBLE);
        monthPick.setVisibility(View.GONE);
        infoHolderParams.addRule(RelativeLayout.LEFT_OF, R.id.wkdypic); // Move the text to the left of the picker.
        infoHolderParams.removeRule(RelativeLayout.RIGHT_OF);
        infoHolder.setLayoutParams(infoHolderParams); // Apply the changes to the layout.
        mAdView.bringToFront(); // Make sure the banner ad is not overlapped.
        makeDarkGrey(); // Set the background colour to match the weekday picker group.
        rightButton.setBackground(rightSelected);
        leftButton.setBackgroundColor(getColour(this, R.color.blueGrey500)); // This version of getColor() is depricated, but will otherwise not be backwards compatible.
        spinnerSet(); // Now that the weekday picker group is visible we must set them to the correct values.
        weekdayGroupCalc(); // We should display the information for the selected date on this picker when it appears.
    }


    public void makeLightGrey() {
        layName.setBackgroundColor(getColour(this, R.color.blueGrey500)); // This version of getColor() is depricated, but will otherwise not be backwards compatible.
    }
    public void makeDarkGrey() {
        layName.setBackgroundColor(getColour(this, R.color.blueGrey600)); // This version of getColor() is depricated, but will otherwise not be backwards compatible.
    }


    // Use this method for getting color resources without worrying about depricated code
    public static int getColour(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public void spinnerSet() {
        if (wkdyPick.getVisibility() == View.VISIBLE) { // Before we set the spinners we must make sure they are visible
            // Set the weekday spinner
            wkdyPick.setMinValue(0); // This spinner will use the weekdays_array, which has 7 items.
            wkdyPick.setMaxValue(6);
            wkdyPick.setValue(wkdyHold - 1); // Initialize to the current or previously selected weekday, accounting for the zero.
            wkdyPick.setDisplayedValues(getResources().getStringArray(R.array.weekdays_array)); // Make the spinner display the string values of the weekdays_array.

            // Set the day spinner
            dtPick.setMinValue(1); // The lowest possible date is 1.
            dtPick.setMaxValue(31); // The fact that not all months have 31 days is irrelevant here because we will simply iterate through the year to find which weekdays fall on this day of the month.
            dtPick.setValue(dayHold); // Initialize to the current or previously selected date.

            // Set the year spinner
            yrPick.setMinValue(1); // The lowest year this program can handle is year 1.
            yrPick.setMaxValue(9999); // The highest year this program can handle is year 9999.
            yrPick.setValue(yearHold); // Initialize to the current or previously selected year.
        } // End if statement
    }

    public void weekdayGroupCalc() { // This method works the same as onValueChange().
        try {
            // Retrieve the information that the user inputted into the spinners
            int day = dtPick.getValue();
            int yea = yrPick.getValue();
            int wee = wkdyPick.getValue() + 1; // We must add 1 here because the displayed value indexes are offset by 1.

            // calculate() requires that we pass it a date with the correct month. So we must determine a month that will work for the day, year, and weekday we are calculating for
            Calendar checkCal = Calendar.getInstance(); // Create a calendar object to use for this.
            int wkdyCheck; // We will need a variable to hold the weekday to check against.
            for (int m = 0; m < 12; m++) { // Use a for loop to iterate through all 12 months to find a correct one.
                checkCal.set(yea, m, day); // Set the calendar with the month of the current iteration.
                wkdyCheck = checkCal.get(checkCal.DAY_OF_WEEK); // Determine the weekday of the current iteration for checking against.
                if (wkdyCheck == wee) { // The weekday of the iteration matches the inputted weekday.
                    int mon = m; // The month of this iteration is correct, so we will be able to pass it to calculate().
                    calculate(day, mon, yea, wee); // Call calculate() and pass it the information.
                    break;
                }
            }// end for loop

            wkdyHold = wee;
            dayHold = day;
            yearHold = yea;

        } catch (Exception e) {
        }
    }
    public void monthGroupCalc() { // This method works the same as onDateChanged().
        int wkdy; // Create a variable to hold the weekday
        int ye = monthPick.getYear();
        int mo = monthPick.getMonth();
        int da = monthPick.getDayOfMonth();

        // Use a calendar object to determine the weekday
        Calendar calen = Calendar.getInstance();
        calen.set(ye, mo, da);
        wkdy = calen.get(calen.DAY_OF_WEEK);

        calculate(da, mo, ye, wkdy); // Call calculate() and pass it the information.
    }


    public void showThePolicy(View view) { // This is called when the Privacy Policy Button is touched.
       showConsentForm(); // This will open the Google consent form for Europe, and our consent form for everywhere else.
    }

    public void hideThePolicy(View view) { // This is called by the Fragment's Close Button.
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(policyFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        fragmentTransaction.commit();
    }

    private void checkConsentNow() {
        // We need to check for the consent status before showing any ads!!!
        ConsentInformation consentInformation = ConsentInformation.getInstance(getApplicationContext());
        String[] publisherIds = {"pub-6900315854016026"}; // This ID can be found in the Settings section of our AdMob account page.
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                switch (consentStatus) {
                    case PERSONALIZED:
                        forwardPersConsent(); // This will send a personalized ad request.
                        break;
                    case NON_PERSONALIZED:
                        forwardNonPersConsent(); // This will send a non-personalized ad request.
                        break;
                    case UNKNOWN: // We need to collect consent.
                        if (!european) {
                            forwardPersConsent(); // Outside Europe, we default to personalized ads consent, rather than automatically opening the consent form or not showing ads at all.
                        }
                        else { // In Europe we must display the consent form if the consent status is unknown.
                            showConsentForm();
                        }
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String reason) {
                Toast.makeText(getApplicationContext(),"Error updating ads personalization consent.", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void showConsentForm() {
        // First we check if the user is in the EEA.
        if (european) { // The user is in Europe, or the location is unknown.
            // Set up the Google ads consent form for Europe
            consentForm = new ConsentForm.Builder(this, privacyUrl).withListener(new ConsentFormListener() {
                @Override
                public void onConsentFormLoaded() {
                    super.onConsentFormLoaded();
                    // Once it's loaded, show it!
                    consentForm.show();
                }

                @Override
                public void onConsentFormError(String reason) {
                    super.onConsentFormError(reason);
                }

                @Override
                public void onConsentFormOpened() {
                    super.onConsentFormOpened();
                }

                @Override
                public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                    super.onConsentFormClosed(consentStatus, userPrefersAdFree);
                    // Check the status of the consent on form closing.
                    switch (consentStatus) {
                        case PERSONALIZED:
                            forwardPersConsent();
                            break;
                        case NON_PERSONALIZED:
                            forwardNonPersConsent();
                            break;
                        case UNKNOWN: // We still need to collect consent.
                            showConsentForm();
                            break;
                    }
                }
            }).withPersonalizedAdsOption().withNonPersonalizedAdsOption().build(); // This makes the form include these two options.

            consentForm.load();
        }
        else { // The user is not in Europe, so we will show our own consent form, which is in the privacy policy fragment.
            // So all we have to do is display the fragment.
            // We have to use the FragmentManager so we can add the fragment dynamically.
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.frag_holder_frame, policyFragment);
            fragmentTransaction.addToBackStack(null); // The user will be able to close the fragment by hitting the back button.
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }
    }


    public void forwardPersConsent() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest); // This now requests an ad with personalized consent.
    }

    public void forwardNonPersConsent() {
        Bundle extras = new Bundle();
        extras.putString("npa", "1"); // This makes the extras bundle carry the non-personalized ads consent to forward it to Google.
        AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();
        mAdView.loadAd(adRequest); // This now requests an ad with non-personalized consent.
    }

    public void outsideEuropeStorePersConsent(View view) { // Called by the personalized consent button in the privacy fragment.
        ConsentInformation.getInstance(this).setConsentStatus(ConsentStatus.PERSONALIZED); // Set the consent locally.
        forwardPersConsent(); // Send the consent to Google.
    }

    public void outsideEuropeStoreNonPersConsent(View view) { // Called by the non-personalized consent button in the privacy fragment.
        ConsentInformation.getInstance(this).setConsentStatus(ConsentStatus.NON_PERSONALIZED); // Set the consent locally.
        forwardNonPersConsent(); // Send the consent to Google.
    }

}
