package org.example;

import org.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.query.DataQueryResult;
import tech.ydb.table.result.ResultSetReader;
import tech.ydb.table.transaction.TxControl;

import java.util.ArrayList;
import java.util.List;


public class SelectData implements Runnable, AutoCloseable{
    final private YDBConnection YDBConnection;
    private final SessionRetryContext retryCtx;
    private final Logger logger = LoggerFactory.getLogger(SelectData.class);
    List<User> answer;
    String query = "";

    public SelectData(YDBConnection YDBConnection) {
        this.YDBConnection = YDBConnection;
        this.retryCtx = YDBConnection.retryCtx;
    }

    public SelectData(YDBConnection YDBConnection, String query) {
        this.YDBConnection = YDBConnection;
        this.retryCtx = YDBConnection.retryCtx;
        this.query = query;
    }

    /** не рекомендуется */
    public SelectData() {
        this.YDBConnection = new YDBConnection();
        this.retryCtx = YDBConnection.retryCtx;
    }

    private void selectSimple() {
        answer = new ArrayList<>();

        String query
                = "SELECT * "
                + "FROM users";

        TxControl txControl = TxControl.serializableRw().setCommitTx(true);

        DataQueryResult result = retryCtx.supplyResult(session -> session.executeDataQuery(query, txControl))
                .join().getValue();

        logger.info("--[ SelectSimple ]--");

        ResultSetReader rs = result.getResultSet(0);
        while (rs.next()) {
            answer.add(new User((int) rs.getColumn("id").getUint64(),
                    rs.getColumn("first_name").getText(),
                    rs.getColumn("last_name").getText()));

            logger.info("read series with id {}, first_name {} and last_name {}",
                    rs.getColumn("id").getUint64(),
                    rs.getColumn("first_name").getText(),
                    rs.getColumn("last_name").getText()
            );
        }
    }

    private void selectSimple(String query) {
        answer = new ArrayList<>();

        TxControl txControl = TxControl.serializableRw().setCommitTx(true);

        DataQueryResult result = retryCtx.supplyResult(session -> session.executeDataQuery(query, txControl))
                .join().getValue();

        logger.info("--[ SelectSimple ]--");

        ResultSetReader rs = result.getResultSet(0);
        while (rs.next()) {
            answer.add(new User((int) rs.getColumn("id").getUint64(),
                    rs.getColumn("first_name").getText(),
                    rs.getColumn("last_name").getText()));

            logger.info("read series with id {}, first_name {} and last_name {}",
                    rs.getColumn("id").getUint64(),
                    rs.getColumn("first_name").getText(),
                    rs.getColumn("last_name").getText()
            );
        }
    }

    public List<User> getAnswer() {
        return answer;
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void run() {
        if (query.isEmpty()){
            selectSimple();
        }else {
            selectSimple(query);
        }
    }


}
