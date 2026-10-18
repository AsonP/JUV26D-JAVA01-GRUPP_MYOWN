package fangdjupet.game;

import fangdjupet.contract.GameEvent;
import fangdjupet.contract.GameEventListener;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Observer: tar emot händelser från Game, skriver ut dem och sparar en historik.
 */
public class EventLog implements GameEventListener {
    private final PrintStream out;
    private final List<GameEvent> history = new ArrayList<>();

    public EventLog() {
        this(System.out);
    }

    public EventLog(PrintStream out) {
        this.out = out;
    }

    @Override
    public void onEvent(GameEvent event) {
        history.add(event);
        out.println(ConsoleStyle.color("[" + event.type() + "]", ConsoleStyle.eventColor(event.type()))
                + " " + event.message());
    }

    public List<GameEvent> getHistory() {
        return Collections.unmodifiableList(history);
    }
}
