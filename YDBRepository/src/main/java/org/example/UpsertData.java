package org.example;

import org.example.model.User;
import tech.ydb.table.transaction.TxControl;

public class UpsertData implements Runnable, AutoCloseable{
    final private YDBConnection YDBConnection;
    User user;

    public UpsertData(User user, YDBConnection YDBConnection) {
        this.user = user;
        this.YDBConnection = YDBConnection;
    }

    /** не рекомендуется */
    public UpsertData(User user) {
        this.user = user;
        this.YDBConnection = new YDBConnection();
    }

    private void upsertSimple(User user) {
        String query = String.format(
                        "UPSERT INTO users (id, first_name, last_name) VALUES (%d, \"%s\", \"%s\");",
                            user.getId(), user.getFirst_name(), user.getLast_name());

        TxControl txControl = TxControl.serializableRw().setCommitTx(true);

        YDBConnection.retryCtx.supplyResult(session -> session.executeDataQuery(query, txControl))
                .join().getValue();
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void run() {
        upsertSimple(user);
    }
}
