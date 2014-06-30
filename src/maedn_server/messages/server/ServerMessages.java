package maedn_server.messages.server;

import java.util.List;
import maedn_server.messages.Action;
import maedn_server.messages.Response;

public class ServerMessages {

    public static Response<Matches> newMatchesResponse(List<MatchNode> matches) {
        return new Response<>("matches", new Matches(matches));
    }

    public static Response<GameParticipants> newClientsResponse(String response, int matchId, List<Player> clients) {
        return new Response<>(response, new GameParticipants(matchId, clients));
    }

    public static Action<GameParticipants> newUpdatePlayersAction(int matchId, List<Player> clients) {
        return new Action<>("updatePlayers", new GameParticipants(matchId, clients));
    }
    
    public static Response<GameParticipants> newUpdatePlayersResponse(int matchId, List<Player> clients) {
        return new Response<>("updatePlayers", new GameParticipants(matchId, clients));
    }
    
    public static Response<GameParticipants> newJoinedResponse(int matchId, List<Player> clients) {
        return new Response<>("joined", new GameParticipants(matchId, clients));
    }

    public static Action<TimerStart> newTimerStartAction(int seconds) {
        return new Action<>("timerStart", new TimerStart(seconds));
    }

    public static Action<MatchUpdate> newMatchUpdateAction(String currentPlayerNickname, List<Figure> board) {
        return new Action<>("matchUpdate", new MatchUpdate(currentPlayerNickname, board));
    }
    
    public static Response<MatchUpdate> newMatchUpdateResponse(String currentPlayerNickname, List<Figure> board) {
        return new Response<>("matchUpdate", new MatchUpdate(currentPlayerNickname, board));
    }

    public static Response<Dice> newRollDiceResponse(String nickname, int eyes) {
        return new Response<>("diceRolled", new Dice(nickname, eyes));
    }

    public static Action<Dice> newRollDiceAction(String nickname, int eyes) {
        return new Action<>("diceRolled", new Dice(nickname, eyes));
    }
    
    public static Action<PlayerDone> newPlayerDoneAction(String nickname) {
        return new Action<>("playerDone", new PlayerDone(nickname));
    }
    
    public static Action<MatchDone> newMatchDoneAction(List<String> nickname) {
        return new Action<>("matchDone", new MatchDone(nickname));
    }
}
