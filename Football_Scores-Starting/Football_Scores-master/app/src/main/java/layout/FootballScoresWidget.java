package layout;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentProvider;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.ViewHolder;
import barqsoft.footballscores.service.myFetchService;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link FootballScoresWidgetConfigureActivity FootballScoresWidgetConfigureActivity}
 */
public class FootballScoresWidget extends AppWidgetProvider {

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        //run an background thread to query for updates while the update may not be the most up
        // to date, they will be within the update date period
        Intent service_start = new Intent(context, myFetchService.class);
        context.startService(service_start);

        CharSequence widgetText = FootballScoresWidgetConfigureActivity.loadTitlePref(context, appWidgetId);

        Date fragmentdate = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");

        String[] dateString = new String[1];
        dateString[0] = mformat.format(fragmentdate);
//get the cursor data
        Cursor cursor = context.getContentResolver().query(
                DatabaseContract.scores_table.buildScoreWithDate()
                ,null
                ,null
                ,dateString
                ,null);

        if(cursor == null)
        {
            Log.e("FOOTBALL_WIDGET", "Cursor is null");
            return;
        }
        else if(cursor.getCount() < 1)
        {
            Log.e("FOOTBALL_WIDGET", "Cursor is Empty");
            return;
        }

        // Construct the RemoteViews object
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.football_scores_widget);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.scores_list_item);


        cursor.moveToNext();
        //update the ui components here the same way we do in the bindview
        String temp= cursor.getString(COL_HOME);
        views.setTextViewText(R.id.home_name, temp);
        views.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(temp));
        views.setContentDescription(R.id.home_crest, temp);

        temp= cursor.getString(COL_AWAY);
        views.setTextViewText(R.id.away_name, temp);
        views.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(temp));
        views.setContentDescription(R.id.away_crest, temp);

        temp= cursor.getString(COL_MATCHTIME);
        views.setTextViewText(R.id.data_textview, temp);

        //get there scores
        int homeGoals = cursor.getInt(COL_HOME_GOALS);
        int awayGoals = cursor.getInt(COL_AWAY_GOALS);
        String goalString = Utilies.getScores(homeGoals,awayGoals);

        views.setTextViewText(R.id.score_textview, goalString);

        //Match id

        //team names


//        mHolder.home_name.setText(cursor.getString(COL_HOME));
//        mHolder.away_name.setText(cursor.getString(COL_AWAY));
//        mHolder.date.setText(cursor.getString(COL_MATCHTIME));
//        mHolder.score.setText(Utilies.getScores(cursor.getInt(COL_HOME_GOALS),cursor.getInt(COL_AWAY_GOALS)));
//        mHolder.match_id = cursor.getDouble(COL_ID);
//        String teamName = cursor.getString(COL_HOME);
//        mHolder.home_crest.setImageResource(Utilies.getTeamCrestByTeamName(teamName));
//        mHolder.home_crest.setContentDescription("Home Team " +teamName);
//        teamName = cursor.getString(COL_AWAY);
//        mHolder.away_crest.setImageResource(Utilies.getTeamCrestByTeamName(teamName));
//        mHolder.away_crest.setContentDescription("Away Team " +teamName);

        //Log.v(FetchScoreTask.LOG_TAG,mHolder.home_name.getText() + " Vs. " + mHolder.away_name.getText() +" id " + String.valueOf(mHolder.match_id));




       // views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            FootballScoresWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

