package maedn_server.logic;

import maedn_server.Client;

public interface IState {
    public void reveiceData(Client client, String json);
}
