package com.example.will.howmanydaysuntilmybirthday;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Character.isDigit;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.will.howmanydaysuntilmybirthday.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void doStuff(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);

        EditText editText = (EditText) findViewById(R.id.editText);
        String inputDate = editText.getText().toString();

        String message = "Error. I screwed up, sorry.";

        int validKey = validInput(inputDate);   //Check for correct syntax: 0 for correct, 1 for incorrect, 2 for empty

        if(validKey != 0){      // Deal with incorrect/empty input
            if(validKey == 1)   // For incorrect
                message = "You gotta put in a real date!";
            else                // For empty
                message = "You gotta enter a day!";
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
            return;
        }

        int[] bday = convertToDate(inputDate);

        if(!validDate(bday)) {
            message = "That's not a real day!";
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
            return;
        }

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String currDateString = df.format(Calendar.getInstance().getTime());
        int[] today = convertToDate(currDateString);

        int daysUntil = calcDaysUntil(bday, today);



        Random rand = new Random();
        int x = rand.nextInt(3);
        if(daysUntil == 0) {
            if(x == 0 || x == 1)
                message = "Today's your birthday! Sweet!!!";
            else
                message = "That's today! Happy birthday!!!";
        }
        else if(daysUntil == 1)
            message = "Your birthday's tomorrow! Cool!";
        else if (daysUntil == 364)
            message = "Your birthday was yesterday! Happy belated birthday!";
        else if(daysUntil > 200)
            if(x == 0 || x == 1)
                message = "Looks like you still have " + daysUntil + " days until your birthday. That's quite a while.";
            else
                message = "You still have " + daysUntil + " days until your birthday.";
        else if(daysUntil < 14)
            message = "You only have " + daysUntil + " days until your birthday! That's almost here!!" ;
        else {
            if(x == 0)
                message = "You have " + daysUntil + " days until your birthday. Hot dog!";
            else if(x == 1)
                message = "You have " + daysUntil + " days until your big day! Wowwie!";
            else if(x == 2)
                message = "There are " + daysUntil + " days until your birthday. Glad we got that figured out.";
        }

        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public static int validInput(String date){  // Check if input string uses valid syntax, return 0 for correct, 1 for incorrect, 2 for empty
        if(date.isEmpty())  // For empty
            return 2;
        if(!isDigit(date.charAt(0)) || !isDigit(date.charAt(date.length() - 1)))    // Make sure first and last characters are digits
            return 1;
        int charStreak = 1;
        int dividerStreak = 0;
        int dividers = 0;
        for(int i = 1; i < date.length(); i++){
            if(isDigit(date.charAt(i))) {
                charStreak++;
                if(charStreak > 4 || (dividers < 2 && charStreak > 2)){ // Check syntax with streaks: No digit streaks longer than 4, no digit streaks longer than 2 except for year
                    return 1;
                }
                dividerStreak = 0;
            }
            else {
                dividerStreak++;
                if(dividerStreak > 1)   // No two dividers in a row
                    return 1;
                charStreak = 0;
                dividers++;
            }
        }
        if(dividers == 0 || dividers > 2)   // Check for proper number of dividers: 1 day, 1 month, 1 year (optional)
            return 1;
        else
            return 0;
    }

    public static int[] convertToDate(String rawDate){
        String curr = "";
        int fixedDate[] = new int[3];
        int pos = 0;
        fixedDate[2] = -1;   // Year set to non-leap year if not given
        for(int i = 0; i < rawDate.length(); i++){
            if(isDigit(rawDate.charAt(i)))
                curr += rawDate.charAt(i);
            else{
                fixedDate[pos] = Integer.parseInt(curr);
                curr = "";
                pos++;
            }
        }
        fixedDate[pos] = Integer.parseInt(curr);
        return fixedDate;
    }

    public static boolean validDate(int[] date){    // Check if input date is a real date
        if(date[0] < 1 || date[0] > 12)
            return false;
        if(date[2] == -1)
            date[2] = -4;    // If year is not specified set to leap year by default
        if(date[1] < 1 || date[1] > daysInMonth(date[0], date[2]))
            return false;
        return true;
    }

    public static int calcDaysUntil(int[] bday, int[] today) {
        int leftThisMonth = daysInMonth(today[0], today[2]) - today[1];
        if(today[0] == bday[0] && today[1] <= bday[1])  // Case: Birthday is later this month
            return bday[1] - today[1];
        else if(today[0] == bday[0])    // Case: Birthday was earlier this month
            return 365 - (today[1] - bday[1]);
        else if(today[0] < bday[0]) {   // Case: Birthday is later this year
            int n = bday[0] - 1;
            int total = bday[1] + leftThisMonth;
            while(today[0] < n) {
                total = total + daysInMonth(n, today[2]);
                n = n - 1;
            }
            return total;
        }
        else {  // Case: Birthday already past this year
            int total = bday[1] + leftThisMonth;
            int n = today[0] + 1;
            while(n <= 12) {
                total = total + daysInMonth(n, today[2]);
                n = n + 1;
            }
            n = 1;
            while(n < bday[0]) {
                total = total + daysInMonth(n, today[2] + 1);
                n = n + 1;
            }
            return total;
        }
    }

    public static int daysInMonth(int m, int y) {
        if(m == 4 || m == 6 || m == 9 || m == 11)
            return 30;
        else if(m == 2 && (y % 4 == 0) && (y % 400 == 0 || y % 100 != 0))
            return 29;
        else if(m == 2)
            return 28;
        else
            return 31;
    }
}
