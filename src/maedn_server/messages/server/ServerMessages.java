package maedn_server.messages.server;

import java.util.List;
import maedn_server.messages.Action;
import maedn_server.messages.Response;

public class ServerMessages {

    public static Response<Matches> newMatchesResponse(List<MatchNode> matches) {
        return new Response<Matches>("matches", new Matches(matches));
    }

    public static Response<GameClients> newClientsResponse(String response, int matchId, List<Client> clients) {
        return new Response<GameClients>(response, new GameClients(matchId, clients));
    }

    public static Action<GameClients> newClientsAction(int matchId, List<Client> clients) {
        return new Action<GameClients>("updatePlayers", new GameClients(matchId, clients));
    }

    public static Action<TimerStart> newTimerStartAction(int seconds) {
        return new Action<TimerStart>("timerStart", new TimerStart(seconds));
    }

    public static Action<MatchUpdate> newMatchUpdateAction(String currentPlayerNickname, List<Figure> board) {
        return new Action<MatchUpdate>("matchUpdate", new MatchUpdate(currentPlayerNickname, board));
    }

    private static Dice newDice(String nickname, int eyes) {
        return new Dice(nickname, eyes);
    }
    
    public static Response<Dice> newRollDiceResponse(String nickname, int eyes) {
        return new Response<Dice>("diceRolled", newDice(nickname, eyes));
    }

    public static Action<Dice> newRollDiceAction(String nickname, int eyes) {
        return new Action<Dice>("diceRolled", newDice(nickname, eyes));
    }
}
