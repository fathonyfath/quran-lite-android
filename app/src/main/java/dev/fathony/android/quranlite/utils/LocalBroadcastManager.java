package dev.fathony.android.quranlite.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public final class LocalBroadcastManager {
    static final int MSG_EXEC_PENDING_BROADCASTS = 1;
    private static final String TAG = "LocalBroadcastManager";
    private static final boolean DEBUG = false;
    private static final Object mLock = new Object();
    private static LocalBroadcastManager mInstance;
    private final Context mAppContext;
    private final HashMap<BroadcastReceiver, ArrayList<ReceiverRecord>> mReceivers
            = new HashMap<>();
    private final HashMap<String, ArrayList<ReceiverRecord>> mActions = new HashMap<>();
    private final ArrayList<BroadcastRecord> mPendingBroadcasts = new ArrayList<>();
    private final Handler mHandler;

    private LocalBroadcastManager(Context context) {
        mAppContext = context;
        mHandler = new Handler(context.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_EXEC_PENDING_BROADCASTS:
                        executePendingBroadcasts();
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };
    }

    public static LocalBroadcastManager getInstance(Context context) {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new LocalBroadcastManager(context.getApplicationContext());
            }
            return mInstance;
        }
    }

    public void registerReceiver(BroadcastReceiver receiver,
                                 IntentFilter filter) {
        synchronized (mReceivers) {
            ReceiverRecord entry = new ReceiverRecord(filter, receiver);
            ArrayList<ReceiverRecord> filters = mReceivers.get(receiver);
            if (filters == null) {
                filters = new ArrayList<>(1);
                mReceivers.put(receiver, filters);
            }
            filters.add(entry);
            for (int i = 0; i < filter.countActions(); i++) {
                String action = filter.getAction(i);
                ArrayList<ReceiverRecord> entries = mActions.get(action);
                if (entries == null) {
                    entries = new ArrayList<ReceiverRecord>(1);
                    mActions.put(action, entries);
                }
                entries.add(entry);
            }
        }
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {
        synchronized (mReceivers) {
            final ArrayList<ReceiverRecord> filters = mReceivers.remove(receiver);
            if (filters == null) {
                return;
            }
            for (int i = filters.size() - 1; i >= 0; i--) {
                final ReceiverRecord filter = filters.get(i);
                filter.dead = true;
                for (int j = 0; j < filter.filter.countActions(); j++) {
                    final String action = filter.filter.getAction(j);
                    final ArrayList<ReceiverRecord> receivers = mActions.get(action);
                    if (receivers != null) {
                        for (int k = receivers.size() - 1; k >= 0; k--) {
                            final ReceiverRecord rec = receivers.get(k);
                            if (rec.receiver == receiver) {
                                rec.dead = true;
                                receivers.remove(k);
                            }
                        }
                        if (receivers.size() <= 0) {
                            mActions.remove(action);
                        }
                    }
                }
            }
        }
    }

    public boolean sendBroadcast(Intent intent) {
        synchronized (mReceivers) {
            final String action = intent.getAction();
            final String type = intent.resolveTypeIfNeeded(
                    mAppContext.getContentResolver());
            final Uri data = intent.getData();
            final String scheme = intent.getScheme();
            final Set<String> categories = intent.getCategories();

            final boolean debug = DEBUG ||
                    ((intent.getFlags() & Intent.FLAG_DEBUG_LOG_RESOLUTION) != 0);
            if (debug) Log.v(
                    TAG, "Resolving type " + type + " scheme " + scheme
                            + " of intent " + intent);

            ArrayList<ReceiverRecord> entries = mActions.get(intent.getAction());
            if (entries != null) {
                if (debug) Log.v(TAG, "Action list: " + entries);

                ArrayList<ReceiverRecord> receivers = null;
                for (int i = 0; i < entries.size(); i++) {
                    ReceiverRecord receiver = entries.get(i);
                    if (debug) Log.v(TAG, "Matching against filter " + receiver.filter);

                    if (receiver.broadcasting) {
                        if (debug) {
                            Log.v(TAG, "  Filter's target already added");
                        }
                        continue;
                    }

                    int match = receiver.filter.match(action, type, scheme, data,
                            categories, "LocalBroadcastManager");
                    if (match >= 0) {
                        if (debug) Log.v(TAG, "  Filter matched!  match=0x" +
                                Integer.toHexString(match));
                        if (receivers == null) {
                            receivers = new ArrayList<ReceiverRecord>();
                        }
                        receivers.add(receiver);
                        receiver.broadcasting = true;
                    } else {
                        if (debug) {
                            String reason;
                            switch (match) {
                                case IntentFilter.NO_MATCH_ACTION:
                                    reason = "action";
                                    break;
                                case IntentFilter.NO_MATCH_CATEGORY:
                                    reason = "category";
                                    break;
                                case IntentFilter.NO_MATCH_DATA:
                                    reason = "data";
                                    break;
                                case IntentFilter.NO_MATCH_TYPE:
                                    reason = "type";
                                    break;
                                default:
                                    reason = "unknown reason";
                                    break;
                            }
                            Log.v(TAG, "  Filter did not match: " + reason);
                        }
                    }
                }

                if (receivers != null) {
                    for (int i = 0; i < receivers.size(); i++) {
                        receivers.get(i).broadcasting = false;
                    }
                    mPendingBroadcasts.add(new BroadcastRecord(intent, receivers));
                    if (!mHandler.hasMessages(MSG_EXEC_PENDING_BROADCASTS)) {
                        mHandler.sendEmptyMessage(MSG_EXEC_PENDING_BROADCASTS);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public void sendBroadcastSync(Intent intent) {
        if (sendBroadcast(intent)) {
            executePendingBroadcasts();
        }
    }

    @SuppressWarnings("WeakerAccess")
    void executePendingBroadcasts() {
        while (true) {
            final BroadcastRecord[] brs;
            synchronized (mReceivers) {
                final int N = mPendingBroadcasts.size();
                if (N <= 0) {
                    return;
                }
                brs = new BroadcastRecord[N];
                mPendingBroadcasts.toArray(brs);
                mPendingBroadcasts.clear();
            }
            for (int i = 0; i < brs.length; i++) {
                final BroadcastRecord br = brs[i];
                final int nbr = br.receivers.size();
                for (int j = 0; j < nbr; j++) {
                    final ReceiverRecord rec = br.receivers.get(j);
                    if (!rec.dead) {
                        rec.receiver.onReceive(mAppContext, br.intent);
                    }
                }
            }
        }
    }

    private static final class ReceiverRecord {
        final IntentFilter filter;
        final BroadcastReceiver receiver;
        boolean broadcasting;
        boolean dead;

        ReceiverRecord(IntentFilter _filter, BroadcastReceiver _receiver) {
            filter = _filter;
            receiver = _receiver;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(128);
            builder.append("Receiver{");
            builder.append(receiver);
            builder.append(" filter=");
            builder.append(filter);
            if (dead) {
                builder.append(" DEAD");
            }
            builder.append("}");
            return builder.toString();
        }
    }

    private static final class BroadcastRecord {
        final Intent intent;
        final ArrayList<ReceiverRecord> receivers;

        BroadcastRecord(Intent _intent, ArrayList<ReceiverRecord> _receivers) {
            intent = _intent;
            receivers = _receivers;
        }
    }
}
