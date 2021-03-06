package reddit;

import core.Filter;
import org.slf4j.Logger;
import core.Message;
import core.Component;
import twitter4j.*;

public class UserProducer extends Component {

    public static final Filter TWITTER_FILTER = new TwitterFilter();

    private final Logger log = org.slf4j.LoggerFactory.getLogger(UserProducer.class);
    private final TwitterStream twitterStream;

    public UserProducer() {
        this.twitterStream = new TwitterStreamFactory().getInstance();
    }

    private UserStreamListener listener = new UserStreamListener(){

        @Override
        public void onException(Exception e) {
            log.error(e.toString());
        }

        @Override
        public void onStatus(Status status) {
            Message message = new Message("@" + status.getUser().getScreenName() + " " + status.getText(), TWITTER_FILTER);
            getQueue().offer(message);
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

        }

        @Override
        public void onTrackLimitationNotice(int i) {

        }

        @Override
        public void onScrubGeo(long l, long l1) {

        }

        @Override
        public void onStallWarning(StallWarning stallWarning) {
            log.warn(stallWarning.toString());
            log.info("Queue size: " + getQueue().size());
        }

        @Override
        public void onDeletionNotice(long l, long l1) {

        }

        @Override
        public void onFriendList(long[] longs) {

        }

        @Override
        public void onFavorite(User user, User user1, Status status) {
            Message message = new Message("@" + user.getScreenName() + " favorited \"" + status.getText() + "\"", TWITTER_FILTER, 10 * Message.SECOND);
            getQueue().offer(message);
        }

        @Override
        public void onUnfavorite(User user, User user1, Status status) {
            Message message = new Message("@" + user.getScreenName() + " unfavorited \"" + status.getText() + "\"", TWITTER_FILTER, 10 * Message.SECOND);
            getQueue().offer(message);


        }

        @Override
        public void onFollow(User user, User user1) {
            Message message = new Message("@" + user.getScreenName() + " started following @" + user1.getScreenName(), TWITTER_FILTER, 10 * Message.SECOND);
            getQueue().offer(message);

        }

        @Override
        public void onUnfollow(User user, User user1) {
            Message message = new Message("@" + user.getScreenName() + " stopped following @" + user1.getScreenName(), TWITTER_FILTER, 10 * Message.SECOND);
            getQueue().offer(message);
        }

        @Override
        public void onDirectMessage(DirectMessage directMessage) {

        }

        @Override
        public void onUserListMemberAddition(User user, User user1, UserList userList) {

        }

        @Override
        public void onUserListMemberDeletion(User user, User user1, UserList userList) {

        }

        @Override
        public void onUserListSubscription(User user, User user1, UserList userList) {

        }

        @Override
        public void onUserListUnsubscription(User user, User user1, UserList userList) {

        }

        @Override
        public void onUserListCreation(User user, UserList userList) {

        }

        @Override
        public void onUserListUpdate(User user, UserList userList) {

        }

        @Override
        public void onUserListDeletion(User user, UserList userList) {

        }

        @Override
        public void onUserProfileUpdate(User user) {

        }

        @Override
        public void onBlock(User user, User user1) {

        }

        @Override
        public void onUnblock(User user, User user1) {

        }
    };

    @Override
    public void run() {
        log.info(this.toString() + " started.");
        this.twitterStream.addListener(listener);
        this.twitterStream.user();
    }

    @Override
    public void stop() {
        twitterStream.shutdown();
    }
}
