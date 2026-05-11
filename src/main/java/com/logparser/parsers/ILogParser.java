package com.logparser.parsers;

import java.util.Map;

/**
 * Strategy interface: accumulate state via {@link #parse(String)} then materialize summaries with {@link #aggregate()}.
 */
public interface ILogParser {

    /**
     * @return {@code true} if the line was accepted and contributed to aggregation state
     */
    boolean parse(String line);

    /**
     * JSON-serializable summary for this log type. Empty maps/objects when nothing was parsed.
     */
    Map<String, Object> aggregate();
}
