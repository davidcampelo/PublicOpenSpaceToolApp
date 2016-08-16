package org.davidcampelo.post;

/**
 * Interface of notification from Menu (MaiActivity) to PublicOpenListFragment
 * to notify when user want to wipe out all data from the Database
 *
 * {@deprecated}
 * Created by davidcampelo on 8/10/16.
 */
public interface ResetDataListener {

    void notifyReset();
}
