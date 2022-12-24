package org.example;

import org.example.model.User;
import tech.ydb.table.transaction.TxControl;

public class DeleteData implements Runnable, AutoCloseable{
    final private YDBConnection YDBConnection;
    User user;

    public DeleteData(User user, YDBConnection YDBConnection) {
        this.user = user;
        this.YDBConnection = YDBConnection;
    }

    /** не рекомендуется */
    public DeleteData(User user) {
        this.user = user;
        this.YDBConnection = new YDBConnection();
    }

    private void upsertSimple(User user) {
        String query = String.format(
                            "DELETE FROM users WHERE id=%d", user.getId());

        TxControl txControl = TxControl.serializableRw().setCommitTx(true);

        YDBConnection.retryCtx.supplyResult(session -> session.executeDataQuery(query, txControl))
                .join();
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void run() {
        upsertSimple(user);
    }
}
