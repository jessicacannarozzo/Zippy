package divideandconquer.zippy.Data;

import com.google.gson.GsonBuilder;
import com.ning.http.client.AsyncHttpClient;

import org.restonfire.BaseFirebaseRestDatabaseFactory;

/**
 * Created by navi on 1/27/18.
 * using wrapper: https://github.com/j-fischer/rest-on-fire
 */


public class Database {
    BaseFirebaseRestDatabaseFactory factory = new BaseFirebaseRestDatabaseFactory(
            new AsyncHttpClient(),
            new GsonBuilder().create()
    );

    Database() {

    }

}
