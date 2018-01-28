package divideandconquer.zippy;

import com.amulyakhare.textdrawable.util.ColorGenerator;

/**
 * Created by geoff on 2017-11-02.
 */

public class UserProfileColorService {
    private static final UserProfileColorService ourInstance = new UserProfileColorService();

    public static UserProfileColorService getInstance() {
        return ourInstance;
    }
    private ColorGenerator generator;
    private UserProfileColorService() {
         generator = ColorGenerator.DEFAULT;
    }

    public ColorGenerator getGenerator() {
        return generator;
    }
}
