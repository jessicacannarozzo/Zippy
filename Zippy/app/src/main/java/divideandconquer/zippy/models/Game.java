package divideandconquer.zippy.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by geoff on 2017-11-27.
 */

public class Game {


    public String uid;
    public Long startTime = (long) 0.0;
    public Boolean active = false;
    public Map<String, Integer> scores = new HashMap<>();


    public Game() {
    }

    public Game(String uid, Boolean start) {
        this.uid = uid;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("timeStamp", getStartTime());
        result.put("active", active);
        result.put("scores", scores);
        return result;
    }



    public java.util.Map<String, String> getStartTime() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public Long getStartTimeLong() {
        return startTime;
    }

    @Exclude
    public String getDifferenceTimeLong() {
        long diff = (System.currentTimeMillis()) - startTime;
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(diff),
                TimeUnit.MILLISECONDS.toSeconds(diff) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diff)));
    }


}
