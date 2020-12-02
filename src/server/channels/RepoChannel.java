package server.channels;

import java.io.Serializable;

public interface RepoChannel<T extends Serializable & RepoInterface> {
    void start() throws Exception;

    void close();

    boolean isOpen();

    T getState();
}
