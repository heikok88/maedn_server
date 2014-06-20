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
    
    public static Response<MatchUpdate> newMatchUpdateResponse(String currentPlayerNickname, List<Figure> board) {
        return new Response<MatchUpdate>("matchUpdate", new MatchUpdate(currentPlayerNickname, board));
    }

    public static Response<Dice> newRollDiceResponse(String nickname, int eyes) {
        return new Response<Dice>("diceRolled", new Dice(nickname, eyes));
    }

    public static Action<Dice> newRollDiceAction(String nickname, int eyes) {
        return new Action<Dice>("diceRolled", new Dice(nickname, eyes));
    }
    
    public static Action<PlayerDone> newPlayerDoneAction(String nickname) {
        return new Action<PlayerDone>("playerDone", new PlayerDone(nickname));
    }
    
    public static Action<MatchDone> newPlayerDoneAction(List<String> nickname) {
        return new Action<MatchDone>("diceRolled", new MatchDone(nickname));
    }
}
