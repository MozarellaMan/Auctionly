package server.channels;

import java.io.Serializable;
import java.util.Optional;

public interface Cluster<T extends Serializable> {
    void startAll();

    void closeAll();

    void close(int amount);

    void add() throws Exception;

    void add(int amount) throws Exception;

    void closeOriginalClusters();

    Optional<T> get();
}
