package fhacktory

import com.google.common.eventbus.Subscribe
import fhacktory.data.Quote
import fhacktory.event.SkyblogFound
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @version $Id$
 */
class Database
{
    Logger logger = LoggerFactory.getLogger(Database.class);

    Sql sql = Sql.newInstance("jdbc:postgresql://localhost:5432/sky", "postgres", "")

    @Subscribe
    void recordSkyblog(SkyblogFound e)
    {
        //logger.info("Recording skyblog {}", e.host)
        sql.withTransaction({
            def rows = sql.rows("select * from skyblogs where host = ?", [e.host])
            if (rows.isEmpty()) {
                sql.execute("insert into skyblogs (host) values (?)", [e.host])
            }
        })
    }

    void recordQuote(Quote quote)
    {
        sql.withTransaction({
            def rows = sql.rows("select * from quotes where quote = ?", [quote.content])
            if (rows.isEmpty()) {
                sql.execute("insert into quotes (quote, author) values (?, ?)", [quote.content, quote.author])
            }
        })
    }

    List<String> hosts()
    {
        def rows = sql.rows("select * from skyblogs")
        return rows.collect({ GroovyRowResult row ->
            row.getProperty("host")
        })
    }
}
